# $name$

$if(codeartifact_support_enabled.truthy)$
## AWS CodeArtifact Support
AWS CodeArtifact support is provided by the [sbt-codeartifact](https://github.com/bbstilson/sbt-codeartifact)
plugin. Take a look at the documentation for detailed information on how it works and
how it is configured.

When using this project with an IDE, you must ensure the build can find the region and credentials needed to
access your repository. The easiest option is to configure the default profiles in your ~/.aws/config
and ~/.aws/credentials files. If you'd prefer to take a different approach, consult the documentation for your IDE,
sbt-codeartifact, and AWS to see if there is a more suitable solution for your needs.


$endif$

## Multi-Project Development

The development server can be configured to execute commands in external projects that are included
as Git submodules. For example:

```scala
playRunHooks += GitSubmoduleServiceHook(
  repositoryRoot = baseDirectory.value.getParentFile,
  submoduleName = "modules/example-service",
  command = "sbt" :: "example-service-server/run" :: Nil
)
```

The above configuration will execute the command `sbt example-service-server/run` at the
file path registered in the `.gitmodules` configuration file for the submodule named
`modules/example-service` after the development server has been started.

You must manage everything else about the submodules yourself. Here are some useful commands
to help ensure that the project is up-to-date with all remote changes.

```bash
git submodule sync --recursive
git submodule update --init --recursive --remote
```

