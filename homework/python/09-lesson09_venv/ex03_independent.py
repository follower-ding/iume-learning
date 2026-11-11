# 只收集变量名。等号后面的占位文字不能出现在输出里。
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


text = "LLM_API_KEY=placeholder-value\n\n# 注释\nBADLINE\nLLM_MODEL=\n"
names = env_names(text)
for name in names:
    print(name)
print("条数：" + str(len(names)))
