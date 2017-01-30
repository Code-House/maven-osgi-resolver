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
package org.code_house.maven.osgi.resolver.compatible;

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

public class CompatibleOsgiVersionRangeResolverTest extends AbstractRepositoryTestCase {

    private CompatibleOsgiVersionRangeResolver versionRangeResolver;

    public CompatibleOsgiVersionRangeResolverTest() {

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        versionRangeResolver = (CompatibleOsgiVersionRangeResolver) lookup(VersionRangeResolver.class);
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
        assertEquals(1, versions.size());

        LinkedList<String> resolved = new LinkedList<>();
        for (Version version : versions) {
            resolved.add(version.toString());
        }

        assertEquals("3.0.5", resolved.getLast());
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
        assertEquals("4.0.0-rc1-SNAPSHOT", iterator.next());
        assertEquals("4.0.0-rc1", iterator.next());
        assertEquals("4.0.0-SNAPSHOT", iterator.next());
        assertEquals("4.0.0", iterator.next());
    }

}
