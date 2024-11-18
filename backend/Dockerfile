# syntax=docker/dockerfile:experimental
FROM openjdk:21-jdk-bullseye AS build-stage
WORKDIR /workspace/app

COPY src /workspace/app/src
COPY gradle /workspace/app/gradle
COPY gradlew settings.gradle.kts build.gradle.kts /workspace/app/

#RUN --mount=type=cache,target=/root/.gradle ./gradlew clean build -x test
# Build cache import/export 기능을 활용하기 위한 코드
RUN --mount=type=cache,target=/root/.gradle ls -alh /root/.gradle; ./gradlew clean build -x test; ls -alh /root/.gradle
RUN mkdir -p build/dependencies && (cd build/dependencies; jar -xf ../libs/backend-0.0.1-SNAPSHOT.jar)

FROM openjdk:21-slim
WORKDIR /tmp
ARG DEPENDENCY=/workspace/app/build/dependencies

COPY --from=build-stage ${DEPENDENCY}/BOOT-INF/lib ./app/lib
COPY --from=build-stage ${DEPENDENCY}/META-INF ./app/META-INF
COPY --from=build-stage ${DEPENDENCY}/BOOT-INF/classes ./app/

ENTRYPOINT ["java","-cp","app:app/lib/*","com.puzzle.backend.BackendApplicationKt"]