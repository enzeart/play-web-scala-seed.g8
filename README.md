# Play Web Scala Seed

An opinionated [Giter8](https://www.foundweekends.org/giter8/) template for Play Framework web applications.

## Usage
Many of the template fields are used for library versions referenced
within the generated code. This can make the interactive template menu a bit tedious.
Here are some pre-built commands for common use cases that will bypass the interactive setup.

### Standard
```shell
sbt new enzeart/play-web-scala-seed.g8 \
  --name=play-web-test \
  --organization=com.example
```

### OIDC Support
```shell
sbt new enzeart/play-web-scala-seed.g8 \
  --name=play-web-test \
  --organization=com.example \
  --oidc_enabled=yes
```

### AWS CodeArtifact Support
```shell
sbt new enzeart/play-web-scala-seed.g8 \
  --name=play-web-test \
  --organization=com.example \
  --codeartifact_support_enabled=yes \
  --codeartifact_url=https://my_domain-111122223333.d.codeartifact.us-west-2.amazonaws.com/maven/my_repo/
```

## Template license
Written by Enseart Simpson

To the extent possible under law, the author(s) have dedicated all copyright and related
and neighboring rights to this template to the public domain worldwide.
This template is distributed without any warranty. See <http://creativecommons.org/publicdomain/zero/1.0/>.
