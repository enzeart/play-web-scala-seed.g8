# play-web-scala-seed.g8

A highly opinionated Play Framework giter8 template

This template includes the following features:
- GraphQL support via Sangria
- Dependency injection support via guice
- A Dockerfile for building a basic application image
- A project-specific sbt plugin to keep custom build logic well-organized
- An sbt task to generate an Angular web application project
- Giter8 scaffolds for generating some common boilerplate
- Security via play-pac4j

## Running

To use this template, run `sbt new enzeart/play-web-scala-seed.g8`

## sbt task namespacing

The sbt plugin that is generated for the project includes some default utility tasks. These tasks are prefixed with
the lower camel-cased name of the project. You can inspect the plugin source code in the *project* sub-directory to see
the exact names generated for your project.

## Generating an Angular project

Use the `(project name)/(lower camel-case project name)AngularUI` task to create an Angular project in the root
directory. The task takes an optional argument that specifies the name of the subdirectory in which to generate the
Angular project. If left unspecified, the name of the directory will be *ui*.

## Frontend GraphQL Code Generation

Use the `(project name)/(lower camel-case project name)GraphqlCodegen` task to (re)generate GraphQL code and configurations
for the Angular project. This includes typescript models based on the server-side GraphQL schema and Apollo client query services
based on *.graphql files included with the UI source code. This task will also (re)generate an introspection file to help
with Intellij GraphQL language support.

## Starting the UI and Server Applications

Use the `(project name)/(lower camel-case project name)AppStart` task to start both the Play development server
and the UI development server. Proxy configurations for the UI project server are preconfigured to route certain API
calls to the Play server. These configurations can be found in *proxy.conf.js*.

## Generating Modules

Use the `(project name)/(lower camel-case project name)Module` task to generate a guice module in the *modules* package of
the project. This task expects a single string argument will be used as the base name for the generated module class.
The module class name will be `(base name as upper camel-case)Module`.

## Generating Sangria GraphQL Schemas

Use the `(project name)/(lower camel-cased project name)GraphqlSchema` task to generate some boilerplate for a new Sangria
GraphQL schema in the *graphql* package of the project. This task expects a single string argument that will be used as the
base name for the generated class. The class name will be `(base name as upper camel-case)Schema`.

## Generating Controllers

Use the `(project name)/(lower camel-case project name)Controller` task to generate a controller class in the *controllers*
package of the project. This task expects a single string argument that will be used as the base name for the generated
class. The class name will be `(base name as upper camel-case)Controller`.

## Generating Models

Use the `(project name)/(lower camel-case project name)Model` task to generate a model class in the *models*
package of the project. This task expects a single string argument that will be used as the base name for the generated
class. The class name will be `(base name as upper camel case)Model`. A second, optional argument can be passed to specify
a sub-package of the *models* package where the class should be placed.

Template license
----------------
Written in 2020 by Enseart Simpson

To the extent possible under law, the author(s) have dedicated all copyright and related
and neighboring rights to this template to the public domain worldwide.
This template is distributed without any warranty. See (http://creativecommons.org/publicdomain/zero/1.0/).

[g8]: http://www.foundweekends.org/giter8/
