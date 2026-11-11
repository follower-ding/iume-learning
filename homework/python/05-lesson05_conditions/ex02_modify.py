score_text = input("整数成绩：")
try:
    score = int(score_text)
except ValueError:
    print("请输入整数成绩。")
else:
    if score < 0 or score > 100:
        print("成绩范围错误")
    elif score >= 90:
        print("优秀")
    elif score >= 80:
        print("良好")
    elif score >= 60:
        print("及格")
    else:
        print("待提高")
