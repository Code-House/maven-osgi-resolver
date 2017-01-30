package org.code_house.maven.osgi.resolver.test.util;

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

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

public class ConsoleRepositoryListener extends AbstractRepositoryListener {

    private Logger logger = LoggerFactory.getLogger(ConsoleRepositoryListener.class);

    public void artifactDeployed(RepositoryEvent event) {
        logger.info("artifactDeployed {} {}", event.getArtifact(), event.getRepository());
    }

    public void artifactDeploying(RepositoryEvent event) {
        logger.info("artifactDeploying {} {}", event.getArtifact(), event.getRepository());
    }

    public void artifactDescriptorInvalid(RepositoryEvent event) {
        logger.info("artifactDescriptorInvalid {}", event.getArtifact(), event.getException());
    }

    public void artifactDescriptorMissing(RepositoryEvent event) {
        logger.info("artifactDescriptorMissing {}", event.getArtifact());
    }

    public void artifactInstalled(RepositoryEvent event) {
        logger.info("artifactInstalled {} {}", event.getArtifact(), event.getFile());
    }

    public void artifactInstalling(RepositoryEvent event) {
        logger.info("artifactInstalling {} {}", event.getArtifact(), event.getFile());
    }

    public void artifactResolved(RepositoryEvent event) {
        logger.info("artifactResolved {} {}", event.getArtifact(), event.getRepository());
    }

    public void artifactDownloading(RepositoryEvent event) {
        logger.info("artifactDownloading {} {}", event.getArtifact(), event.getRepository());
    }

    public void artifactDownloaded(RepositoryEvent event) {
        logger.info("artifactDownloaded {} {}", event.getArtifact(), event.getRepository());
    }

    public void artifactResolving(RepositoryEvent event) {
        logger.info("artifactResolving {}", event.getArtifact());
    }

    public void metadataDeployed(RepositoryEvent event) {
        logger.info("metadataDeployed {} {}", event.getMetadata(), event.getRepository());
    }

    public void metadataDeploying(RepositoryEvent event) {
        logger.info("metadataDeploying {} {}", event.getMetadata(), event.getRepository());
    }

    public void metadataInstalled(RepositoryEvent event) {
        logger.info("metadataInstalled {} {}", event.getMetadata(), event.getFile());
    }

    public void metadataInstalling(RepositoryEvent event) {
        logger.info("metadataInstalling {} {}", event.getMetadata(), event.getFile());
    }

    public void metadataInvalid(RepositoryEvent event) {
        logger.info("metadataInvalid {}", event.getMetadata(), event.getException());
    }

    public void metadataResolved(RepositoryEvent event) {
        logger.info("metadataResolved {} {}", event.getMetadata(), event.getRepository());
    }

    public void metadataResolving(RepositoryEvent event) {
        logger.info("metadataResolving {} {}", event.getMetadata(), event.getRepository());
    }

}
