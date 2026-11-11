# input 会等待输入，按回车后把文字交给变量。
name = input("请输入姓名：")
minutes_text = input("请输入今天学习的整数分钟数：")
print(f"输入的分钟文本：{minutes_text}，类型：{type(minutes_text)}")
# 只把可能转换失败的操作放进 try。
try:
    minutes = int(minutes_text)
except ValueError:
    print("分钟数必须是整数，例如 25；请重新运行。")
else:
    print(f"{name}今天学习了{minutes}分钟。")
    print(f"再学10分钟，总计{minutes + 10}分钟。")
