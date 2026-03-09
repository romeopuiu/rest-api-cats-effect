# ---------- Build Stage ----------
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Install sbt manually
RUN apt-get update && \
    apt-get install -y curl unzip && \
    curl -L -o sbt.zip https://github.com/sbt/sbt/releases/download/v1.12.4/sbt-1.12.4.zip && \
    unzip sbt.zip && \
    rm sbt.zip

ENV PATH="/app/sbt/bin:${PATH}"

# Copy build files first
COPY build.sbt .
COPY project ./project

RUN sbt update

# Copy source
COPY . .

# Build fat jar
RUN sbt clean assembly


# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/scala-3.3.7/*assembly*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]