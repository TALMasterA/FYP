# MCP (Model Context Protocol) Configuration Guide

## Overview

This document explains how MCP servers work in different contexts and how to configure them for your development workflow.

---

## ✅ MCP Access Test Results

### GitHub Copilot Agent (Repository Settings)
**Status: ✅ WORKING**

When I (Claude) am called as a GitHub Copilot agent from the repository Agents page, I **CAN** access MCP servers configured in the repository's Copilot agent settings.

**Available MCP Servers:**
- **Firebase MCP Server** ✅
  - 10 resources available
  - Includes guides for: App ID, Crashlytics, Firestore, Authentication, Hosting, etc.
  - Successfully tested resource reading

**How it works:**
- MCP servers are configured in: **Repository Settings → Copilot → Agent Settings**
- Configuration is stored at the GitHub repository level
- No local files needed in the repository
- Environment variable `COPILOT_AGENT_MCP_SERVER_TEMP` is set automatically

---

## Understanding Different MCP Configuration Contexts

### 1. GitHub Copilot Agent (This Context)
**Location:** Repository Settings → Copilot → Agent Settings
**Scope:** Repository-wide
**Used by:** GitHub Copilot agents called from the repository Agents page
**Configuration:** Web UI in GitHub repository settings
**Status:** ✅ Working (as tested above)

### 2. Local Claude Code CLI
**Location:** `~/.claude/config.json` or project `.claude/mcp_config.json`
**Scope:** Local machine or specific project
**Used by:** Claude Code CLI tool running locally
**Configuration:** JSON file with MCP server definitions
**Status:** ⚠️ Not applicable in GitHub Copilot context

### 3. VS Code with Claude Extension (via GitHub Copilot)
**Location:** `.vscode/settings.json` (gitignored)
**Scope:** VS Code workspace
**Used by:** Claude extension in VS Code when using GitHub Copilot backend
**Configuration:** VS Code settings file
**Status:** ❌ Not working (your reported issue)

---

## 🔧 Fixing VS Code + GitHub Copilot MCP Access

### The Problem
You mentioned:
> "I cannot access MCP in VS Code when using Claude in GitHub Copilot (but I can access MCP in VS Code by using 'Local' and choose Claude model, because I have .vscode file with MCP config, just gitignored.)"

### Why This Happens

There are two different modes in VS Code Claude extension:

1. **"Local" Mode** ✅ Works
   - Uses local Claude API
   - Reads MCP config from `.vscode/settings.json`
   - MCP servers run locally on your machine

2. **"GitHub Copilot" Mode** ❌ Not Working
   - Uses GitHub Copilot's backend
   - Should read MCP config from repository Copilot settings
   - May not properly bridge local VS Code to GitHub's MCP servers

### Solution Options

#### Option 1: Use Repository-Level MCP Configuration (Recommended)
The MCP servers should be configured at the repository level (GitHub Settings), which is already working for GitHub Copilot agents.

**Steps:**
1. Go to your repository on GitHub
2. Navigate to: **Settings → Copilot → Agent Settings**
3. Add your MCP server configurations there
4. The VS Code extension should theoretically pick this up when using GitHub Copilot mode

**However**, if this doesn't work, it may be a limitation of the current VS Code extension.

#### Option 2: Continue Using "Local" Mode
If GitHub Copilot mode doesn't support MCP properly in VS Code:
1. Keep your MCP config in `.vscode/settings.json` (already working)
2. Use "Local" mode with Claude model selection
3. This gives you full MCP access locally

#### Option 3: Create a Shared Configuration (Hybrid)
You could create a setup that works in both contexts:

1. **For GitHub Copilot Agents:** Configure MCP servers in repository settings (already done)
2. **For Local VS Code:** Keep `.vscode/settings.json` with MCP config (already working)

**Example `.vscode/settings.json`** (for reference):
```json
{
  "claude.mcpServers": {
    "firebase": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-firebase"],
      "env": {
        "FIREBASE_PROJECT_ID": "your-project-id"
      }
    },
    "filesystem": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-filesystem",
        "${workspaceFolder}"
      ]
    }
  }
}
```

---

## Current Repository MCP Status

### What's Configured (Repository Level)
- ✅ Firebase MCP Server (working in GitHub Copilot agents)
- ✅ Accessible from repository Agents page
- ✅ Provides 10+ Firebase-related resources and guides

### What's NOT in Repository
- ❌ No `.claude/mcp_config.json` (not needed for GitHub Copilot)
- ❌ No `.vscode/settings.json` (gitignored - user-specific)

### Gitignore Settings
The repository correctly ignores local configuration files:
```
.vscode/         # VS Code local settings
.claude/skills/  # Claude Code local skills
```

This allows each developer to have their own local MCP configuration without conflicts.

---

## Troubleshooting

### If MCP doesn't work in VS Code with GitHub Copilot:

1. **Check Extension Version**
   - Ensure you have the latest VS Code Claude extension
   - Some features may be in preview/beta

2. **Verify Repository Settings**
   - Go to GitHub Repository Settings → Copilot → Agent Settings
   - Confirm MCP servers are configured there

3. **Check VS Code Output**
   - Open VS Code Output panel
   - Select "Claude" from the dropdown
   - Look for MCP-related errors

4. **Try Local Mode**
   - Switch to "Local" mode in VS Code
   - Use your `.vscode/settings.json` configuration
   - This should work immediately

5. **Report to Extension Developers**
   - If GitHub Copilot mode doesn't support MCP in VS Code, this may be a feature gap
   - Consider reporting to the VS Code extension maintainers

---

## Recommendations

### For Team Development:
1. ✅ **Configure MCP at repository level** (GitHub Settings) - for GitHub Copilot agents
2. ✅ **Keep `.vscode/` gitignored** - for individual developer preferences
3. ✅ **Document available MCP servers** - so team knows what's available

### For Individual Development:
1. ✅ **Use "Local" mode** in VS Code if GitHub Copilot mode doesn't support MCP
2. ✅ **Keep personal MCP config** in `.vscode/settings.json`
3. ✅ **Don't commit** VS Code settings to git (already gitignored)

---

## Summary

| Context | MCP Config Location | Status | Notes |
|---------|-------------------|--------|-------|
| GitHub Copilot Agent | Repository Settings → Copilot | ✅ Working | Tested and confirmed |
| VS Code (Local Mode) | `.vscode/settings.json` | ✅ Working | Your current setup |
| VS Code (GitHub Copilot Mode) | Repository Settings (?) | ❌ Not Working | May be unsupported |
| Claude Code CLI | `.claude/mcp_config.json` | N/A | Not applicable here |

**Bottom Line:**
- GitHub Copilot agents (like me) **CAN** access repository-level MCP servers ✅
- VS Code GitHub Copilot mode may not support MCP yet (continue using Local mode) ⚠️
- Your current local setup with `.vscode/settings.json` is correct ✅
