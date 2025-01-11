from fastapi.testclient import TestClient
from RAG import app  # Assicurati che 'app' sia importato correttamente
from rag_utils import load_dataset, extract_conversations, create_faiss_index
from sentence_transformers import SentenceTransformer


# Setup per il test
csv_path = r'../../linux_commands_conversations.csv'  # Sostituisci con il percorso del tuo CSV
model = SentenceTransformer('all-MiniLM-L6-v2')
df = load_dataset(csv_path)
conversations = extract_conversations(df)
index, embeddings = create_faiss_index(conversations, model)

# Test client per FastAPI
client = TestClient(app)

# Test per l'endpoint root ("/")
def test_root():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"message": "Microservizio per query su Linux Commands Conversations è attivo!"}

# Test per l'endpoint /query con una query valida
def test_query_valid():
    query = {"query": "Come posso copiare un file in Linux?"}
    response = client.post("/query", json=query)

    assert response.status_code == 200
    json_response = response.json()
    assert "query" in json_response
    assert "retrieved_context" in json_response
    assert "response" in json_response



# Test di performance per l'endpoint /query
def test_query_performance():
    query = {"query": "Come posso creare una cartella in Linux?"}

    import time
    start_time = time.time()
    response = client.post("/query", json=query)
    end_time = time.time()

    elapsed_time = end_time - start_time
    assert response.status_code == 200
    assert elapsed_time < 50
