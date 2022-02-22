FROM ubuntu:latest AS builder

WORKDIR /build
COPY . .

RUN apt-get update && apt-get install -y curl zip openjdk-11-jdk
SHELL ["/bin/bash", "-c"]
RUN curl -s https://get.sdkman.io | bash
RUN source "/root/.sdkman/bin/sdkman-init.sh" && sdk install sbt

RUN source "/root/.sdkman/bin/sdkman-init.sh" && sbt clean $name;format="norm"$-server/Universal/packageZipTarball
RUN tar -xzf /build/$name;format="norm"$-server/target/universal/$name;format="norm"$-server.tgz -C /tmp

FROM ubuntu:latest AS app

WORKDIR /app
COPY --from=builder /tmp/$name;format="norm"$-server/ ./
RUN apt-get update && apt-get install -y openjdk-8-jre
EXPOSE 9000/tcp

CMD ["/app/bin/$name;format="norm"$-server", "-J-Xms4096M", "-J-Xmx4096M", "-J-server"]
