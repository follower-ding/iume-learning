# 在循环之前准备累计值，只加偶数。
total = 0
for number in range(1, 7):
    if number % 2 == 0:
        total = total + number
print(f"偶数和：{total}")
