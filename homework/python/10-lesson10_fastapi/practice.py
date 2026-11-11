# 练习完整版：登记 /health 与 /echo，进程内客户端核对状态码与正文。
from fastapi import FastAPI
from fastapi.testclient import TestClient
from pydantic import BaseModel

app = FastAPI()


class EchoIn(BaseModel):
    message: str


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/echo")
def echo(body: EchoIn):
    return {"message": body.message}


with TestClient(app) as client:
    health_response = client.get("/health")
    echo_response = client.post("/echo", json={"message": "你好"})
print(health_response.status_code)
print(health_response.json()["status"])
print(echo_response.status_code)
print(echo_response.json()["message"])
