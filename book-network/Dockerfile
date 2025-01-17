# Build stage
FROM maven:3.9-amazoncorretto-21-alpine AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime Stage
FROM amazoncorretto:21-alpine
LABEL authors="thivernale"
# Arguments to be passed when building the image (with provided defaults)
ARG PROFILE=dev
ARG APP_PORT=8088
ARG APP_VERSION=1.0.0

WORKDIR /app
COPY --from=build /build/target/book-network-*.jar /app/app-${APP_VERSION}.jar
EXPOSE ${APP_PORT}

# Create environment variables for arguments to be able to reference them at runtime
ENV PROFILE=${PROFILE}
ENV APP_VERSION=${APP_VERSION}
ENV DB_HOST=postgres

CMD java -jar -Dspring.profiles.active=${PROFILE} app-${APP_VERSION}.jar

# Run locally with:
# docker build -t bsn/bsn-api:1.0.0 -f ./book-network/Dockerfile ./book-network/
