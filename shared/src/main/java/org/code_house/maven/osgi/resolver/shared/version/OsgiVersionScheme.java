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
package org.code_house.maven.osgi.resolver.shared.version;

import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.VersionConstraint;
import org.eclipse.aether.version.VersionScheme;

public final class OsgiVersionScheme implements VersionScheme {

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

}
