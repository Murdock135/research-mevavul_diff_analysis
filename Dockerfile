FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

# Install git
RUN apt-get update && \
    apt-get install -y --no-install-recommends git ca-certificates vim neovim && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Download coming 6.0.0 jar with dependencies from Maven Central
RUN wget https://repo1.maven.org/maven2/com/github/spoonlabs/coming/6.0.0/coming-6.0.0-jar-with-dependencies.jar \
    -O coming.jar

# Clone a sample repository for testing
RUN git clone https://github.com/SpoonLabs/repogit4testv0 

# Create directories for analysis
RUN mkdir -p /output

# Create the coming command-line script. Use 'EOF' to prevent variable expansion.
RUN <<'EOF' cat > /usr/local/bin/coming
#!/usr/bin/env bash
set -euo pipefail
exec java -cp /app/coming.jar fr.inria.coming.main.ComingMain "$@"
EOF

RUN chmod +x /usr/local/bin/coming

# Set MOTD
COPY motd.txt /etc/motd

# ENTRYPOINT [ "coming" ]
CMD ["bash"]
