# 用名称保存值；五个值有不同的类型。
name = "小雨"
minutes = 20
hours = 1.5
finished = True
next_topic = None
print(name)
print(minutes)
print(hours)
print(finished)
print(next_topic)
print(type(name))
print(type(minutes))
print(type(hours))
print(type(finished))
print(type(next_topic))
# 重新赋值会让 minutes 指向新值。
minutes = 30
print(minutes)
# 转换产生新值，不会自动改变原来的文本。
minutes_text = "20"
print(int(minutes_text) + 5)
print(float("2.5"))
print("分钟：" + str(20))
