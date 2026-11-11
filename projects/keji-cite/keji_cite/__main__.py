"""让 `python3 -m keji_cite` 能启动命令行。"""

from keji_cite.cli import main

if __name__ == "__main__":
    raise SystemExit(main())
