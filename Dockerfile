# Imagens padrão via mirror do Google (evita timeout no Docker Hub em algumas redes).
# Para usar o Docker Hub diretamente:
#   docker compose build --build-arg MAVEN_IMAGE=maven:3.9-eclipse-temurin-17 --build-arg TOMCAT_IMAGE=tomcat:10.1-jdk17-temurin
ARG MAVEN_IMAGE=mirror.gcr.io/library/maven:3.9-eclipse-temurin-17
ARG TOMCAT_IMAGE=mirror.gcr.io/library/tomcat:10.1-jdk17-temurin

# Estágio 1: Compilação
FROM ${MAVEN_IMAGE} AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# Estágio 2: Tomcat
FROM ${TOMCAT_IMAGE}
WORKDIR /usr/local/tomcat/webapps/
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/ROOT.war ./ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
