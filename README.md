# Aether OSGi Range Resolver

This project demonstrates how to customize version range resolution within aether. By default version ranges resolved by
aether are not compatible with osgi specific ranges. This brings some downsides because some properties must be declared
twice - one for maven and another for osgi.

To avoid troubles a small customization of version resolver has been created.

## Current troubles
While this code is pretty messy and does embed `osgi.core` jar it does quite simple thing. Most of code comes from requirements
of aether api implementations. For example `DefaultVersionRangeResolver` despite of its name is not meant to be extended which
brought most of code to project.
Test classes (beyond OsgiVersionRangeResolverTest) is copy of `aether-impl` base classes.

### Usage in Maven build
Even if maven uses aether for some dependency resolution it is currently not possible to use this component as build extension.
