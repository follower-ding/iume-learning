# 每天二十五分钟，直到累计至少六十分钟。
days = 0
total = 0
while total < 60:
    days = days + 1
    total = total + 25
    print(f"第{days}天：累计{total}分钟")
print(f"需要{days}天")
