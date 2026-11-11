age_text = input("请输入整数年龄：")
try:
    age = int(age_text)
except ValueError:
    print("请输入整数年龄。")
else:
    print(f"明年年龄：{age + 1}")
