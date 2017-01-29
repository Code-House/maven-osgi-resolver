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

import org.osgi.framework.Version;

import java.util.Objects;

/**
 * Version implementation to bridge osgi logic to aether's world.
 *
 * @author Łukasz Dywicki
 */
public class OsgiVersion implements org.eclipse.aether.version.Version {

    private final String rawVersion;
    private final Version version;

    public OsgiVersion(String version) {
        this(version, new Version(OsgiVersionScheme.normalize(version)));
    }

    public OsgiVersion(Version version) {
        this(version.toString(), version);
    }

    public OsgiVersion(String rawVersion, Version version) {
        this.rawVersion = rawVersion;
        this.version = version;
    }

    @Override
    public int compareTo(org.eclipse.aether.version.Version o) {
        if (o instanceof OsgiVersion) {
            return version.compareTo(((OsgiVersion) o).getVersion());
        }

        return version.compareTo(new Version(OsgiVersionScheme.normalize(o.toString())));
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OsgiVersion that = (OsgiVersion) o;
        return Objects.equals(rawVersion, that.rawVersion) &&
            Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawVersion, version);
    }

    @Override
    public String toString() {
        return rawVersion;
    }

}
