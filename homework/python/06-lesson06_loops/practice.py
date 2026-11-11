# range 不包含结束值，因此这里得到 1、2、3。
for day in range(1, 4):
    print(f"第{day}天")
total = 0
for number in range(1, 6):
    total = total + number
print(f"1到5的和：{total}")
# 每轮减少一次，条件最终会变成 False。
remaining = 3
while remaining > 0:
    print(f"倒计时：{remaining}")
    remaining = remaining - 1
print("出发！")
for number in range(1, 6):
    if number % 2 == 0:
        continue
    print(f"奇数：{number}")
# 找到第一个符合要求的数，就离开当前循环。
for number in range(1, 6):
    if number == 4:
        print("找到4，结束搜索")
        break
