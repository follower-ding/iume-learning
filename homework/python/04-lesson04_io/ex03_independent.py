first_text = input("第一天分钟数：")
second_text = input("第二天分钟数：")
try:
    first = int(first_text)
    second = int(second_text)
except ValueError:
    print("两天都需要输入整数。")
else:
    print(f"两天共学习{first + second}分钟。")
