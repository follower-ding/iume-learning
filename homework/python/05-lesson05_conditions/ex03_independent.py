age_text = input("整数年龄：")
try:
    age = int(age_text)
except ValueError:
    print("请输入整数年龄。")
else:
    if age < 0 or age > 120:
        print("年龄范围错误")
    elif age <= 5:
        print("免费")
    elif age <= 17:
        print("儿童票")
    else:
        print("成人票")
