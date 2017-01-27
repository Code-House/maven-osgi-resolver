package org.code_house.maven.osgi.resolver.test;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.code_house.maven.osgi.resolver.test.util.ConsoleRepositoryListener;
import org.code_house.maven.osgi.resolver.test.util.ConsoleTransferListener;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusTestCase;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;

import java.net.MalformedURLException;

public abstract class AbstractRepositoryTestCase extends PlexusTestCase {

    protected RepositorySystem system;

    protected RepositorySystemSession session;

    @Override
    protected void customizeContainerConfiguration(ContainerConfiguration containerConfiguration) {
        super.customizeContainerConfiguration(containerConfiguration);
        containerConfiguration.setAutoWiring(true);
        containerConfiguration.setClassPathScanning(PlexusConstants.SCANNING_INDEX);
        containerConfiguration.setComponentVisibility(PlexusConstants.GLOBAL_VISIBILITY);
    }

    @Override
    protected void setUp()
        throws Exception {
        super.setUp();
        system = lookup(RepositorySystem.class);
        session = newMavenRepositorySystemSession(system);
    }

    @Override
    protected void tearDown()
        throws Exception {
        session = null;
        system = null;
        super.tearDown();
    }

    public RepositorySystemSession newMavenRepositorySystemSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository("target/local-repo");
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        session.setTransferListener(new ConsoleTransferListener());
        session.setRepositoryListener(new ConsoleRepositoryListener());

        return session;
    }

    public static RemoteRepository newTestRepository()
        throws MalformedURLException {
        return new RemoteRepository.Builder("repo", "default", getTestFile("target/test-classes/repo").toURI().toURL().toString()).build();
    }

}
