#!/usr/bin/env bash
# Starts Ollama in the background if it isn't running yet.
if ! command -v ollama > /dev/null; then
  exit 0
fi
if ! curl -s http://localhost:11434 > /dev/null; then
  nohup ollama serve > /tmp/ollama.log 2>&1 &
  for i in $(seq 1 30); do
    curl -s http://localhost:11434 > /dev/null && break
    sleep 1
  done
fi
