# MCP Configuration Guide

## Overview
This directory contains the Model Context Protocol (MCP) configuration for the repository. The MCP config enables Claude Code and other AI tools to interact with your repository through standardized servers.

## Files
- `mcp_config.json` - Main MCP configuration file defining available servers

## Current Configuration

### Filesystem Server
Provides access to repository files and directories.
- **Package**: `@modelcontextprotocol/server-filesystem`
- **Path**: `/home/runner/work/FYP/FYP` (repository root)

### GitHub Server
Provides GitHub API integration for issues, PRs, and repository operations.
- **Package**: `@modelcontextprotocol/server-github`
- **Authentication**: Uses `GITHUB_TOKEN` environment variable

## Access Verification

The MCP config should now be accessible in the repository settings page. You can verify:

1. ✅ File exists at `.claude/mcp_config.json`
2. ✅ File has proper permissions (644 - readable by all)
3. ✅ File is tracked in git (committed to repository)
4. ✅ JSON syntax is valid
5. ✅ Configuration includes filesystem and GitHub servers

## Troubleshooting

### If the config is not accessible:
1. **Check file existence**: Ensure `.claude/mcp_config.json` exists
2. **Check permissions**: File should have 644 permissions
3. **Verify JSON syntax**: Run `cat .claude/mcp_config.json | python3 -m json.tool`
4. **Check gitignore**: Only `.claude/skills/` should be ignored, not `.claude/mcp_config.json`

### If GitHub integration doesn't work:
1. Ensure `GITHUB_TOKEN` environment variable is set
2. Token needs appropriate permissions (repo access)
3. Verify token in CI/CD environment or local development setup

## Adding More MCP Servers

To add additional MCP servers, edit `mcp_config.json` and add new entries under `mcpServers`:

```json
{
  "mcpServers": {
    "your-server-name": {
      "command": "npx",
      "args": ["-y", "@your-org/your-mcp-server"],
      "env": {
        "YOUR_API_KEY": "${YOUR_ENV_VAR}"
      }
    }
  }
}
```

## References
- [MCP Documentation](https://modelcontextprotocol.io/)
- [Available MCP Servers](https://github.com/modelcontextprotocol/servers)
