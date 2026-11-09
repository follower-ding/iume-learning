# 第10课：第一个接口（FastAPI）

## 学了啥
岗位短轨第二课：用 FastAPI 登记 `GET /health`（进程是否应答）和 `POST /echo`（原样送回消息）。`EchoIn(BaseModel)` 约束正文字段；`TestClient` 进程内发请求，课检不依赖长期 uvicorn。三级练习：只测 health；echo 附带 `length`；空消息 `HTTPException` 400。

## 为什么能跑
装饰器把函数登记到「方法+路径」；请求进来才调用。字典返回值被序列成 JSON；`response.json()` 再变回字典。Pydantic 按字段清单校验：缺字段 422；空字符串仍是 str，业务拒绝要自己 raise。`if __name__ == "__main__"` 区分「直接跑打印」和「被 uvicorn 导入只暴露 app」。

## 卡过啥
- 必须用 `.venv` 解释器（含 fastapi / httpx2）。
- 键名/方法不对 → 422 / 405。
- 空消息不会自动 422，要显式 400。
- 顶层 print 会被 uvicorn 导入时触发。
