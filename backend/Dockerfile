# syntax=docker/dockerfile:experimental
FROM openjdk:21-jdk-bullseye AS build-stage
WORKDIR /workspace/app

COPY .editorconfig /workspace/app
COPY src /workspace/app/src
COPY gradle /workspace/app/gradle
COPY gradlew settings.gradle.kts build.gradle.kts /workspace/app/

RUN --mount=type=cache,target=/root/.gradle ./gradlew clean build -x test
RUN mkdir -p build/dependencies && (cd build/dependencies; jar -xf ../libs/backend-0.0.1-SNAPSHOT.jar)

FROM openjdk:21-slim
WORKDIR /tmp
ARG DEPENDENCY=/workspace/app/build/dependencies

COPY --from=build-stage ${DEPENDENCY}/BOOT-INF/lib ./app/lib
COPY --from=build-stage ${DEPENDENCY}/META-INF ./app/META-INF
COPY --from=build-stage ${DEPENDENCY}/BOOT-INF/classes ./app/

ENTRYPOINT ["java","-cp","app:app/lib/*","com.puzzle.backend.BackendApplicationKt"]