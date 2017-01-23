package org.code_house.maven.osgi.resolver.version;

import org.osgi.framework.Version;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OsgiVersion implements org.eclipse.aether.version.Version {

    static final Pattern FUZZY_VERSION = Pattern.compile("(\\d+)(\\.(\\d+)(\\.(\\d+))?)?([^a-zA-Z0-9](.*))?", 32);

    private final String base;
    private final Version version;

    public OsgiVersion(String version) {
        this(version, new Version(normalize(version)));
    }

    public OsgiVersion(Version version) {
        this(version.toString(), version);
    }

    public OsgiVersion(String base, Version version) {
        this.base = base;
        this.version = version;
    }

    @Override
    public int compareTo(org.eclipse.aether.version.Version o) {
        if (o instanceof OsgiVersion) {
            return version.compareTo(((OsgiVersion) o).getVersion());
        }

        return base.compareTo(o.toString());
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OsgiVersion that = (OsgiVersion) o;
        return Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }

    @Override
    public String toString() {
        return base;
    }

    private static String normalize(String version) {
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
