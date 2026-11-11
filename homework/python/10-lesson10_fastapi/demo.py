# /health 报告进程是否在应答，/echo 把收到的消息原样送回。
from fastapi import FastAPI
from fastapi.testclient import TestClient
from pydantic import BaseModel

app = FastAPI()


class EchoIn(BaseModel):
    # message 预期是字符串。FastAPI 按这个字段清单检查正文。
    message: str


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/echo")
def echo(body: EchoIn):
    return {"message": body.message}


def call_demo():
    lines = []
    with TestClient(app) as client:
        health_response = client.get("/health")
        echo_response = client.post("/echo", json={"message": "你好"})
        lines.append(str(health_response.status_code))
        lines.append(health_response.json()["status"])
        lines.append(str(echo_response.status_code))
        lines.append(echo_response.json()["message"])
    return lines


if __name__ == "__main__":
    for line in call_demo():
        print(line)
