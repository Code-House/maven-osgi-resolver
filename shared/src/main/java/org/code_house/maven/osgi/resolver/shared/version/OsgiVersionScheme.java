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
import org.eclipse.aether.version.VersionScheme;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OsgiVersionScheme implements VersionScheme {

    static final Pattern FUZZY_VERSION = Pattern.compile("(\\d+)(\\.(\\d+)(\\.(\\d+))?)?([^a-zA-Z0-9](.*))?", 32);

    @Override
    public org.eclipse.aether.version.Version parseVersion(String version) throws InvalidVersionSpecificationException {
        return new OsgiVersion(version);
    }

    @Override
    public org.eclipse.aether.version.VersionRange parseVersionRange(String range) throws InvalidVersionSpecificationException {
        return new OsgiVersionRange(range);
    }

    @Override
    public VersionConstraint parseVersionConstraint(String constraint) throws InvalidVersionSpecificationException {
        return new OsgiVersionConstraint(constraint);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        return obj != null && getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    static String normalize(String version) {
        StringBuffer result = new StringBuffer();
        Matcher m = FUZZY_VERSION.matcher(version);

        if (m.matches()) {
            String major = m.group(1);
            String minor = m.group(3);
            String micro = m.group(5);
            String qualifier = m.group(7);

            if (major != null) {
                result.append(major);
                if (minor != null) {
                    result.append(".");
                    result.append(minor);
                    if (micro != null) {
                        result.append(".");
                        result.append(micro);
                        if (qualifier != null) {
                            result.append(".");
                            cleanupModifier(result, qualifier);
                        }
                    } else if (qualifier != null) {
                        result.append(".0.");
                        cleanupModifier(result, qualifier);
                    } else {
                        result.append(".0");
                    }
                } else if (qualifier != null) {
                    result.append(".0.0.");
                    cleanupModifier(result, qualifier);
                } else {
                    result.append(".0.0");
                }
            }
        } else {
            result.append("0.0.0.");
            cleanupModifier(result, version);
        }
        return result.toString();
    }

    static void cleanupModifier(StringBuffer result, String modifier) {
        for (int i = 0; i < modifier.length(); i++) {
            char c = modifier.charAt(i);
            if (((c >= '0') && (c <= '9')) || ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || (c == '_') || (c == '-')) {
                result.append(c);
            } else result.append('_');
        }
    }

}
