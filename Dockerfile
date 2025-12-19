FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

# Install dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends git ca-certificates vim neovim python3 curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Install uv using the official installer
RUN curl -LsSf https://astral.sh/uv/install.sh | sh

# Add uv to PATH
ENV PATH="/root/.local/bin:$PATH"

# Configure uv caching for faster builds. UV_LINK_MODE = copy suppresses warnings that hardlink won't be possible.
ENV UV_LINK_MODE=copy
ENV UV_PYTHON_CACHE_DIR=/root/.cache/uv/python

# Download coming 6.0.0 jar with dependencies from Maven Central
RUN wget https://repo1.maven.org/maven2/com/github/spoonlabs/coming/6.0.0/coming-6.0.0-jar-with-dependencies.jar \
    -O /opt/coming.jar

# Clone a sample repository for testing
RUN git clone https://github.com/SpoonLabs/repogit4testv0 /opt/repogit4testv0

# Create directories for analysis
RUN mkdir -p /data/code /output

# Create the coming command-line script
RUN <<'EOF' cat > /usr/local/bin/coming
#!/usr/bin/env bash
set -euo pipefail
exec java -cp /opt/coming.jar fr.inria.coming.main.ComingMain "$@"
EOF

RUN chmod +x /usr/local/bin/coming

# Copy python source code and dependency files
COPY pyproject.toml uv.lock* ./
COPY src/ ./src/
COPY README.md ./

# Install python dependencies
RUN uv sync --frozen

# Set MOTD
COPY motd.txt /etc/motd

CMD ["bash"]
