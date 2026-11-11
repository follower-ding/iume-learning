# 按照每次阅读页数求总页数；输入为非负整数列表。
def total_pages(records):
    total = 0
    for pages in records:
        total = total + pages
    return total


print(total_pages([3, 5, 2]))
print(total_pages([]))
