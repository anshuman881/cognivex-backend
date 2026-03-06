# ---------- Build Stage ----------
FROM maven:3.9-eclipse-temurin-25-alpine AS builder

WORKDIR /build

# Copy pom first (better caching)
COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Copy source
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests


# ---------- Runtime Stage ----------
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]