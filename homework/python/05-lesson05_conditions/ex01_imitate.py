minutes_text = input("学习分钟数：")
try:
    minutes = int(minutes_text)
except ValueError:
    print("请输入整数分钟数。")
else:
    if minutes >= 30:
        print("达到目标")
    else:
        print("继续加油")
