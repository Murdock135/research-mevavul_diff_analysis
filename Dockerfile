FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

# Install git
RUN apt-get update && apt-get install -y --no-install-recommends git ca-certificates && rm -rf /var/lib/apt/lists/*

# Download coming 6.0.0 jar with dependencies from Maven Central
RUN wget https://repo1.maven.org/maven2/com/github/spoonlabs/coming/6.0.0/coming-6.0.0-jar-with-dependencies.jar \
    -O coming.jar

# Create directories for analysis
RUN mkdir -p /data/code /output

CMD ["bash"]
