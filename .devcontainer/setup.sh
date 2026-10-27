#!/usr/bin/env bash
# Runs once, when the Codespace is created. Takes about 5 minutes.
set -e

echo ">>> Installing Ollama (runs AI models inside this Codespace)"
# apt-get update can exit with an error on a broken third-party repo key (Yarn) even though
# zstd installs fine, so don't let that stop the setup.
sudo apt-get update -qq || true
sudo apt-get install -y -qq zstd > /dev/null
curl -fsSL https://ollama.com/install.sh | sh

echo ">>> Starting Ollama"
bash .devcontainer/start-ollama.sh

echo ">>> Downloading the models (about 2.3 GB)"
ollama pull llama3.2
ollama pull nomic-embed-text

echo ">>> Downloading the Java libraries"
mvn -q -DskipTests dependency:go-offline || true

echo ""
echo "All set. Run:  mvn spring-boot:run"
