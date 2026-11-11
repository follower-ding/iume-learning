# 先把输入文本转换为整数，再检查范围和等级。
score_text = input("请输入0到100的整数成绩：")
try:
    score = int(score_text)
except ValueError:
    print("输入错误：请输入整数。")
else:
    if score < 0 or score > 100:
        print("范围错误：成绩必须在0到100之间。")
    elif score >= 90:
        print("等级：优秀")
    elif score >= 60:
        print("等级：及格")
    else:
        print("等级：待提高")
