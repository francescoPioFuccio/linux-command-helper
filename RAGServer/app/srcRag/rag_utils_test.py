import pytest
from rag_utils import load_dataset, extract_conversations, create_faiss_index, retrieve_context
from sentence_transformers import SentenceTransformer
import faiss

# Setup
csv_path = r'../../linux_commands_conversations.csv'  # Sostituisci con il percorso del tuo CSV
embedding_file = 'user_embeddings.npy'
index_file = 'faiss_index.index'
model = SentenceTransformer('all-MiniLM-L6-v2')

# Test per caricare il dataset
def test_load_dataset():
    try:
        df = load_dataset(csv_path)
        assert df is not None, "Il dataset è vuoto"
        assert "conversations" in df.columns, "La colonna 'conversations' non è presente nel dataset"
    except Exception as e:
        pytest.fail(f"Errore durante il caricamento del dataset: {str(e)}")

# Test per estrarre le conversazioni
def test_extract_conversations():
    df = load_dataset(csv_path)
    conversations = extract_conversations(df)
    assert isinstance(conversations, list), "Le conversazioni non sono in formato lista"
    assert len(conversations) > 0, "Le conversazioni estratte sono vuote"
    assert isinstance(conversations[0], tuple), "Ogni conversazione dovrebbe essere una tupla"
    assert len(conversations[0]) == 2, "Ogni conversazione dovrebbe contenere un messaggio utente e una risposta assistente"

# Test per creare l'indice FAISS
def test_create_faiss_index():
    df = load_dataset(csv_path)
    conversations = extract_conversations(df)
    index, embeddings = create_faiss_index(conversations, model)

    assert isinstance(index, faiss.IndexFlatL2), "L'indice non è di tipo faiss.IndexFlatL2"
    assert embeddings.shape[0] == len(conversations), "Il numero di embeddings non corrisponde al numero di conversazioni"

# Test per recuperare il contesto
def test_retrieve_context():
    df = load_dataset(csv_path)
    conversations = extract_conversations(df)
    index, _ = create_faiss_index(conversations, model)

    query = "Come posso copiare un file in Linux?"
    context = retrieve_context(query, model, index, conversations)

    assert context is not None, "Nessun contesto trovato"
    assert isinstance(context, list), "Il contesto dovrebbe essere una lista"
    assert len(context) > 0, "Il contesto è vuoto"