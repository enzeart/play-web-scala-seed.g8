FROM alpine:latest AS builder

WORKDIR /build
COPY . .
RUN apk add --no-cache --update-cache bash nodejs npm curl zip openjdk10
SHELL ["/bin/bash", "-c"]
RUN curl -s https://get.sdkman.io | bash
RUN source "/root/.sdkman/bin/sdkman-init.sh" && sdk install sbt

RUN source "/root/.sdkman/bin/sdkman-init.sh" && sbt clean $name;format="norm"$/universal:packageZipTarball
RUN tar -xzf /build/target/universal/$name;format="norm"$-*.tgz -C /tmp

FROM alpine:latest AS app
WORKDIR /app
COPY --from=builder /tmp/$name;format="norm"$ ./
RUN apk add --no-cache --update-cache bash openjdk8
EXPOSE 9000/tcp
CMD ["/app/bin/$name;format="norm"$"]
