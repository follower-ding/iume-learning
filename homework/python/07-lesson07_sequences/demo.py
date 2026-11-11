# 列表按顺序保存多条记录，索引从零开始。
records = [25, 40, 15]
print(f"首条：{records[0]}")
print(f"末条：{records[-1]}")
print(f"前两条：{records[:2]}")
records.append(20)
records[0] = 30
records.remove(40)
print(f"更新后：{records}")
print(f"条数：{len(records)}")
total = 0
for minutes in records:
    total = total + minutes
print(f"累计：{total}")
# 空列表不能读取第一个元素，先判断长度。
empty = []
if len(empty) == 0:
    print("没有记录")
else:
    print(empty[0])
# 字符串方法产生新字符串，不会原地改写原文。
title = "  Python入门  "
clean_title = title.strip()
print(f"整理后：{clean_title}")
print(f"前六字：{clean_title[:6]}")
print(f"包含入门：{'入门' in clean_title}")
print(f"替换后：{clean_title.replace('入门', '基础')}")
print(f"原文：{clean_title}")
for character in "学习":
    print(f"字符：{character}")
