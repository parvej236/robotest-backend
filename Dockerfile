# ─────────────────────────────────────────────────────────────
# Stage 1: Build the Spring Boot JAR
# ─────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml first — lets Docker cache the dependency layer
# Only re-downloads dependencies if pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -q

# ─────────────────────────────────────────────────────────────
# Stage 2: Run the JAR (minimal image, no Maven/JDK overhead)
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create uploads directory — will be mounted as a volume
RUN mkdir -p /app/uploads/profiles /app/uploads/questions/images /app/uploads/questions/videos

# Copy only the built JAR
COPY --from=builder /app/target/*.jar app.jar

# Non-root user for security
RUN addgroup -S robotest && adduser -S robotest -G robotest
RUN chown -R robotest:robotest /app
USER robotest

EXPOSE 8080

# Pass Spring profile and ensure uploads dir is writable
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Djava.net.preferIPv4Stack=true", \
  "-Dspring.profiles.active=prod", \
  "-jar", "app.jar"]