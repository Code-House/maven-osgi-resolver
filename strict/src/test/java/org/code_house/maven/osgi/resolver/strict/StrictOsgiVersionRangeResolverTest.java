package org.code_house.maven.osgi.resolver.strict;

import org.code_house.maven.osgi.resolver.test.AbstractRepositoryTestCase;
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

public class StrictOsgiVersionRangeResolverTest extends AbstractRepositoryTestCase {

    private StrictOsgiVersionRangeResolver versionRangeResolver;

    public StrictOsgiVersionRangeResolverTest() {

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        versionRangeResolver = (StrictOsgiVersionRangeResolver) lookup(VersionRangeResolver.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        versionRangeResolver = null;
    }

    @Test
    public void testResolveRange() throws Exception {
        VersionRangeRequest requestB = new VersionRangeRequest();
        requestB.addRepository(newTestRepository());
        Artifact artifactB = new DefaultArtifact("org.apache.maven.its:dep-mng-range:[3.0,4)");
        requestB.setArtifact(artifactB);

        VersionRangeResult resultB = versionRangeResolver.resolveVersionRange(session, requestB);
        List<Version> versions = resultB.getVersions();
        assertEquals(4, versions.size());

        LinkedList<String> resolved = new LinkedList<>();
        for (Version version : versions) {
            resolved.add(version.toString());
        }

        Iterator<String> iterator = resolved.iterator();
        assertEquals("3.0.1-SNAPSHOT", iterator.next());
        assertEquals("3.0.3-SNAPSHOT", iterator.next());
        assertEquals("3.0.4-SNAPSHOT", iterator.next());
        assertEquals("3.0.5", iterator.next());
        assertEquals("3.0.5", resolved.getLast());

    }

    @Test
    public void testResolveStaticVersion() throws Exception {
        VersionRangeRequest requestB = new VersionRangeRequest();
        requestB.addRepository(newTestRepository());
        Artifact artifactB = new DefaultArtifact("org.apache.maven.its:dep-mng-range:3.0.5");
        requestB.setArtifact(artifactB);

        VersionRangeResult resultB = versionRangeResolver.resolveVersionRange(session, requestB);
        List<Version> versions = resultB.getVersions();

        assertEquals(5, versions.size());

        LinkedList<String> resolved = new LinkedList<>();
        for (Version version : versions) {
            resolved.add(version.toString());
        }

        Iterator<String> iterator = resolved.iterator();
        assertEquals("3.0.5", iterator.next());
        assertEquals("4.0.0", iterator.next());
        assertEquals("4.0.0-SNAPSHOT", iterator.next());
        assertEquals("4.0.0-rc1", iterator.next());
        assertEquals("4.0.0-rc1-SNAPSHOT", iterator.next());
    }

    @Test
    public void testOrderOfVersionsInRange() throws Exception {
        VersionRangeRequest requestB = new VersionRangeRequest();
        requestB.addRepository(newTestRepository());
        Artifact artifactB = new DefaultArtifact("org.apache.maven.its:dep-mng-range:[4,5)");
        requestB.setArtifact(artifactB);

        VersionRangeResult resultB = versionRangeResolver.resolveVersionRange(session, requestB);
        List<Version> versions = resultB.getVersions();

        assertEquals(4, versions.size());

        LinkedList<String> resolved = new LinkedList<>();
        for (Version version : versions) {
            resolved.add(version.toString());
        }

        Iterator<String> iterator = resolved.iterator();
        assertEquals("4.0.0", iterator.next());
        assertEquals("4.0.0-SNAPSHOT", iterator.next());
        assertEquals("4.0.0-rc1", iterator.next());
        assertEquals("4.0.0-rc1-SNAPSHOT", iterator.next());
    }

}