"""Helpers for maker JSON: legacy root draft or extended route with ``questHelperMaker``."""

from __future__ import annotations

import json
from pathlib import Path
from typing import Any


def load_maker_json(path: Path) -> tuple[dict[str, Any], dict[str, Any] | None]:
    """
    Load JSON from disk.

    Returns ``(draft_root, envelope)`` where ``draft_root`` is the mutable maker snapshot
    (``formatVersion``, ``definitions``, …). ``envelope`` is the full document when the file
    used extended route shape (so callers can write back without dropping ``sections``).
    """
    doc: Any = json.loads(path.read_text(encoding="utf-8"))
    if not isinstance(doc, dict):
        raise SystemExit(f"Expected JSON object: {path}")
    qhm = doc.get("questHelperMaker")
    if isinstance(qhm, dict) and qhm.get("formatVersion") == 1:
        return qhm, doc
    return doc, None


def write_maker_document(draft_root: dict[str, Any], envelope: dict[str, Any] | None, path: Path, *, indent: int = 2) -> None:
    """Write maker snapshot; if ``envelope`` was from :func:`load_maker_json`, preserve route wrapper."""
    path.parent.mkdir(parents=True, exist_ok=True)
    if envelope is not None:
        out = dict(envelope)
        out["questHelperMaker"] = draft_root
        qn = draft_root.get("questName")
        if isinstance(qn, str) and qn.strip():
            out["name"] = qn.strip()
        path.write_text(json.dumps(out, indent=indent), encoding="utf-8")
    else:
        path.write_text(json.dumps(draft_root, indent=indent), encoding="utf-8")
