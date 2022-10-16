FROM ubuntu:latest AS builder

$if(codeartifact_support_enabled.truthy)$
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY
ARG AWS_REGION
$endif$

$if(codeartifact_support_enabled.truthy)$
ENV AWS_ACCESS_KEY_ID=\$AWS_ACCESS_KEY_ID
ENV AWS_SECRET_ACCESS_KEY=\$AWS_SECRET_ACCESS_KEY
ENV AWS_REGION=\$AWS_REGION
$endif$

WORKDIR /build
COPY . .

RUN apt-get update && DEBIAN_FRONTEND=noninteractive TZ=Etc/UTC apt-get install -y curl zip openjdk-11-jdk
RUN curl -fsSL https://deb.nodesource.com/setup_14.x | bash - && apt-get install -y nodejs

SHELL ["/bin/bash", "-c"]
RUN curl -s https://get.sdkman.io | bash
RUN source "/root/.sdkman/bin/sdkman-init.sh" && sdk install sbt

RUN if [ -d "/build/$name;format="norm"$-server/ui" ]; then cd /build/$name;format="norm"$-server/ui && npm ci && npm run build; fi

RUN source "/root/.sdkman/bin/sdkman-init.sh" && sbt clean $name;format="norm"$-server/Universal/packageZipTarball
RUN tar -xzf /build/$name;format="norm"$-server/target/universal/$name;format="norm"$-server.tgz -C /tmp

FROM ubuntu:latest AS app

WORKDIR /app
COPY --from=builder /tmp/$name;format="norm"$-server/ ./
RUN apt-get update && apt-get install -y openjdk-8-jre
EXPOSE 9000/tcp

CMD ["/app/bin/$name;format="norm"$-server", "-J-Xms4096M", "-J-Xmx4096M", "-J-server"]
