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
import org.eclipse.aether.version.VersionConstraint;

import java.util.Objects;

/**
 * Version constraint implementation following aether philosophy:
 * - returns null when asked for giving range but constructed range is exact
 * - returns null when asked for giving version recommendation but range is wider than just single number
 *
 * @author Łukasz Dywicki
 */
public class OsgiVersionConstraint implements VersionConstraint {
    private final String constraint;
    private final OsgiVersionRange versionRange;

    public OsgiVersionConstraint(String constraint) throws InvalidVersionSpecificationException {
        this.versionRange = new OsgiVersionRange(constraint);
        this.constraint = constraint;
    }

    @Override
    public org.eclipse.aether.version.VersionRange getRange() {
        if (versionRange.isExact()) {
            return null; // we have just one version
        }
        return versionRange;
    }

    @Override
    public org.eclipse.aether.version.Version getVersion() {
        if (versionRange.isExact()) {
            return new OsgiVersion(constraint);
        }
        return null;
    }

    @Override
    public boolean containsVersion(org.eclipse.aether.version.Version version) {
        return getRange().containsVersion(version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OsgiVersionConstraint that = (OsgiVersionConstraint) o;
        return Objects.equals(constraint, that.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constraint);
    }

    @Override
    public String toString() {
        return "Osgi Version constraint[" + constraint + ']';
    }
}
