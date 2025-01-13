FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt /app/requirements.txt
COPY linux_commands_conversations.csv /app/linux_commands_conversations.csv

RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 8083

COPY . /app/

CMD ["python", "app/srcRag/RAG.py"]
