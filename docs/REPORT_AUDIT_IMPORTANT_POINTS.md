# Report Audit Important Points

Last updated: 2026-04-20

## Scope
Use this guide when auditing and regenerating the final report from DOCX/PlantUML sources.

## Latest Report Version
- **Current**: `report-audit/Project_Report_ISA_22235876_v6.docx` (generated from v5)
- **Audit**: `report-audit/AUDIT_REPORT_V6.md` — 37 claims verified (0 MISMATCH), 3 documentation gaps fixed
- **Generator**: `report-audit/generate_v6.py` (applies F-01–F-04 to v5 DOCX)
- **Validation**: TOC/bookmark audit clean (112/112), font audit clean (59 intentional only)

## Source Of Truth Order
1. Android/backend code in `app/src/main/java/...` and `fyp-backend/functions/src/...`
2. Security contracts in `fyp-backend/firestore.rules`
3. Index definitions in `fyp-backend/firestore.indexes.json`
4. Verified test/coverage counts in `docs/TEST_COVERAGE.md`
5. Report files in `report-audit/`

## Critical Metrics (Current Verified Snapshot)
- Android source files: 223
- Backend source files: 9
- Android unit test files: 187
- Android unit tests: 2,449
- Backend test files: 14
- Backend tests: 186
- Backend coverage: statements 94.69%, branches 80.66%, functions 93.50%, lines 95.62%
- Git commit count at current repository snapshot: 591

## UI Language System (Hardcoded — v7 change)
- All 17 UI languages are fully hardcoded — switching is instant with zero API calls
- Azure Translator API is used ONLY for content translation (speech/chat), NOT for UI strings
- Authentication is NOT required for language switching
- Report must distinguish "UI language" (hardcoded) from "Content translation" (API-based)

## Testing Framework Identity (CRITICAL — v3 fix)
- Android: **JUnit 4.13.2** + **Mockito 5.7.0** + **Mockito-Kotlin 5.1.0**
- NOT JUnit 5, NOT MockK, NOT Turbine
- Source of truth: `gradle/libs.versions.toml`
- Backend: **Jest** + **ts-jest** (unchanged)

## Generation Threshold Rules (CRITICAL)
- First-time learning sheet generation: **≥1 record** (`count > 0`)
- Learning sheet regeneration: **+5 new records** (`newRecordsSince >= 5`)
- Word bank regeneration: **+20 new records** (`newRecordsSince >= 20`)
- **DO NOT** change the ≥1 threshold in UC-02, BT-11, user manual, or diagrams
- The ≥5/≥20 thresholds apply only to regeneration, NOT first-time generation

## Unused Schema Field Exclusion Rule
- Database fields existing in code models but NOT wired to UI must NOT appear in report, in all form, include tables and diagrams
- Previously applied to: `avatarUrl`, `friendAvatarUrl`, `fromAvatarUrl`, `photoUrl` — these fields have been **removed from the codebase** entirely
- Rationale: User instruction — "all database schema that is not wired into the system DO NOT APPEAR in report"

## Report Schema Alignment Rules
When auditing Chapter 4 tables, align to real persisted fields (not conceptual names):
- Translation history uses `targetText`, `speaker`, `direction`, `sequence`, `timestamp` (Firestore timestamp).
- Settings uses `themeMode` string (not ordinal) and includes `autoThemeEnabled`, `lastPrimaryLanguageChangeMs`, `lastUsernameChangeMs`.
- Quiz anti-cheat validates against `users/{uid}/quiz_versions/{pair}` and awards update `user_stats/coins` (`coinTotal`, `coinByLang`).
- Chat messages use `content`, `createdAt`, `type`, `metadata`, and unread counters via chat metadata + root user counters.
- Friend request cancel flow currently deletes the request doc instead of persisting `CANCELLED`.
- Global registry tables: `usernames` and `user_search` use explicit document IDs (`username`, `uid`) and should mark Document ID as Required = Yes.

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

## TOC / Heading Text Update Rule
When wording changes affect headings that already appear in the Table of Contents, update both the heading text and the stored TOC display text inside the DOCX package, or refresh fields in Word before finalising.
`python-docx` paragraph edits alone do not automatically refresh TOC field results.

## Repeatable Audit Workflow
1. Generate updated report DOCX from the latest baseline generator script in `report-audit/` (use the newest `generate_v*.py` relevant to the current cycle).
2. Run:
   - `python report-audit/tools/docx_audit.py <docx> --out report-audit/<name>_audit.json`
   - `python report-audit/tools/extract_docx_full.py <docx> --json-out report-audit/<name>_full.json --txt-out report-audit/<name>_full.txt`
   - `python report-audit/tools/audit_toc_bookmarks.py <docx> --out report-audit/<name>_toc_bookmark_audit.json`
3. Scan extracted TXT for stale terms (old field names, old metrics, outdated anti-cheat reasons).
4. Re-run Android quality gates before finalizing repository-changing tasks. 
(Do not need to do this if you have not move the actual code files)

## One-Off Tool Hygiene (Mandatory after full audit)
- Keep reusable scripts in `report-audit/tools/` only.
- Archive version-specific one-off scripts (for example `check_v26_*.py`) instead of leaving them mixed with reusable tools.
- Use cleanup helper:
  - `python report-audit/tools/cleanup_oneoff_tools.py --dry-run`
  - `python report-audit/tools/cleanup_oneoff_tools.py --apply`
- Always review dry-run candidates before apply.
- Archive destination default: `report-audit/previous-report-versions/tool-archive/<timestamp>/`.

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
2. Whole report uses Times New Roman.
3. If you need, you may access my firbase through firebase-sa.json.
4. `figure_3_8.puml` media target in DOCX is `image9.png`; for v27/v28, `figure_4_10.puml` is `image33.png`.
5. docDefaults `sz` is in half-points (24 = 12pt). Check with `report-audit/previous-report-versions/_check_inherited.py`.
6. Font audit tool: `python tools/audit_fonts_comprehensive.py <docx> [--json-out <path>]`.
7. Report-audit skill available at `.agents/skills/fyp-report-audit/SKILL.md`.
