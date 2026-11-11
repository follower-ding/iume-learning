# 只读取依赖名称和环境变量名，不打印等号后面的内容。
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]


def requirement_names(text):
    names = []
    for line in text.splitlines():
        stripped = line.strip()
        if stripped == "" or stripped.startswith("#"):
            continue
        name = stripped
        for mark in ["==", ">=", "<=", "~=", "!=", ">", "<"]:
            if mark in name:
                name = name.split(mark, 1)[0]
                break
        if "[" in name:
            name = name.split("[", 1)[0]
        names.append(name.strip())
    return names


def env_names(text):
    names = []
    for line in text.splitlines():
        stripped = line.strip()
        if stripped == "" or stripped.startswith("#"):
            continue
        if "=" not in stripped:
            continue
        name = stripped.split("=", 1)[0].strip()
        if name == "":
            continue
        names.append(name)
    return names


def read_text(path):
    return path.read_text(encoding="utf-8")


if __name__ == "__main__":
    names = requirement_names(read_text(ROOT / "requirements.txt"))
    print("依赖数量：" + str(len(names)))
    number = 1
    for name in names:
        print("第" + str(number) + "个：" + name)
        number = number + 1
    for name in env_names(read_text(ROOT / ".env.example")):
        print("示例变量：" + name)
    print("不打印等号后面的内容")
