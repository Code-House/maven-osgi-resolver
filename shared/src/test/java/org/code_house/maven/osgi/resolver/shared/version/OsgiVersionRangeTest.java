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
package org.code_house.maven.osgi.resolver.shared.version;

import org.code_house.maven.osgi.resolver.test.VersionRangeTest;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test of OSGi version range filtering.
 */
public class OsgiVersionRangeTest extends VersionRangeTest<OsgiVersionRange, OsgiVersion> {

    @Override
    protected OsgiVersion newVersion(String version) {
        return new OsgiVersion(version);
    }

    @Override
    protected OsgiVersionRange parseValid(String range) {
        try {
            return new OsgiVersionRange(range);
        } catch (InvalidVersionSpecificationException e) {
            AssertionError error =
                new AssertionError(range + " should be valid but failed to parse due to: " + e.getMessage());
            error.initCause(e);
            throw error;
        }
    }

    @Override
    protected void parseInvalid(String range) {
        try {
            new OsgiVersionRange(range);
            fail(range + " should be invalid");
        } catch (InvalidVersionSpecificationException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testLowerBoundInclusiveUpperBoundInclusive() {
        OsgiVersionRange range = parseValid("[1,2]");
        assertContains(range, "1");
        assertContains(range, "1.1-SNAPSHOT");
        assertContains(range, "2");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testLowerBoundInclusiveUpperBoundExclusive() {
        OsgiVersionRange range = parseValid("[1.2.3.4,1.2.3.6)");
        assertContains(range, "1.2.3.4");
        assertContains(range, "1.2.3.45");
        assertContains(range, "1.2.3.5");
        assertNotContains(range, "1.2.3.6");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testLowerBoundExclusiveUpperBoundInclusive() {
        OsgiVersionRange range = parseValid("(1.0.0.a,1.0.0.b]");
        assertNotContains(range, "1-a");
        assertContains(range, "1-b");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testLowerBoundExclusiveUpperBoundExclusive() {
        OsgiVersionRange range = parseValid("(1,3)");
        assertNotContains(range, "1");
        assertContains(range, "2-SNAPSHOT");
        assertNotContains(range, "3");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testSingleVersion() {
        OsgiVersionRange range = parseValid("[1,1]");
        assertContains(range, "1");
        assertEquals(range, parseValid(range.toString()));

        range = parseValid("[1,1]");
        assertContains(range, "1");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testSingleWildcardVersion() {
        OsgiVersionRange range = parseValid("1.2");
        assertContains(range, "1.2-alpha-1");
        assertContains(range, "1.2-SNAPSHOT");
        assertContains(range, "1.2");
        assertContains(range, "1.2.9999999");
        assertContains(range, "1.3-rc-1");
        assertContains(range, "1.9");
        assertContains(range, "9");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testMissingOpenCloseDelimiter() {
        parseValid("1.0");
    }

    @Test
    public void testMissingOpenDelimiter() {
        parseInvalid("1.0]");
        parseInvalid("1.0)");
    }

    @Test
    public void testMissingCloseDelimiter() {
        parseInvalid("[1.0");
        parseInvalid("(1.0");
    }

    @Test
    public void testTooManyVersions() {
        parseInvalid("[1,2,3]");
        parseInvalid("(1,2,3)");
        parseInvalid("[1,2,3)");
    }

    @Test
    public void testSnapshotAndReleaseWithMinimum() {
        OsgiVersionRange range = parseValid("[4.0.0.min,5)");

        assertNotContains(range, "4.0-alpha-1");
        assertNotContains(range, "4.0-SNAPSHOT");
        assertNotContains(range, "4.0.0-SNAPSHOT");
        assertContains(range, "4.min");
        assertContains(range, "4.99");
        assertNotContains(range, "5");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testSnapshotAndReleaseWithoutMinimum() {
        OsgiVersionRange range = parseValid("[4.0.0,5)");

        assertContains(range, "4.0-alpha-1");
        assertContains(range, "4.0-SNAPSHOT");
        assertContains(range, "4.0.0-SNAPSHOT");
        assertContains(range, "4.99.99");
        assertNotContains(range, "5");
        assertNotContains(range, "5.0.0-SNAPSHOT");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testSnapshotAndReleaseWithMinAndMax() {
        OsgiVersionRange range = parseValid("[4.0.0.min,5.0.0.min)");

        assertNotContains(range, "4.0-alpha-1");
        assertNotContains(range, "4.0-SNAPSHOT");
        assertNotContains(range, "4.0.0-SNAPSHOT");
        assertContains(range, "4.0.0.min");
        assertContains(range, "4.0.0-min");
        assertContains(range, "4.0.0-minimum");
        assertContains(range, "4.99.99");
        assertContains(range, "5");
        assertContains(range, "5.0.0-SNAPSHOT");
        assertContains(range, "5-alpha-1");
        assertNotContains(range, "5-rc");
        assertContains(range, "5-ga");
        assertContains(range, "5-mi");
        assertNotContains(range, "5-min");
        assertNotContains(range, "5-minimum");
    }

}
