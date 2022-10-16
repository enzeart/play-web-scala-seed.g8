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
