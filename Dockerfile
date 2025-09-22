# Estágio 1: Build da Aplicação com Gradle
FROM gradle:8.9.0-jdk21 AS build
WORKDIR /app

# Copie os arquivos do Gradle e baixe as dependências primeiro para aproveitar o cache
# MUDANÇA AQUI: de .gradle.kts para .gradle
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew build || return 0

# Copie o código fonte e construa o JAR executável
COPY src ./src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# Estágio 2: Criação da Imagem Final
FROM openjdk:21-slim
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]