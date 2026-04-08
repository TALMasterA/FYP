---
name: create-reusable-skill
description: 'Create or refine a reusable skill from a user prompt when the workflow is recurring, worth standardizing, or likely to improve future development. Use when the user asks to create a skill, save a process as a reusable skill, turn a repeated workflow into a skill, or when the agent identifies a high-value repeated process that should become a skill.'
argument-hint: 'Workflow or prompt to turn into a reusable skill (for example: create a skill for report screenshot audits, make a skill for recurring Firestore rule reviews)'
user-invocable: true
---

# Create Reusable Skill

## Purpose

Create a new skill only when it will make future work faster, safer, or more consistent.

This skill is for converting a prompt or repeated workflow into a reusable skill file, not for solving a one-off coding task.

## When To Use

- The user explicitly asks to create a skill.
- A workflow has multiple repeatable steps, commands, or checks that are likely to recur.
- The task needs repo-specific knowledge that would be useful again.
- The agent notices a repeated manual process that keeps appearing across tasks.
- A future task would benefit from a standard checklist, search pattern set, or verification flow.

## When Not To Use

- The request is a one-off fix with no clear reuse value.
- The behavior should be always-on guidance instead of an on-demand workflow skill.
- A simple prompt or instruction file would solve the problem more cleanly.
- The process is too vague, unstable, or speculative to standardize yet.

## Creation Criteria

Create the skill when most of these are true:

1. The workflow has at least 3 concrete steps or meaningful decision points.
2. The workflow is likely to recur in this repository or across the user's future tasks.
3. Standardizing it reduces missed checks, inconsistent edits, or repeated context gathering.
4. The skill can be described with explicit triggers, scope, and outputs.
5. The skill does not duplicate an existing skill with only minor wording differences.

If those conditions are not met, explain why no skill should be created and suggest the better primitive instead:
- workspace instruction
- prompt
- custom agent
- no customization needed

## Scope Selection

- Use `.github/skills/<skill-name>/SKILL.md` for repository-shared skills that should travel with the project.
- Use `.agents/skills/<skill-name>/SKILL.md` only for local machine-level skills or personal workflows.
- In this repository, prefer `.github/skills/` when the skill helps future FYP development because `.agents/` is gitignored.

## Authoring Workflow

1. Restate the user's requested workflow in one sentence.
2. Search existing skills first to avoid duplicates or overlapping responsibilities.
3. Choose a short kebab-case name that matches the folder name exactly.
4. Write frontmatter with:
   - `name`
   - quoted `description` containing trigger phrases the agent can discover
   - `argument-hint`
   - `user-invocable: true` when appropriate
5. Write a concise body with only the sections needed for reliable reuse.
6. Include concrete commands, checks, paths, or search patterns when they are stable and useful.
7. Keep the workflow opinionated enough to help, but not so rigid that it blocks valid variations.

## Required Skill Content

Every created skill should usually include:

- Purpose
- When To Use
- When Not To Use
- Step-by-step workflow
- Tooling recommendations when tool choice matters
- Output checklist or completion expectations

## Repo Integration Rules

When a new shared repo skill is created:

1. Update `docs/MCP_SKILLS_PLAYBOOK.md` if the skill changes the recommended skill/tooling workflow.
2. Update `docs/treeOfImportantfiles.txt` when files are added, removed, or renamed.
3. Review whether `README.md` or another docs file also needs an update.
4. Run required repository verification before finalizing:
```bash
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
```

## Proactive Curation Rule

While working on normal tasks, if the agent finds a repeated, high-value workflow that is not well covered by an existing skill, it should create a new skill when:

- the workflow is likely to be reused,
- the standardization benefit is clear, and
- adding the skill will reduce future drift or rework.

Avoid skill sprawl. Prefer refining an existing skill when that is enough.

## Output Checklist

At completion, report:

- whether a new skill was created or intentionally not created,
- the chosen skill path,
- why the workflow was worth standardizing,
- which docs were updated,
- whether tree maintenance was required,
- Android verification outcomes.