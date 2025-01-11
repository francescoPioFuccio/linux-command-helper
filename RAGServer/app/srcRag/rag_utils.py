import pandas as pd
import ast
import numpy as np
import faiss
from sentence_transformers import SentenceTransformer

# Caricamento del dataset
def load_dataset(csv_path):
    df = pd.read_csv(csv_path)
    df.columns = df.columns.str.strip()
    df['conversations'] = df['conversations'].apply(ast.literal_eval)
    return df

# Estrazione delle conversazioni (per il contesto)
def extract_conversations(df):
    conversations = []
    for row in df['conversations']:
        user_message = next(item['content'] for item in row if item['role'] == 'user')
        assistant_response = next(item['content'] for item in row if item['role'] == 'assistant')
        conversations.append((user_message, assistant_response))
    return conversations

# Creazione dell'indice FAISS
def create_faiss_index(conversations, model, embedding_file='user_embeddings.npy', index_file='faiss_index.index'):
    user_messages = [conv[0] for conv in conversations]
    embeddings = model.encode(user_messages, convert_to_tensor=False)

    # Salvataggio embeddings e creazione indice
    np.save(embedding_file, embeddings)
    dim = embeddings.shape[1]
    index = faiss.IndexFlatL2(dim)
    index.add(embeddings)
    faiss.write_index(index, index_file)
    return index, embeddings

# Recupero del contesto
def retrieve_context(query, model, index, conversations, k=3):
    query_embedding = model.encode([query], convert_to_tensor=False)
    D, I = index.search(query_embedding, k)
    if len(I[0]) == 0:
        return None
    retrieved_conversations = [conversations[i] for i in I[0]]
    return retrieved_conversations
