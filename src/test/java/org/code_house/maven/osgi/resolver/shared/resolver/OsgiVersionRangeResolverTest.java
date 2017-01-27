package org.code_house.maven.osgi.resolver;

import org.code_house.maven.osgi.resolver.version.OsgiVersion;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class OsgiVersionRangeResolverTest extends AbstractRepositoryTestCase {

    private OsgiVersionRangeResolver versionRangeResolver;

    public OsgiVersionRangeResolverTest() {

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        versionRangeResolver = (OsgiVersionRangeResolver) lookup(VersionRangeResolver.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        versionRangeResolver = null;
    }

    @Test
    public void testResolveRange() throws Exception {
        VersionRangeRequest requestB = new VersionRangeRequest();
        requestB.addRepository( newTestRepository() );
        Artifact artifactB = new DefaultArtifact( "org.apache.maven.its:dep-mng-range:[3.0,4)");
        requestB.setArtifact( artifactB );

//        1-SNAPSHOT
//        1
//        1.0.0.29-SNAPSHOT
//        2.0.0-SNAPSHOT
//        3.0.1-SNAPSHOT
//        3.0.3-SNAPSHOT
//        3.0.4-SNAPSHOT
//        3.0.5
//        4.0.0-SNAPSHOT
//        4.0.0-alpha2

        VersionRangeResult resultB = versionRangeResolver.resolveVersionRange( session, requestB );
        List<Version> versions = resultB.getVersions();
        assertEquals(4, versions.size());

        LinkedList<String> resolved = new LinkedList<>();
        for (Version version : versions) {
            if (version instanceof OsgiVersion) {
                resolved.add(((OsgiVersion) version).getRawVersion());
            } else {
                resolved.add(version.toString());
            }
        }

        Iterator<String> iterator = resolved.iterator();
        assertEquals( "3.0.1-SNAPSHOT", iterator.next());
        assertEquals( "3.0.3-SNAPSHOT", iterator.next());
        assertEquals( "3.0.4-SNAPSHOT", iterator.next());
        assertEquals( "3.0.5", iterator.next());
        assertEquals( "3.0.5", resolved.getLast());

    }

    @Test
    public void testResolveStaticVersion() throws Exception {
        VersionRangeRequest requestB = new VersionRangeRequest();
        requestB.addRepository( newTestRepository() );
        Artifact artifactB = new DefaultArtifact( "org.apache.maven.its:dep-mng-range:3.0.5");
        requestB.setArtifact( artifactB );

        VersionRangeResult resultB = versionRangeResolver.resolveVersionRange( session, requestB );
        List<Version> versions = resultB.getVersions();
        System.out.println(versions);
        assertEquals(1, versions.size());

        LinkedList<String> resolved = new LinkedList<>();
        for (Version version : versions) {
            resolved.add(version.toString());
        }

        assertEquals( "3.0.5", resolved.getLast());

    }

}