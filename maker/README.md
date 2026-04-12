# Quest Helper Maker (repo home)

This folder holds **Maker-specific artifacts** that are not tied to the Java package layout under `src/main/java`.

| Path | Purpose |
|------|---------|
| [`scripts/`](scripts/) | Python tools (e.g. legacy draft → extended route JSON converter). |
| [`schemas/`](schemas/) | Reserved for JSON Schema / example fragments for `questHelperMaker` or route envelopes. |

## In-app Maker (Java)

- **UI:** `com.questhelper.panel` — `HelperConstructFrame`, `HelperConstructEditorPanel`, order conditions dialog.
- **Draft / persistence:** `com.questhelper.managers` — `HelperConstructManager`, `ConstructDraftPersistence`, `HelperConstructModels`.
- **Draft I/O helpers:** `com.questhelper.managers.construct` — JSON load, file store, preview runtime.

Auto-save and manual export typically use `construct-draft.json` under the RuneLite user folder (same shape as **Export route JSON** in the maker).

## Legacy JSON conversion

Older root-only maker snapshots (no top-level `sections`) must be converted before import. From the repo root:

```bash
python maker/scripts/convert_legacy_maker_draft.py -i old.json -o new.json
```

Details: [`maker/scripts/README.md`](scripts/README.md).
