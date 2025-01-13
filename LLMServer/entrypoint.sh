#!/bin/bash

if [ -z "$MODEL_NAME" ]; then
  echo "Errore: La variabile MODEL_NAME non è impostata."
  exit 1
fi

echo "Scarico il modello: $MODEL_NAME"
ollama run "$MODEL_NAME"

echo "Eseguo ollama in modalità servizio..."
exec "$@"
