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
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import java.util.Objects;

/**
 * A version range inspired by mathematical range syntax. For example, "[1.0,2.0)", "[1.0,)" or "[1.0]".
 */
final class OsgiVersionRange implements org.eclipse.aether.version.VersionRange {

    private final VersionRange range;

    OsgiVersionRange(String range) throws InvalidVersionSpecificationException {
        this.range = new VersionRange(normalize(range));
    }

    @Override
    public boolean containsVersion(org.eclipse.aether.version.Version version) {
        if (version instanceof OsgiVersion) {
            return range.includes(((OsgiVersion) version).getVersion());
        }

        return range.includes(new Version(version.toString()));
    }

    public boolean isEmpty() {
        return range.isEmpty();
    }

    public boolean isExact() {
        return range.isExact();
    }

    @Override
    public Bound getLowerBound() {
        return new Bound(new OsgiVersion(range.getLeft()), range.getLeftType() == VersionRange.LEFT_OPEN);
    }

    @Override
    public Bound getUpperBound() {
        return new Bound(new OsgiVersion(range.getRight()), range.getLeftType() == VersionRange.RIGHT_OPEN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OsgiVersionRange that = (OsgiVersionRange) o;
        return Objects.equals(range, that.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(range);
    }

    @Override
    public String toString() {
        return "Osgi VersionRange[" + range + ']';
    }

    private static String normalize(String range) {
        // for versions which are not ranges osgi creates open range X,0.0.0, we don't want to do it, we want exact version
        if (range.charAt(0) == VersionRange.LEFT_CLOSED || range.charAt(0) == VersionRange.LEFT_OPEN) {
            return range;
        }
        return VersionRange.LEFT_CLOSED + range + ',' + range + VersionRange.RIGHT_CLOSED;
    }

}
