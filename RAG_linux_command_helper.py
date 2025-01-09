import pandas as pd
import ast
import faiss
import numpy as np
from sentence_transformers import SentenceTransformer
import ollama
import os

# 1. Caricamento del dataset CSV
csv_path = r'C:\Users\franc\Downloads\linux_commands_conversations.csv'  # Sostituisci con il percorso del tuo file CSV
df = pd.read_csv(csv_path)

# Rimuovi gli spazi extra nelle intestazioni
df.columns = df.columns.str.strip()

# Converte la colonna 'conversations' da stringa a una lista di dizionari Python
df['conversations'] = df['conversations'].apply(ast.literal_eval)

# Estrai le conversazioni dal dataset: una coppia di messaggi (user, assistant)
conversations = []
for row in df['conversations']:
    user_message = next(item['content'] for item in row if item['role'] == 'user')
    assistant_response = next(item['content'] for item in row if item['role'] == 'assistant')
    conversations.append((user_message, assistant_response))

# Controlla che i dati siano correttamente estratti
print("Esempio di conversazione:")
print(conversations[0])  # Stampa il primo esempio

# 2. Carica il modello per generare embeddings
model = SentenceTransformer('all-MiniLM-L6-v2')  # Inizializza il modello

embedding_file = 'user_embeddings.npy'  # Nome del file dove salveremo gli embeddings
index_file = 'faiss_index.index'  # Nome del file dove salveremo l'indice FAISS

# Controlla se esistono già gli embeddings e l'indice
if os.path.exists(embedding_file) and os.path.exists(index_file):
    # Carica gli embeddings e l'indice
    embeddings = np.load(embedding_file)
    index = faiss.read_index(index_file)
    print("Embeddings e indice FAISS caricati.")
else:
    # Creazione degli embeddings per i messaggi utente (user messages)
    user_messages = [conv[0] for conv in conversations]

    print("Creazione degli embeddings per i messaggi dell'utente...")

    embeddings = model.encode(user_messages, convert_to_tensor=False)

    # Salva gli embeddings su disco
    np.save(embedding_file, embeddings)
    print(f"Shape degli embeddings: {embeddings.shape}")

    # Creazione dell'indice FAISS per il recupero
    dim = embeddings.shape[1]  # La dimensione dell'embedding
    index = faiss.IndexFlatL2(dim)  # Usa la distanza euclidea (L2)
    index.add(embeddings)  # Aggiungi gli embeddings all'indice

    # Salva l'indice FAISS
    faiss.write_index(index, index_file)
    print("Indice FAISS creato e embeddings aggiunti.")

# 3. Recupero dei documenti pertinenti in base alla query dell'utente
query = ("give me the linux command to show the files of a directory")
query_embedding = model.encode([query], convert_to_tensor=False)

print(f"Creazione embedding per la query: '{query}'...")

# Esegui la ricerca nel database
D, I = index.search(query_embedding, k=3)  # Recupera i 3 documenti più simili
print(f"Indici dei documenti recuperati: {I[0]}")

# Stampa i documenti recuperati
retrieved_conversations = [conversations[i] for i in I[0]]
print("Conversazioni recuperate:")
for i, (user_msg, assistant_msg) in enumerate(retrieved_conversations):
    print(f"Documento {i + 1}:\nUser: {user_msg}\nAssistant: {assistant_msg}\n")

# 4. Generazione della risposta con il modello "linux-command" su Ollama
# Creiamo il contesto per il modello concatenando la query e la risposta precedente recuperata
retrieved_context = retrieved_conversations[0][1]  # Risposta dell'assistente dal documento più simile
input_text = f"Domanda: {query}\nRisposta precedente: {retrieved_context}"

print(f"Input al modello di generazione: {input_text}")

# Usa il modello di Ollama per generare una risposta
client = ollama.Client()

response = client.chat(model="linux_command_helper_new", messages=[{"role": "user", "content": input_text}])

# Estrai la risposta dal campo 'content' della risposta
response_content = response['message']['content']

print(f"Risposta generata dal modello: {response_content}")