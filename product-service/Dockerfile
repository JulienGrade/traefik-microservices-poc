FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml pom.xml
COPY shipping-service/pom.xml shipping-service/pom.xml
COPY product-service/pom.xml product-service/pom.xml
COPY order-service/pom.xml order-service/pom.xml
COPY product-service/src product-service/src
COPY order-service/src order-service/src
RUN mvn -B -ntp -pl product-service -am verify

FROM eclipse-temurin:17-jre-jammy
RUN groupadd --gid 10001 app && useradd --uid 10001 --gid app --no-create-home app
WORKDIR /app
COPY --from=build /workspace/product-service/target/product-service-1.0.0-SNAPSHOT.jar app.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
