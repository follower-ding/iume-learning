"""探针：hours 布尔 / 超范围 / 非 int → ToolArgumentError；合法 2 → 合计 60。"""

from ailab.errors import ToolArgumentError
from ailab.tools import quote_late_checkout


def main() -> int:
    ok = quote_late_checkout({"hours": 2})
    print(f"合法 hours=2：{ok}")
    if "合计 60 元" not in ok:
        print("期望合计 60 元")
        return 1

    for bad in (True, False, 13, 0, 1.5, "2", None):
        try:
            quote_late_checkout({"hours": bad})
            print(f"意外通过：hours={bad!r}")
            return 1
        except ToolArgumentError as exc:
            print(f"拒绝 hours={bad!r}：{exc}")
        except TypeError as exc:
            # None 等可能在别处炸；统一当失败信号也可，这里明确打印
            print(f"类型错误 hours={bad!r}：{exc}")
            return 1
    print("ok: 布尔单独拒绝；仅 1..12 的 int 过关")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
