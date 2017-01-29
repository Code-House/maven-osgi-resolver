/*
 * (C) Copyright 2017 Code-House, Łukasz Dywicki.
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
package org.code_house.maven.resolver.compatible;

import org.code_house.maven.osgi.resolver.shared.CustomVersionRangeResolver;
import org.code_house.maven.osgi.resolver.shared.version.OsgiVersionScheme;
import org.codehaus.plexus.component.annotations.Component;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.MetadataResolver;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.log.LoggerFactory;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;
import org.eclipse.aether.version.VersionScheme;
import org.osgi.framework.VersionRange;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component(role = VersionRangeResolver.class)
public class CompatibleOsgiVersionRangeResolver extends CustomVersionRangeResolver {

    public CompatibleOsgiVersionRangeResolver() {
        // enable default constructor
    }

    @Inject
    public CompatibleOsgiVersionRangeResolver(MetadataResolver metadataResolver, SyncContextFactory syncContextFactory, RepositoryEventDispatcher repositoryEventDispatcher, LoggerFactory loggerFactory) {
        super(metadataResolver, syncContextFactory, repositoryEventDispatcher, loggerFactory);
    }

    public VersionRangeResult resolveVersionRange(RepositorySystemSession session, VersionRangeRequest request) throws VersionRangeResolutionException {
        VersionRangeResult result = new VersionRangeResult(request);

        VersionScheme osgiVersionScheme = new OsgiVersionScheme();
        VersionScheme regularVersionScheme = new GenericVersionScheme();

        VersionConstraint versionConstraint;
        try {
            versionConstraint = osgiVersionScheme.parseVersionConstraint(request.getArtifact().getVersion());
        } catch (InvalidVersionSpecificationException e) {
            result.addException(e);
            throw new VersionRangeResolutionException(result);
        }

        if (versionConstraint.getRange() == null) { // exact version is given
            result.setVersionConstraint(versionConstraint);
            result.addVersion(versionConstraint.getVersion());
        } else {
            try {
                versionConstraint = osgiVersionScheme.parseVersionConstraint(request.getArtifact().getVersion());
            } catch (InvalidVersionSpecificationException e) {
                result.addException(e);
                throw new VersionRangeResolutionException(result);
            }
            result.setVersionConstraint(versionConstraint);

            Map<String, ArtifactRepository> versionIndex = getVersions(session, result, request);

            List<Version> versions = new ArrayList<>();
            for (Map.Entry<String, ArtifactRepository> v : versionIndex.entrySet()) {
                try {
                    Version ver = osgiVersionScheme.parseVersion(v.getKey());
                    if (versionConstraint.containsVersion(ver)) {
                        versions.add(ver);
                        result.setRepository(ver, v.getValue());
                    }
                } catch (InvalidVersionSpecificationException e) {
                    result.addException(e);
                }
            }

            Collections.sort(versions);
            result.setVersions(versions);
        }

        return result;
    }

    public static void main(String[] args) {
        VersionRange versionRange = new VersionRange("2.0.0");
        System.out.println(versionRange.getLeft() + " " + versionRange.getLeftType() + " " + versionRange.getRight() + " " + versionRange.getRightType());
    }
}
