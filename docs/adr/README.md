# Architecture Decision Records

Item 42, `docs/APP_SUGGESTIONS.md` — short records of the major architectural
choices already in place. New contributors and future maintainers can read
these in order to understand *why* the codebase looks the way it does without
having to reverse-engineer it from the source.

## Format

Each ADR is one short Markdown file. The format is the lightweight
[Michael Nygard template](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions):

- **Status** — proposed / accepted / superseded
- **Context** — what forced the decision
- **Decision** — what we chose
- **Consequences** — trade-offs we accepted

A blank starter is in [`0000-template.md`](0000-template.md).

## Index

| #   | Title                                                                | Status   |
| --- | -------------------------------------------------------------------- | -------- |
| 1   | [Use Jetpack Compose for the Android UI](0001-jetpack-compose.md)    | Accepted |
| 2   | [Use Hilt for dependency injection](0002-hilt-di.md)                 | Accepted |
| 3   | [Firestore-only data layer (no Room)](0003-firestore-only-data-layer.md) | Accepted |
| 4   | [Azure OpenAI for content generation](0004-azure-openai-for-generation.md) | Accepted |
| 5   | [Hardcoded UI translations (no `res/values-*/strings.xml`)](0005-hardcoded-ui-translations.md) | Accepted |

## Adding a new ADR

1. Copy `0000-template.md` to `NNNN-short-slug.md` (next sequential number).
2. Fill in the four sections.
3. Add a row to the index above.
4. Open a PR; reviewers should focus on whether the Context and Consequences
   are honest about the trade-off, not on style.

ADRs are *immutable once accepted*. To change a decision, write a new ADR that
supersedes the previous one and update both files' Status fields.
