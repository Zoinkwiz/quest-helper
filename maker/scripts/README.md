# Quest Helper Maker — legacy draft conversion

The plugin loads **only** extended Tasks Tracker route JSON (top-level `sections` array). Older files may be:

- **Root-only maker JSON** — object with `formatVersion`, `definitions`, `order`, etc., but no `sections`.
- **Extended route** with deprecated keys inside `questHelperMaker` (`instructionText`, `worldX`/`worldY`/`worldPlane`, `lineId`, `varbitRequirements`, `ITEM` step definitions, routing varbits on step definitions).

Use the converter before upgrading or to normalize backups:

```bash
# Single file → stdout
python maker/scripts/convert_legacy_maker_draft.py path/to/old.json

# Write to file (SOURCE first, then -o DEST — not the other way around)
python maker/scripts/convert_legacy_maker_draft.py old.json -o new.json

# Explicit flags (avoids mixing up input/output on Windows)
python maker/scripts/convert_legacy_maker_draft.py -i old.json -o new.json

# Batch (all .json in a directory)
python maker/scripts/convert_legacy_maker_draft.py --batch path/to/dir/
```

Always keep a **backup** of the original. The script prints errors to stderr and exits non-zero on failure.
