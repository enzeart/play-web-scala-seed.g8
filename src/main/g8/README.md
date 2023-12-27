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
playInteractionMode := AppInteractionMode.Default,
playRunHooks ++= {
  implicit val sharedContext: SharedContext =
    SharedContext.forInteractionMode(
      interactionMode = playInteractionMode.value,
      logger = streams.value.log
    )

  Seq(
    GitSubmoduleServiceHook(
      repositoryRoot = baseDirectory.value.getParentFile,
      submoduleName = "modules/example-service",
      command = "sbt" :: "example-service-server/run" :: Nil
    )
  )
}
```

The above configuration will execute the command `sbt example-service-server/run` at the
file path registered in the `.gitmodules` configuration file for the submodule named
`modules/example-service` after the development server has been started.

You must manage everything else about the submodules yourself. Here are some useful commands
to help ensure that the project is up-to-date with all remote changes.

```bash
# There is a special situation that can happen when pulling superproject updates:
# it could be that the upstream repository has changed the URL of the submodule in the .gitmodules file
# in one of the commits you pull. This can happen for example if the submodule project changes its hosting platform.
# In that case, it is possible for git pull --recurse-submodules, or git submodule update, to fail if the superproject
# references a submodule commit that is not found in the submodule remote locally configured in your repository.
# In order to remedy this situation, the git submodule sync command is required.
git submodule sync --recursive

# Recursively initialize the working tree of all submodules that don't already exist. Also, fetch and update
# the contents of each submodule based on the state of the relevant remote tracking branch.
git submodule update --init --recursive --remote
```

### Excluding Duplicate Services

It is possible for a service to occur more than once in your application's service graph.
This can cause failures when starting the development servers due to port collisions, etc.
You can configure git to exclude the working tree of submodules using the
following commands.

```bash
# This will recursively initialize the working tree of all submodules included in the project.
# Most importantly, it will cause git to populate the .git/modules directory hierarchy where
# we can configure the submodules we want to exclude.
git submodule update --init --recursive

# This will clear out the working tree of all submodules while preserving all of the repository
# information and configurations stored in .git/modules.
git submodule deinit --all

# This will effectively configure the repository to prevent the submodule update command from populating the
# specified submodule's working tree.
git config -f .git/modules/<path_to_parent_of_submodule_to_exclude>/config submodule.<submodule_name>.update none
```

## Gitlab CI

### Docker-in-Docker
Gitlab CI can fail if the Docker-in-Docker service version is incompatible with the one installed in the build stage's
before_script section. If you run into any cryptic Docker failures, make sure the major versions of these two docker
installations are compatible.
