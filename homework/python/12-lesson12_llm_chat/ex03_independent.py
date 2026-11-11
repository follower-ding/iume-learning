# 空密钥返回说明。非空密钥只报告已配置，不把密钥写进返回值。
def require_api_key(api_key):
    if api_key.strip() == "":
        return "缺少密钥"
    return "已配置"


print(require_api_key(""))
print(require_api_key("placeholder-value"))
