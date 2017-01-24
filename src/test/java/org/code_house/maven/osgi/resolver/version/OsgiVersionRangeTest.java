/*******************************************************************************
 * Copyright (c) 2010, 2013 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.code_house.maven.osgi.resolver.version;

import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionRange;
import org.junit.Test;

import static org.junit.Assert.*;

public class OsgiVersionRangeTest {

    private Version newVersion(String version) {
        return new OsgiVersion(version);
    }

    private VersionRange parseValid(String range) {
        try {
            return new OsgiVersionRange(range);
        } catch (InvalidVersionSpecificationException e) {
            AssertionError error =
                new AssertionError(range + " should be valid but failed to parse due to: " + e.getMessage());
            error.initCause(e);
            throw error;
        }
    }

    private void parseInvalid(String range) {
        try {
            new OsgiVersionRange(range);
            fail(range + " should be invalid");
        } catch (InvalidVersionSpecificationException e) {
            assertTrue(true);
        }
    }

    private void assertContains(VersionRange range, String version) {
        assertTrue(range + " should contain " + version, range.containsVersion(newVersion(version)));
    }

    private void assertNotContains(VersionRange range, String version) {
        assertFalse(range + " should not contain " + version, range.containsVersion(newVersion(version)));
    }

    @Test
    public void testLowerBoundInclusiveUpperBoundInclusive() {
        VersionRange range = parseValid("[1,2]");
        assertContains(range, "1");
        assertContains(range, "1.1-SNAPSHOT");
        assertContains(range, "2");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testLowerBoundInclusiveUpperBoundExclusive() {
        VersionRange range = parseValid("[1.2.3.4.5,1.2.3.4.6)");
        assertContains(range, "1.2.3.4.5");
        assertNotContains(range, "1.2.3.4.6");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testLowerBoundExclusiveUpperBoundInclusive() {
        VersionRange range = parseValid("(1a,1b]");
        assertNotContains(range, "1a");
        assertContains(range, "1b");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testLowerBoundExclusiveUpperBoundExclusive() {
        VersionRange range = parseValid("(1,3)");
        assertNotContains(range, "1");
        assertContains(range, "2-SNAPSHOT");
        assertNotContains(range, "3");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testSingleVersion() {
        VersionRange range = parseValid("[1]");
        assertContains(range, "1");
        assertEquals(range, parseValid(range.toString()));

        range = parseValid("[1,1]");
        assertContains(range, "1");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testSingleWildcardVersion() {
        VersionRange range = parseValid("[1.2.*]");
        assertContains(range, "1.2-alpha-1");
        assertContains(range, "1.2-SNAPSHOT");
        assertContains(range, "1.2");
        assertContains(range, "1.2.9999999");
        assertNotContains(range, "1.3-rc-1");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testMissingOpenCloseDelimiter() {
        parseInvalid("1.0");
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
    public void testSnapshotAndRelease() {
        VersionRange range = parseValid("[4.0.0.min,5)");

        assertContains(range, "4.0-alpha-1");
        assertContains(range, "4.0-SNAPSHOT");
        assertContains(range, "4.0.0-SNAPSHOT");
        assertContains(range, "4.99");
        assertNotContains(range, "5");
        assertEquals(range, parseValid(range.toString()));
    }

    @Test
    public void testSnapshotAndRelease2() {
        VersionRange range = parseValid("[4.0.0,5)");

        assertContains(range, "4.0-alpha-1");
        assertContains(range, "4.0-SNAPSHOT");
        assertContains(range, "4.0.0-SNAPSHOT");
        assertContains(range, "4.99.99");
        assertNotContains(range, "5");
        assertNotContains(range, "5.0.0-SNAPSHOT");
        assertEquals(range, parseValid(range.toString()));
    }


}
