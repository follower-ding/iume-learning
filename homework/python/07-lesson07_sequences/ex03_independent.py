# 独立：整理标题。strip结果要赋给变量，再用len和in。
text = "  学习Python  "
clean_text = text.strip()
print(f"清理后：{clean_text}")
print(f"字符数：{len(clean_text)}")
print(f"包含Python：{'Python' in clean_text}")
