# play-web-scala-seed.g8

A highly opinionated Play Framework application giter8 template

This template includes the following features:
- GraphQL support via Sangria
- Dependency injection support via guice
- A Dockerfile for building a basic application image
- A project-specific sbt plugin to keep custom build logic well-organized
- An sbt task to generate an Angular web application project
- Giter8 scaffolds for generating some common boilerplate

## Running

To use this template, run `sbt new enzeart/play-web-scala-seed.g8`

## sbt task namespacing

The built-in utility tasks that are generated as part of the project are prefixed with the project name in lower
camel-case. As a result, some examples below will use a hypothetical project name when outlining usage. When in doubt,
you can always look at the sbt plugin generated in the *project* directory.

## Generating an Angular project

For a project named play-test, you can invoke the task to create an Angular project by issuing the
`sbt play-test/playTestAngularUi` command in the project directory.

## Frontend GraphQL Code Generation

For a project named play-test, you can invoke the task to generate typescript code based on the server-side
GraphQL schema by issuing the `sbt play-test/playTestGraphqlCodegen` command in the project directory.

This task starts the server in dev mode and runs the @graphql-codegen/cli utility against the server's graphql endpoint.
The cli will also generate Apollo query services based on any queries defined in *.graphql files in the UI project
directory tree.

This task will also generate an introspection file that is used for Intellij GraphQL language support.

## Starting the UI and Server Applications

For a project named play-test, you can invoke the task to start Play Framework and the angular
UI project in dev mode by issuing the `sbt play-test/playTestAppStart` command in the project directory.

Proxy configurations are included in the proxy.conf.js file to tell the UI dev server to route backend
API calls to the Play Framework dev server.

Template license
----------------
Written in 2020 by Enseart Simpson

To the extent possible under law, the author(s) have dedicated all copyright and related
and neighboring rights to this template to the public domain worldwide.
This template is distributed without any warranty. See <http://creativecommons.org/publicdomain/zero/1.0/>.

[g8]: http://www.foundweekends.org/giter8/
