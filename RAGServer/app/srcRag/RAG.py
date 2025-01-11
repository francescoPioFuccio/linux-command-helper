from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import os
import uvicorn
import ollama
from rag_utils import load_dataset, extract_conversations, create_faiss_index, retrieve_context
from sentence_transformers import SentenceTransformer
import faiss
import numpy as np

# Inizializza FastAPI
app = FastAPI()

# Variabili globali
csv_path = '../../linux_commands_conversations.csv'
embedding_file = 'user_embeddings.npy'
index_file = 'faiss_index.index'
model = SentenceTransformer('all-MiniLM-L6-v2')
conversations = []
index = None

# Caricamento e inizializzazione
def initialize():
    global conversations, index
    df = load_dataset(csv_path)
    conversations = extract_conversations(df)
    if os.path.exists(embedding_file) and os.path.exists(index_file):
        embeddings = np.load(embedding_file)
        index = faiss.read_index(index_file)
    else:
        index, embeddings = create_faiss_index(conversations, model)

initialize()

# Modello dati per input utente
class QueryRequest(BaseModel):
    query: str

@app.get("/")
def root():
    return {"message": "Microservizio per query su Linux Commands Conversations è attivo!"}

@app.post("/query")
def handle_query(request: QueryRequest):
    query = request.query
    context = retrieve_context(query, model, index, conversations)

    if context is None:
        raise HTTPException(status_code=404, detail="Nessuna conversazione rilevante trovata.")

    retrieved_context = context[0][1]
    input_text = f"Task: Rispondi alla domanda dell'utente fornendo un comando Linux utile.\n\nQuery dell'utente: {query}\n\nContesto (risposte pertinenti):\n{retrieved_context}\n\nFornisci una risposta chiara e completa alla query."

    client = ollama.Client()
    response = client.chat(model="llama3.2-vision", messages=[{"role": "user", "content": input_text}])
    response_content = response['message']['content']

    return {"query": query, "retrieved_context": retrieved_context, "response": response_content}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
