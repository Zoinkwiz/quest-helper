#!/usr/bin/env python3
"""
Convert legacy Quest Helper Maker / draft JSON into canonical extended Tasks Tracker route JSON.

Accepted inputs:
  1) Extended route: top-level JSON object with a "sections" array (Tasks Tracker shape).
     questHelperMaker is normalized in-place when present.
  2) Root-only maker snapshot: object with formatVersion/definitions/order (no top-level sections).
     Wrapped as a minimal route; items[] may be empty because the full draft lives in questHelperMaker.

Output: pretty-printed JSON suitable for construct-draft.json or Import route JSON in the maker.

Back up originals before converting. Stdlib only (Python 3.9+).
"""

from __future__ import annotations

import argparse
import copy
import json
import sys
from pathlib import Path
from typing import Any, Dict, List, Optional


DRAFT_FORMAT_VERSION = 1
DEFAULT_TASK_TYPE = "LEAGUE_5"


def _is_route_envelope(obj: Any) -> bool:
    return isinstance(obj, dict) and isinstance(obj.get("sections"), list)


def _first_non_blank(a: Optional[str], b: Optional[str]) -> Optional[str]:
    if a is not None and str(a).strip():
        return str(a).strip()
    if b is not None and str(b).strip():
        return str(b).strip()
    return None


def _upgrade_tree_kinds(node: Any) -> None:
    if not isinstance(node, dict):
        return
    k = (node.get("kind") or "").strip().upper()
    if k == "CAPTURED_ITEM":
        node["kind"] = "ITEM"
    elif k == "ROUTING_VARBIT":
        node["kind"] = "ORDER_VARBIT"
    elif k == "INLINE_VARBIT":
        node["kind"] = "VARBIT"
    for ch in node.get("children") or []:
        _upgrade_tree_kinds(ch)


def _normalize_step_state(st: Dict[str, Any]) -> None:
    """Canonical keys on a draft step state object (mutates)."""
    note = st.get("note")
    instr = st.get("instructionText")
    if (note is None or str(note).strip() == "") and instr is not None:
        st["note"] = instr
    st.pop("instructionText", None)

    loc = st.get("location")
    wx, wy, wp = st.get("worldX"), st.get("worldY"), st.get("worldPlane")
    if not isinstance(loc, dict) and wx is not None and wy is not None and wp is not None:
        st["location"] = {"x": int(wx), "y": int(wy), "plane": int(wp)}
    st.pop("worldX", None)
    st.pop("worldY", None)
    st.pop("worldPlane", None)

    kind = st.get("kind")
    if kind == "ITEM":
        rid = int(st.get("rawId") or 0)
        st["kind"] = "TEXT"
        st["rawId"] = 0
        atts: List[Dict[str, Any]] = list(st.get("attachedRequirements") or [])
        has_item = any(
            isinstance(a, dict)
            and str(a.get("kind") or "").upper() == "ITEM"
            and int(a.get("itemRawId") or 0) == rid
            for a in atts
        )
        if rid != 0 and not has_item:
            atts.insert(0, {"kind": "ITEM", "itemRawId": rid, "attachmentHighlighted": False})
        st["attachedRequirements"] = atts

    st.pop("linkedRequirementRawId", None)


def _normalize_order_line_state(line: Dict[str, Any]) -> None:
    slot = _first_non_blank(line.get("orderSlotId"), line.get("lineId"))
    if slot:
        line["orderSlotId"] = slot
    line.pop("lineId", None)
    sr = line.get("stepRequirement")
    if sr is not None:
        _upgrade_tree_kinds(sr)


def _find_order_line_by_slot(order: List[Dict[str, Any]], slot: str) -> Optional[Dict[str, Any]]:
    for ol in order:
        if ol.get("sectionDivider"):
            continue
        if str(ol.get("orderSlotId") or "") == slot:
            return ol
    return None


def _find_step_def(defs: List[Dict[str, Any]], step_id: Optional[str]) -> Optional[Dict[str, Any]]:
    if not step_id:
        return None
    for d in defs:
        if str(d.get("stepId") or "") == step_id:
            return d
    return None


def _hoist_routing_varbits_from_definitions(maker: Dict[str, Any]) -> None:
    defs: List[Dict[str, Any]] = list(maker.get("definitions") or [])
    order: List[Dict[str, Any]] = list(maker.get("order") or [])
    for step in defs:
        atts = step.get("attachedRequirements")
        if not isinstance(atts, list):
            continue
        to_remove: List[Dict[str, Any]] = []
        for a in atts:
            if not isinstance(a, dict):
                continue
            if str(a.get("kind") or "").upper() != "VARBIT":
                continue
            slot = a.get("orderSlotId")
            if not slot or not str(slot).strip():
                continue
            target = _find_order_line_by_slot(order, str(slot).strip())
            if target is None:
                continue
            to_remove.append(a)
            oa = target.setdefault("attachedRequirements", [])
            if not isinstance(oa, list):
                target["attachedRequirements"] = []
                oa = target["attachedRequirements"]
            # avoid duplicate if already present
            dup = False
            for ex in oa:
                if isinstance(ex, dict) and str(ex.get("kind") or "").upper() == "VARBIT":
                    if str(ex.get("orderSlotId") or "") == str(slot):
                        dup = True
                        break
            if not dup:
                oa.append(copy.deepcopy(a))
        for r in to_remove:
            atts.remove(r)


def _apply_legacy_def_linked_to_order(maker: Dict[str, Any]) -> None:
    defs: List[Dict[str, Any]] = list(maker.get("definitions") or [])
    linked_by_step: Dict[str, int] = {}
    for d in defs:
        sid = d.get("stepId")
        leg = d.get("linkedRequirementRawId")
        if sid and leg is not None:
            linked_by_step[str(sid)] = int(leg)
    for d in defs:
        d.pop("linkedRequirementRawId", None)
    for line in maker.get("order") or []:
        if not isinstance(line, dict) or line.get("sectionDivider"):
            continue
        if line.get("linkedRequirementRawId") is not None:
            continue
        ref = line.get("refStepId")
        if ref and ref in linked_by_step:
            line["linkedRequirementRawId"] = linked_by_step[ref]


def _migrate_varbit_requirements_list(maker: Dict[str, Any]) -> None:
    vreq = maker.get("varbitRequirements")
    if not isinstance(vreq, list) or not vreq:
        maker["varbitRequirements"] = []
        return
    defs: List[Dict[str, Any]] = list(maker.get("definitions") or [])
    order: List[Dict[str, Any]] = list(maker.get("order") or [])
    for v in vreq:
        if not isinstance(v, dict):
            continue
        slot = _first_non_blank(v.get("orderSlotId"), v.get("lineId"))
        if not slot:
            continue
        match = _find_order_line_by_slot(order, slot)
        if match is None:
            continue
        oa = match.setdefault("attachedRequirements", [])
        if not isinstance(oa, list):
            match["attachedRequirements"] = []
            oa = match["attachedRequirements"]
        has = False
        for ex in oa:
            if isinstance(ex, dict) and str(ex.get("kind") or "").upper() == "VARBIT":
                if str(ex.get("orderSlotId") or "") == slot:
                    has = True
                    break
        if has:
            continue
        op = v.get("operation") or "EQUAL"
        oa.append(
            {
                "kind": "VARBIT",
                "varbitId": int(v.get("varbitId") or 0),
                "varbitRequiredValue": int(v.get("requiredValue") or 0),
                "varbitOperation": str(op),
                "varbitDisplayText": v.get("displayText"),
                "attachmentHighlighted": False,
                "orderSlotId": slot,
            }
        )
        sid = v.get("structId")
        if sid is not None:
            ref = match.get("refStepId")
            step = _find_step_def(defs, str(ref) if ref else None)
            if step is not None and step.get("structId") is None:
                step["structId"] = int(sid)
    maker["varbitRequirements"] = []


def _synthesize_step_requirement_from_linked(line: Dict[str, Any]) -> None:
    if line.get("sectionDivider"):
        return
    if line.get("stepRequirement") is not None:
        return
    linked = line.get("linkedRequirementRawId")
    if linked is None or int(linked) <= 0:
        return
    rid = int(linked)
    line["stepRequirement"] = {
        "kind": "INVERT",
        "logic": None,
        "children": [{"kind": "ITEM", "itemRawId": rid, "children": []}],
    }


def normalize_quest_helper_maker(maker: Dict[str, Any]) -> Dict[str, Any]:
    """Return a deep-copied, canonical maker snapshot (DraftState-shaped)."""
    m = copy.deepcopy(maker)
    m["formatVersion"] = int(m.get("formatVersion") or DRAFT_FORMAT_VERSION)

    for st in m.get("definitions") or []:
        if isinstance(st, dict):
            _normalize_step_state(st)

    for line in m.get("order") or []:
        if isinstance(line, dict):
            _normalize_order_line_state(line)

    _hoist_routing_varbits_from_definitions(m)
    _apply_legacy_def_linked_to_order(m)
    _migrate_varbit_requirements_list(m)

    defs = list(m.get("definitions") or [])
    for line in m.get("order") or []:
        if isinstance(line, dict):
            _synthesize_step_requirement_from_linked(line)
            sr = line.get("stepRequirement")
            if sr is not None:
                _upgrade_tree_kinds(sr)

    return m


def wrap_root_maker_as_route(root: Dict[str, Any]) -> Dict[str, Any]:
    maker = normalize_quest_helper_maker(root)
    name = maker.get("questName") or "Converted Route"
    return {
        "name": str(name),
        "taskType": DEFAULT_TASK_TYPE,
        "author": "Quest Helper Maker (converted)",
        "description": None,
        "sections": [
            {
                "name": str(name),
                "description": None,
                "items": [],
            }
        ],
        "questHelperMaker": maker,
    }


def normalize_route_document(route: Dict[str, Any]) -> Dict[str, Any]:
    out = copy.deepcopy(route)
    if "taskType" not in out or not str(out.get("taskType") or "").strip():
        out["taskType"] = DEFAULT_TASK_TYPE
    qm = out.get("questHelperMaker")
    if isinstance(qm, dict):
        out["questHelperMaker"] = normalize_quest_helper_maker(qm)
    # ensure sections is non-empty list (validation)
    sections = out.get("sections")
    if not isinstance(sections, list) or not sections:
        out["sections"] = [{"name": str(out.get("name") or "Tasks"), "description": None, "items": []}]
    return out


def convert_document(obj: Any) -> Dict[str, Any]:
    if not isinstance(obj, dict):
        raise ValueError("Root JSON must be an object")
    if _is_route_envelope(obj):
        return normalize_route_document(obj)
    # Heuristic: maker root snapshot
    if "definitions" in obj or "order" in obj or "formatVersion" in obj:
        return wrap_root_maker_as_route(obj)
    raise ValueError(
        "Unrecognized JSON: need extended route (top-level sections[]) "
        "or maker DraftState (definitions/order/formatVersion)"
    )


def _write_json(path: Path, data: Any) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def main() -> int:
    epilog = """
Examples:
  %(prog)s OLD.json -o NEW.json              # read OLD, write NEW (positional must be the SOURCE file)
  %(prog)s -i OLD.json -o NEW.json          # explicit input/output (recommended on Windows)
  %(prog)s OLD.json                         # print converted JSON to stdout
  cat OLD.json | %(prog)s                   # read from stdin
""".rstrip()
    ap = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=epilog,
    )
    ap.add_argument(
        "input",
        nargs="?",
        metavar="INPUT.json",
        help="Source .json file to read (omit to use stdin, or use -i instead)",
    )
    ap.add_argument(
        "-i",
        "--input",
        dest="input_path",
        metavar="FILE",
        help="Source .json file (same as positional INPUT; use this if you prefer flags before -o)",
    )
    ap.add_argument("-o", "--output", metavar="FILE", help="Output file (default: stdout)")
    ap.add_argument(
        "--batch",
        metavar="DIR",
        help="Convert every *.json in DIR; writes *.converted.json alongside",
    )
    args = ap.parse_args()

    if args.batch:
        d = Path(args.batch)
        if not d.is_dir():
            print(f"Not a directory: {d}", file=sys.stderr)
            return 2
        n = 0
        for p in sorted(d.glob("*.json")):
            if p.name.endswith(".converted.json"):
                continue
            try:
                raw = json.loads(p.read_text(encoding="utf-8"))
                out = convert_document(raw)
                outp = p.with_suffix("").with_name(p.stem + ".converted.json")
                _write_json(outp, out)
                print(f"OK {p} -> {outp}")
                n += 1
            except Exception as e:
                print(f"FAIL {p}: {e}", file=sys.stderr)
                return 1
        if n == 0:
            print("No *.json files found.", file=sys.stderr)
            return 2
        return 0

    src = args.input_path or args.input
    if src:
        p = Path(src)
        if not p.is_file():
            print(f"Error: input file not found or not a file: {p.resolve()}", file=sys.stderr)
            print(
                "Hint: the path after -o is the OUTPUT file. Put the SOURCE file first, e.g.\n"
                f"  python {Path(sys.argv[0]).name} .\\\\object_npc_ordered.json -o .\\\\new_object_npc_ordered.json\n"
                "or use explicit flags:\n"
                f"  python {Path(sys.argv[0]).name} -i .\\\\object_npc_ordered.json -o .\\\\new_object_npc_ordered.json",
                file=sys.stderr,
            )
            return 2
        try:
            raw_text = p.read_text(encoding="utf-8")
        except OSError as e:
            print(f"Error: could not read {p}: {e}", file=sys.stderr)
            return 2
    else:
        raw_text = sys.stdin.read()

    try:
        obj = json.loads(raw_text)
        converted = convert_document(obj)
    except Exception as e:
        print(str(e), file=sys.stderr)
        return 1

    text = json.dumps(converted, indent=2, ensure_ascii=False) + "\n"
    if args.output:
        Path(args.output).write_text(text, encoding="utf-8")
    else:
        sys.stdout.write(text)
    return 0


if __name__ == "__main__":
    sys.exit(main())
