from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import pandas as pd
import ast
import faiss
import numpy as np
from sentence_transformers import SentenceTransformer
import ollama
import os
import uvicorn

# Inizializza l'app FastAPI
app = FastAPI()

# Caricamento del dataset e configurazione iniziale
csv_path = '../linux_commands_conversations.csv'  # Sostituisci con il tuo file
embedding_file = 'user_embeddings.npy'
index_file = 'faiss_index.index'

# Variabili globali
df = None
conversations = []
model = SentenceTransformer('all-MiniLM-L6-v2')
index = None


# Carica il dataset all'avvio del server
def initialize():
    global df, conversations, index

    # 1. Caricamento del dataset CSV
    df = pd.read_csv(csv_path)
    df.columns = df.columns.str.strip()
    df['conversations'] = df['conversations'].apply(ast.literal_eval)

    # Estrazione conversazioni
    conversations.clear()
    for row in df['conversations']:
        user_message = next(item['content'] for item in row if item['role'] == 'user')
        assistant_response = next(item['content'] for item in row if item['role'] == 'assistant')
        conversations.append((user_message, assistant_response))

    # 2. Creazione o caricamento embeddings e indice FAISS
    if os.path.exists(embedding_file) and os.path.exists(index_file):
        embeddings = np.load(embedding_file)
        index = faiss.read_index(index_file)
    else:
        user_messages = [conv[0] for conv in conversations]
        embeddings = model.encode(user_messages, convert_to_tensor=False)
        np.save(embedding_file, embeddings)
        dim = embeddings.shape[1]
        index = faiss.IndexFlatL2(dim)
        index.add(embeddings)
        faiss.write_index(index, index_file)


# Inizializza il sistema
initialize()


# Modello dati per input utente
class QueryRequest(BaseModel):
    query: str


# Endpoint di test per verifica
@app.get("/")
def root():
    return {"message": "Microservizio per query su Linux Commands Conversations è attivo!"}


# Endpoint per recupero delle conversazioni e generazione della risposta
@app.post("/query")
def handle_query(request: QueryRequest):
    global conversations, index

    query = request.query
    query_embedding = model.encode([query], convert_to_tensor=False)

    # Recupera i documenti più simili
    D, I = index.search(query_embedding, k=3)
    if len(I[0]) == 0:
        raise HTTPException(status_code=404, detail="Nessuna conversazione rilevante trovata.")

    # Recupera le conversazioni pertinenti
    retrieved_conversations = [conversations[i] for i in I[0]]
    retrieved_context = retrieved_conversations[0][1]

    # Formatta il contesto per Ollama
    input_text = (
        f"Task: Rispondi alla domanda dell'utente fornendo un comando Linux utile.\n\n"
        f"Query dell'utente: {query}\n\n"
        f"Contesto (risposte pertinenti):\n{retrieved_context}\n\n"
        f"Fornisci una risposta chiara e completa alla query."
    )

    # Generazione della risposta con Ollama
    client = ollama.Client(host="http://localhost:11434")
    response = client.chat(model="llama3.1", messages=[{"role": "user", "content": input_text}])
    response_content = response['message']['content']

    return {
        "query": query,
        "retrieved_context": retrieved_context,
        "response": response_content
    }


if __name__ == "__main__":

    uvicorn.run(app, host="0.0.0.0", port=8000)
