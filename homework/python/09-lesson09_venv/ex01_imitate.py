# 从一段依赖声明里取出包名。输入里没有注释。
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


names = requirement_names("fastapi==0.162.2\nuvicorn==0.54.0\n")
print(len(names))
for name in names:
    print(name)
