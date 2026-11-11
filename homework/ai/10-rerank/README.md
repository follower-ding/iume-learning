# 第 10 课作业：重排序

`exercise.py` 的正文与讲义 `lessons/10-rerank/exercise.py` 相同，来自课程仓库

`follower-ding/ai-learning-lab` 的 `main@f1c08846ce3e6ea8decb9f65ce8e6acd83e3e108`。

讲义原文件用 `parents[2]/src` 把课程仓源码放进搜索路径。那个相对路径只有文件放在讲义仓的 `lessons/10-rerank/` 里才成立。这里改成导入已经安装的 `ailab`，所以换一台机器、换一个目录也能跑。

`rerank` 不是这份作业自己写的。它在课程包 `ailab.rerank` 里，和上面同一个 commit 的 `src/ailab/rerank.py`。本目录不复制这份课程源码，也不复制讲义答案或别的课。

## 干净环境里安装并运行

需要 Python 3.11 或以上、`git`，以及能访问 GitHub。在本目录执行：

```bash
cd homework/ai/10-rerank
python3 -m venv .venv
.venv/bin/python -m pip install --upgrade pip
.venv/bin/python -m pip install "ai-learning-lab @ git+https://github.com/follower-ding/ai-learning-lab.git@f1c08846ce3e6ea8decb9f65ce8e6acd83e3e108"
.venv/bin/python -X utf8 exercise.py
echo "exit:$?"
```

`.venv/` 只留在本机，不提交。练习不读 `.env`，不调用模型接口。

这是脚本自检，不是 pytest。退出码 0 只表示脚本里的失败返回点没有走到，不能写成 pytest 的 `10/10`。
