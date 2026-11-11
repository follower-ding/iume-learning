# 只请求 /health，确认进程内客户端能拿到状态码和状态文字。
from fastapi import FastAPI
from fastapi.testclient import TestClient

app = FastAPI()


@app.get("/health")
def health():
    return {"status": "ok"}


with TestClient(app) as client:
    response = client.get("/health")
print(response.status_code)
print(response.json()["status"])
