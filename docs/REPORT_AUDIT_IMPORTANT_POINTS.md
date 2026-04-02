# Report Audit Important Points

Last updated: 2026-03-31

## Scope
Use this guide when auditing and regenerating the final report from DOCX/PlantUML sources.

## Source Of Truth Order
1. Android/backend code in `app/src/main/java/...` and `fyp-backend/functions/src/...`
2. Security contracts in `fyp-backend/firestore.rules`
3. Index definitions in `fyp-backend/firestore.indexes.json`
4. Verified test/coverage counts in `docs/TEST_COVERAGE.md`
5. Report files in `report-audit/`

## Critical Metrics (Current Verified Snapshot)
- Android source files: 252
- Backend source files: 9
- Android unit test files: 211
- Android unit tests: 2,766
- Backend test files: 14
- Backend tests: 182
- Backend coverage: statements 99.00%, branches 84.21%, functions 100.00%, lines 99.84%
- Git commit count baseline used in report v19: 564+

## Report Schema Alignment Rules
When auditing Chapter 4 tables, align to real persisted fields (not conceptual names):
- Translation history uses `targetText`, `speaker`, `direction`, `sequence`, `timestamp` (Firestore timestamp).
- Settings uses `themeMode` string (not ordinal) and includes `autoThemeEnabled`, `lastPrimaryLanguageChangeMs`, `lastUsernameChangeMs`.
- Quiz anti-cheat validates against `users/{uid}/quiz_versions/{pair}` and awards update `user_stats/coins` (`coinTotal`, `coinByLang`).
- Chat messages use `content`, `createdAt`, `type`, `metadata`, and unread counters via chat metadata + root user counters.
- Friend request cancel flow currently deletes the request doc instead of persisting `CANCELLED`.

## Diagram Accuracy Hotspots
Always re-check these because they drift often:
- Learning lifecycle threshold: first generation requires >=1 record; regeneration requires +5.
- Quiz+coin sequence: must reference `quiz_versions`, `coin_awards`, `last_awarded_quiz`, `user_stats/coins`.
- Chat sequence: use `content`/`createdAt` field names and 1 read + 2 writes mark-read design.
- Friend request states: cancellation is delete-path behavior.

## DOCX Media Replacement Rule
For this report file family, embedded media names are `image24.png`, `image25.png`, etc. (no hyphen).
If diagram replacement fails, check `report-audit/v*_audit.json` for actual media targets first.
Replace the diagrams should maintain the diagram size in report. Not affect the format.
Also, when editing table, use the same format for alignment.

## Repeatable Audit Workflow
1. Generate updated report DOCX from the latest generator script (`report-audit/generate_v19.py` for the current baseline).
2. Run:
   - `python report-audit/tools/docx_audit.py <docx> --out report-audit/<name>_audit.json`
   - `python report-audit/tools/extract_docx_full.py <docx> --json-out report-audit/<name>_full.json --txt-out report-audit/<name>_full.txt`
3. Scan extracted TXT for stale terms (old field names, old metrics, outdated anti-cheat reasons).
4. Re-run Android quality gates before finalizing repository-changing tasks. 
(Do not need to do this if you have not move the actual code files)

## Security Reminder
Never commit service-account credential JSON files or keys into tracked source. Keep credentials local-only and rotate if exposed.

## Font Size Convention (Mandatory Rule)
When inserting or editing DOCX content, these sizes **must** be followed:

| Element | Size | How to apply |
|---|---|---|
| Body text (Normal style) | **12 pt** | Explicit `run.font.size = Pt(12)` on every new run |
| Table cells | **12 pt** (inherited from docDefaults) | Do **NOT** set an explicit font size; leave as `None` |
| Heading 1 | 16 pt | Set by style |
| Heading 2 | 13–14 pt | Set by style |
| Heading 3 | Inherited | Set by style |

- `generate_v24.py` enforces this via `BODY_FONT_SIZE = Pt(12)`.
- Cross-run paragraph replacement (`_replace_in_paragraph`) must preserve the original run's font size.
- Device-compatibility table rows must **not** override the inherited size.

## Table Cell Replacement Rule (Mandatory)
DOCX table cells are individual paragraphs — they do **not** contain pipe `|` characters.
Never use markdown-style `col1 | col2 | col3` patterns in TEXT_REPLACEMENTS for table cells.
Use table-aware replacement: identify the table by header row, then replace individual cells by key.

## Audit note (do not delete)

1. If you need regen the diagrams during audit, remember to keep the size of the diagrams.
2. Whole report use Times New Romen.
3. If you need, you may access my firbase through firebase-sa.json.
4. `figure_3_8.puml` media target in DOCX is `image9.png`; `figure_4_10.puml` is `image29.png`.
5. docDefaults `sz` is in half-points (24 = 12pt). Check with `tools/_check_inherited.py`.
6. Font audit tool: `python tools/audit_fonts_comprehensive.py <docx> [--json-out <path>]`.
7. Report-audit skill available at `.agents/skills/fyp-report-audit/SKILL.md`.
