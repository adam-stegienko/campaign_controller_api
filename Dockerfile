# Start with a base image containing Java runtime
FROM openjdk:23-oraclelinux9

# Add Maintainer Info
LABEL maintainer="adam.stegienko1@gmail.com"

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8002 available to the world outside this container
EXPOSE 8002

ARG APP_VERSION
ENV APP_VERSION=$APP_VERSION

# Add the application's jar to the container
COPY target/campaign_controller_api-${APP_VERSION}.jar campaign_controller_api.jar

# Run the jar file 
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/campaign_controller_api.jar"]