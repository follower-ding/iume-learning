# 跳过空行和整行注释，再取出包名。
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


text = "# 注释\n\nhttpx==0.28.1\n  \n# 另一行\nhttpx2==2.16.0\n"
names = requirement_names(text)
for name in names:
    print(name)
