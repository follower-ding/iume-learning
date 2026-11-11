# 分数由调用者保证在 0 到 100 之间，60 分包含在及格范围内。
def count_passed(scores):
    count = 0
    for score in scores:
        if score >= 60:
            count = count + 1
    return count


print(count_passed([59, 60, 100]))
print(count_passed([]))
