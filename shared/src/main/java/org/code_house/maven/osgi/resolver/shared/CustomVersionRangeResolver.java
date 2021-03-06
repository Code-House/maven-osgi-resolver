/*
 * (C) Copyright ${year} Code-House, Łukasz Dywicki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.code_house.maven.osgi.resolver.shared;

import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.IOUtil;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.impl.MetadataResolver;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.metadata.DefaultMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.spi.log.Logger;
import org.eclipse.aether.spi.log.LoggerFactory;
import org.eclipse.aether.spi.log.NullLoggerFactory;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class CustomVersionRangeResolver implements VersionRangeResolver, Service {

    protected static final String MAVEN_METADATA_XML = "maven-metadata.xml";

    private Logger logger = NullLoggerFactory.LOGGER;

    private MetadataResolver metadataResolver;

    private SyncContextFactory syncContextFactory;

    private RepositoryEventDispatcher repositoryEventDispatcher;

    public CustomVersionRangeResolver() {
        // enable default constructor
    }

    @Inject
    public CustomVersionRangeResolver(MetadataResolver metadataResolver, SyncContextFactory syncContextFactory, RepositoryEventDispatcher repositoryEventDispatcher, LoggerFactory loggerFactory) {
        setMetadataResolver(metadataResolver);
        setSyncContextFactory(syncContextFactory);
        setLoggerFactory(loggerFactory);
        setRepositoryEventDispatcher(repositoryEventDispatcher);
    }

    public void initService(ServiceLocator locator) {
        this.setLoggerFactory((LoggerFactory)locator.getService(LoggerFactory.class));
        this.setMetadataResolver((MetadataResolver)locator.getService(MetadataResolver.class));
        this.setSyncContextFactory((SyncContextFactory)locator.getService(SyncContextFactory.class));
        this.setRepositoryEventDispatcher((RepositoryEventDispatcher)locator.getService(RepositoryEventDispatcher.class));
    }

    public final void setLoggerFactory(LoggerFactory loggerFactory) {
        this.logger = NullLoggerFactory.getSafeLogger(loggerFactory, getClass());
    }

    public final CustomVersionRangeResolver setMetadataResolver(MetadataResolver metadataResolver) {
        this.metadataResolver = Objects.requireNonNull(metadataResolver, "metadataResolver cannot be null");
        return this;
    }

    public final CustomVersionRangeResolver setSyncContextFactory(SyncContextFactory syncContextFactory) {
        this.syncContextFactory = Objects.requireNonNull(syncContextFactory, "syncContextFactory cannot be null");
        return this;
    }

    public final CustomVersionRangeResolver setRepositoryEventDispatcher(RepositoryEventDispatcher repositoryEventDispatcher) {
        this.repositoryEventDispatcher = Objects.requireNonNull(repositoryEventDispatcher,  "repositoryEventDispatcher cannot be null");
        return this;
    }

    protected final Map<String, ArtifactRepository> getVersions(RepositorySystemSession session, VersionRangeResult result, VersionRangeRequest request) {
        RequestTrace trace = RequestTrace.newChild(request.getTrace(), request);

        Map<String, ArtifactRepository> versionIndex = new HashMap<>();

        Metadata metadata =
            new DefaultMetadata(request.getArtifact().getGroupId(), request.getArtifact().getArtifactId(), MAVEN_METADATA_XML, Metadata.Nature.RELEASE_OR_SNAPSHOT);

        List<MetadataRequest> metadataRequests = new ArrayList<>(request.getRepositories().size());

        metadataRequests.add(new MetadataRequest(metadata, null, request.getRequestContext()));

        for (RemoteRepository repository : request.getRepositories()) {
            MetadataRequest metadataRequest = new MetadataRequest(metadata, repository, request.getRequestContext());
            metadataRequest.setDeleteLocalCopyIfMissing(true);
            metadataRequest.setTrace(trace);
            metadataRequests.add(metadataRequest);
        }

        List<MetadataResult> metadataResults = metadataResolver.resolveMetadata(session, metadataRequests);

        WorkspaceReader workspace = session.getWorkspaceReader();
        if (workspace != null) {
            List<String> versions = workspace.findVersions(request.getArtifact());
            for (String version : versions) {
                versionIndex.put(version, workspace.getRepository());
            }
        }

        for (MetadataResult metadataResult : metadataResults) {
            result.addException(metadataResult.getException());

            ArtifactRepository repository = metadataResult.getRequest().getRepository();
            if (repository == null) {
                repository = session.getLocalRepository();
            }

            Versioning versioning = readVersions(session, trace, metadataResult.getMetadata(), repository, result);
            for (String version : versioning.getVersions()) {
                if (!versionIndex.containsKey(version)) {
                    versionIndex.put(version, repository);
                }
            }
        }

        return versionIndex;
    }

    private Versioning readVersions(RepositorySystemSession session, RequestTrace trace, Metadata metadata, ArtifactRepository repository, VersionRangeResult result) {
        Versioning versioning = null;

        FileInputStream fis = null;
        try {
            if (metadata != null) {

                try (SyncContext syncContext = syncContextFactory.newInstance(session, true)) {
                    syncContext.acquire(null, Collections.singleton(metadata));

                    if (metadata.getFile() != null && metadata.getFile().exists()) {
                        fis = new FileInputStream(metadata.getFile());
                        org.apache.maven.artifact.repository.metadata.Metadata m = new MetadataXpp3Reader().read(fis, false);
                        versioning = m.getVersioning();
                    }
                }
            }
        } catch (Exception e) {
            invalidMetadata(session, trace, metadata, repository, e);
            result.addException(e);
        } finally {
            IOUtil.close(fis);
        }

        return (versioning != null) ? versioning : new Versioning();
    }

    private void invalidMetadata(RepositorySystemSession session, RequestTrace trace, Metadata metadata, ArtifactRepository repository, Exception exception) {
        RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.METADATA_INVALID);
        event.setTrace(trace);
        event.setMetadata(metadata);
        event.setException(exception);
        event.setRepository(repository);

        repositoryEventDispatcher.dispatch(event.build());
    }

}