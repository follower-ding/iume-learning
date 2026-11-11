# 修改：维护记录。先追加，再按索引改第二项，最后按值删40。
records = [10, 20, 40]
records.append(30)
records[1] = 25
records.remove(40)
print(records)
