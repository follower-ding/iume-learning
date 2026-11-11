# 找最长的一次学习记录；输入为非负整数列表，不修改原列表。
def longest_session(records):
    if len(records) == 0:
        return None
    longest = records[0]
    for minutes in records:
        if minutes > longest:
            longest = minutes
    return longest


print(longest_session([20, 40, 10]))
print(longest_session([0]))
print(longest_session([]))
