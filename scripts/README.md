# Firebase MCP Scripts

This directory contains helper scripts for setting up and verifying Firebase MCP server authentication.

## Available Scripts

### `setup-firebase-mcp.sh`

Interactive script to configure Firebase authentication for the Firebase MCP server.

**Usage:**
```bash
./scripts/setup-firebase-mcp.sh
```

**What it does:**
- Checks if Firebase CLI is installed (offers to install if missing)
- Presents authentication options:
  1. Firebase CLI Login (quick, for local development)
  2. Service Account Key File (for CI/CD and production)
- Guides you through the setup process
- Configures environment variables
- Optionally adds configuration to your shell profile

### `verify-firebase-mcp.sh`

Diagnostic script to check if Firebase authentication is properly configured.

**Usage:**
```bash
./scripts/verify-firebase-mcp.sh
```

**What it checks:**
- Firebase CLI installation
- Firebase project directory structure
- Service account key files
- Environment variables
- Firebase CLI authentication status
- Access to the `isa-fyp` project

**Exit codes:**
- `0` - All checks passed
- `1` - One or more issues found

## Quick Start

1. **First time setup:**
   ```bash
   ./scripts/setup-firebase-mcp.sh
   ```

2. **Verify configuration:**
   ```bash
   ./scripts/verify-firebase-mcp.sh
   ```

3. **Troubleshooting:**
   - Read the output of `verify-firebase-mcp.sh` carefully
   - See `docs/FIREBASE_MCP_SETUP_GUIDE.md` for detailed troubleshooting

## Common Issues

### Firebase CLI Not Found

**Error:** `firebase: command not found`

**Solution:** Install Firebase CLI:
```bash
npm install -g firebase-tools
```

### Authentication Failed

**Error:** `Failed to authenticate, have you run firebase login?`

**Solutions:**
1. Run `firebase login` to authenticate
2. Or set up service account key (see setup script)

### Empty Service Account File

**Error:** `firebase-sa.json exists but is empty`

**Solution:**
1. Generate a service account key from Google Cloud Console
2. Run `./scripts/setup-firebase-mcp.sh` and choose option 2
3. Or manually copy your key to `firebase-sa.json` and `fyp-backend/firebase-sa.json`

### Permission Denied

**Error:** `Permission denied` when accessing Firebase

**Solution:**
- Ensure your Google account has Owner or Editor role on the `isa-fyp` project
- For service accounts, grant "Firebase Admin" role in IAM

## GitHub Actions Integration

The repository is configured to use service account authentication in GitHub Actions.

**Setup:**
1. Generate a service account key (see `docs/FIREBASE_MCP_SETUP_GUIDE.md`)
2. In GitHub: Settings → Secrets → Actions
3. Add secret named `GOOGLE_APPLICATION_CREDENTIALS_JSON`
4. Paste the entire JSON content as the value

The workflow in `.github/workflows/copilot-setup-steps.yml` will automatically:
- Extract the secret to `firebase-sa.json`
- Set the `GOOGLE_APPLICATION_CREDENTIALS` environment variable
- Enable Firebase MCP server access

## Security Notes

⚠️ **IMPORTANT:**
- Never commit `firebase-sa.json` to git (it's in `.gitignore`)
- Never share service account keys publicly
- Rotate keys regularly (every 90 days recommended)
- Use least-privilege permissions for service accounts

## For More Information

See the complete guide: `docs/FIREBASE_MCP_SETUP_GUIDE.md`
