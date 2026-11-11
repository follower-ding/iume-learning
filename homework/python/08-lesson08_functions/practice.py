# 每个函数只完成一件事；输入约定为非负整数分钟组成的列表。
def total_minutes(records):
    total = 0
    for minutes in records:
        total = total + minutes
    return total


def average_minutes(records):
    # 空列表没有平均值，用 None 表示，不做除零运算。
    if len(records) == 0:
        return None
    return total_minutes(records) / len(records)


def remaining_minutes(total, target):
    # total 和 target 都是非负整数；超出目标时无需再学负数分钟。
    if total >= target:
        return 0
    return target - total


# 以下是顺序执行的调用部分，不需要额外入口封装。
records = [25, 40, 15]
target = 90
total = total_minutes(records)
average = average_minutes(records)
remaining = remaining_minutes(total, target)
print(f"学习次数：{len(records)}")
print(f"累计分钟：{total}")
if average is None:
    print("平均分钟：暂无记录")
else:
    print(f"平均分钟：{average:.2f}")
print(f"距离目标：{remaining} 分钟")
