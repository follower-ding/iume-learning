# /echo 除了送回原文，还返回消息的字符数。
from fastapi import FastAPI
from fastapi.testclient import TestClient
from pydantic import BaseModel

app = FastAPI()


class EchoIn(BaseModel):
    message: str


@app.post("/echo")
def echo(body: EchoIn):
    return {"message": body.message, "length": len(body.message)}


with TestClient(app) as client:
    response = client.post("/echo", json={"message": "你好"})
print(response.json()["message"])
print(response.json()["length"])
