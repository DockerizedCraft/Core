FROM maven as build

WORKDIR /build
COPY src /build/
COPY pom.xml /build/
RUN mvn package

FROM itzg/bungeecord

COPY --from=build /build/target/assembly/DockerizedCraft.jar /server/plugins/DockerizedCraft.jar
