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
package org.code_house.maven.osgi.resolver.shared.version;

import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import java.util.Objects;

/**
 * Version range implementation which is backed by {@link VersionRange} from osgi framework.
 */
final class OsgiVersionRange implements org.eclipse.aether.version.VersionRange {

    private final VersionRange range;

    OsgiVersionRange(String range) throws InvalidVersionSpecificationException {
        try {
            this.range = new VersionRange(range);
        } catch (Exception e) {
            throw new InvalidVersionSpecificationException("Invalid version range " + range, e);
        }
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
        return range.toString();
    }

}
