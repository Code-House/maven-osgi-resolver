package org.code_house.maven.osgi.resolver.version;

import org.eclipse.aether.version.VersionConstraint;

import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import java.util.Objects;

/**
 * Created by splatch on 23.01.2017.
 */
public class OsgiVersionConstraint implements VersionConstraint {
    private final String constraint;

    public OsgiVersionConstraint(String constraint) {
        this.constraint = constraint;
    }

    @Override
    public org.eclipse.aether.version.VersionRange getRange() {
        return new OsgiVersionRange(constraint);
    }

    @Override
    public org.eclipse.aether.version.Version getVersion() {
        return new OsgiVersion(constraint);
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
