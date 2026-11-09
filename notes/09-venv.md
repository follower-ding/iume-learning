# 第9课：虚拟环境与依赖

## 学了啥
岗位短轨开场：在项目根用 `python3 -m venv .venv` 建独立解释器，再用 `.venv/bin/python -m pip install -r requirements.txt` 装直接依赖。`.env.example` 可提交，真实密钥只写本机 `.env`。本课代码用标准库解析 `requirements.txt` 包名和 `.env.example` 变量名（不打印等号右边）；三级练习：两行依赖、跳过注释空行、只收变量名。

## 为什么能跑
`.venv` 自带解释器 + `site-packages`；激活或直接调用该解释器时，`sys.path` 指向项目货架，装的包互不踩系统。解析依赖时先跳过空行/`#`，再按 `==`/`>=` 等切开取包名（双字符符号优先），方括号前截断；环境变量只取 `=` 左边。

## 卡过啥
- 用错解释器 → 后面课 `ModuleNotFoundError`；短轨一律 `.venv` 里的 python。
- 裸 `pip` 可能装进系统 → 用 `python -m pip` 挂在 venv 上。
- 先切单个 `>` → 包名带 `=`；双字符符号要先检查。
- 别把 `.env` / `.venv` 推进 Git。
