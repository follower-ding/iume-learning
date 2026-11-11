# 空字符串和缺字段不同。空字符串在这里返回 400，非空消息仍送回原文。
from fastapi import FastAPI, HTTPException
from fastapi.testclient import TestClient
from pydantic import BaseModel

app = FastAPI()


class EchoIn(BaseModel):
    message: str


@app.post("/echo")
def echo(body: EchoIn):
    if body.message == "":
        raise HTTPException(status_code=400, detail="消息不能为空")
    return {"message": body.message}


with TestClient(app) as client:
    empty_response = client.post("/echo", json={"message": ""})
    text_response = client.post("/echo", json={"message": "在吗"})
print(empty_response.status_code)
print(empty_response.json()["detail"])
print(text_response.status_code)
print(text_response.json()["message"])
