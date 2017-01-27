package org.code_house.maven.osgi.resolver.strict;

import org.code_house.maven.osgi.resolver.shared.CustomVersionRangeResolver;
import org.codehaus.plexus.component.annotations.Component;
import org.eclipse.aether.impl.MetadataResolver;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.spi.log.LoggerFactory;

import javax.inject.Inject;

@Component(role = VersionRangeResolver.class)
public class StrictOsgiVersionRangeResolver extends CustomVersionRangeResolver {

    public StrictOsgiVersionRangeResolver() {
        // enable default constructor
    }

    @Inject
    public StrictOsgiVersionRangeResolver(MetadataResolver metadataResolver, SyncContextFactory syncContextFactory, RepositoryEventDispatcher repositoryEventDispatcher, LoggerFactory loggerFactory) {
        super(metadataResolver, syncContextFactory, repositoryEventDispatcher, loggerFactory);
    }

}
