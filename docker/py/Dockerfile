FROM python:3.10-slim

ENV PYTHONUNBUFFERED=1

WORKDIR /app

COPY app/ /app/

RUN apt-get update && apt-get install -y libpq-dev python3-dev --no-install-recommends && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    pip install --no-cache-dir -r requirements.txt

 CMD ["python3", "main.py"]
#CMD ["uvicorn", "app:app", "--reload", "--port", "3000"]
#uvicorn app:app --reload --port 3000 --host 0.0.0.0