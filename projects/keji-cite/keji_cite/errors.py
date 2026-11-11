"""运行失败时抛出这些异常。命令行只打印消息，不打印栈。"""


class KejiError(Exception):
    """用户能看懂的失败。"""


class NotesError(KejiError):
    """笔记目录不存在，或里面没有可索引的 Markdown。"""


class MissingIndexError(KejiError):
    """还没有建过索引。"""


class CorruptIndexError(KejiError):
    """索引读不出来，或和当前代码的参数对不上。"""


class ThresholdError(KejiError):
    """阈值文件缺失或损坏。不退回课程里的 0.30。"""
