"""定位仓库、笔记和本项目自己的文件。不访问笔记以外的目录。"""

from pathlib import Path

from keji_cite.errors import NotesError


def project_root() -> Path:
    """`projects/keji-cite/`。本文件在 `keji_cite/` 里面，所以往上两级。"""
    return Path(__file__).resolve().parents[1]


def repo_root() -> Path:
    """学习仓根目录：`projects/keji-cite` 再往上两级。"""
    root = project_root().parents[1]
    if not (root / "notes").is_dir() or not (root / "projects" / "keji-cite").is_dir():
        raise NotesError("找不到学习仓根目录（需要同时有 notes/ 和 projects/keji-cite/）")
    return root


def default_notes_dir() -> Path:
    return repo_root() / "notes"


def default_index_path() -> Path:
    return project_root() / ".index" / "index.json"


def default_questions_path() -> Path:
    return project_root() / "calibration" / "questions.json"


def default_threshold_path() -> Path:
    return project_root() / "calibration" / "threshold.json"


def default_record_path() -> Path:
    return project_root() / "calibration" / "record.md"
