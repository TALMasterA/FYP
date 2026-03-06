# Firebase MCP Access Issue - Resolution Summary

**Date:** March 6, 2026
**Issue:** Firebase MCP server cannot access Firebase project and Firestore
**Status:** ✅ Resolved with documentation and tools

---

## Issue Analysis

### Problem Identified

The Firebase MCP server was unable to access the Firebase project (`isa-fyp`) and Firestore due to missing authentication credentials. The error encountered was:

```
Failed to authenticate, have you run firebase login?
```

### Root Causes

1. **Empty Service Account File**: The `firebase-sa.json` file existed but was empty (no credentials)
2. **Missing Environment Variable**: `GOOGLE_APPLICATION_CREDENTIALS` was not properly configured
3. **No Firebase CLI Login**: Interactive Firebase login had not been performed

### Why This Happened

Firebase MCP server requires one of three authentication methods:
- Firebase CLI login (`firebase login`)
- Service account key file with `GOOGLE_APPLICATION_CREDENTIALS` environment variable
- Application Default Credentials (for Google Cloud environments)

None of these were configured in the environment.

---

## Solution Implemented

Since Firebase authentication requires user credentials that cannot be automated programmatically, the solution provides:

### 1. Comprehensive Documentation

**Created: `docs/FIREBASE_MCP_SETUP_GUIDE.md`**

Complete guide covering:
- Three authentication methods (CLI login, service account, ADC)
- Step-by-step setup instructions for each method
- GitHub Actions integration instructions
- Troubleshooting common issues
- Security best practices
- Quick reference commands

### 2. Interactive Setup Script

**Created: `scripts/setup-firebase-mcp.sh`**

Features:
- Checks Firebase CLI installation (offers to install)
- Presents authentication options
- Guides through Firebase CLI login process
- Handles service account key file configuration
- Sets environment variables automatically
- Optionally adds configuration to shell profile

**Usage:**
```bash
./scripts/setup-firebase-mcp.sh
```

### 3. Verification Script

**Created: `scripts/verify-firebase-mcp.sh`**

Diagnostic tool that checks:
- Firebase CLI installation
- Project directory structure
- Service account key files
- Environment variables
- Firebase CLI authentication status
- Access to the `isa-fyp` project
- Provides actionable feedback

**Usage:**
```bash
./scripts/verify-firebase-mcp.sh
```

### 4. Scripts Documentation

**Created: `scripts/README.md`**

Quick reference for:
- Available scripts and their usage
- Common issues and solutions
- GitHub Actions integration notes
- Security considerations

### 5. Updated Main README

**Modified: `README.md`**

Added sections:
- Firebase MCP Server setup commands
- Reference to detailed documentation
- Integration with development workflow

---

## How to Use the Solution

### For Local Development (Recommended)

1. **Run the setup script:**
   ```bash
   ./scripts/setup-firebase-mcp.sh
   ```

2. **Choose Option 1** (Firebase CLI Login)

3. **Authenticate** when browser opens

4. **Verify** the setup:
   ```bash
   ./scripts/verify-firebase-mcp.sh
   ```

### For CI/CD (GitHub Actions)

1. **Generate service account key:**
   - Go to Google Cloud Console
   - Select `isa-fyp` project
   - Navigate to IAM & Admin → Service Accounts
   - Generate JSON key for Firebase Admin SDK account

2. **Add GitHub secret:**
   - Repository Settings → Secrets → Actions
   - Add secret: `GOOGLE_APPLICATION_CREDENTIALS_JSON`
   - Paste entire JSON content

3. **Workflow already configured** in `.github/workflows/copilot-setup-steps.yml`

---

## What You Can Do Now

Once authenticated, the Firebase MCP server provides access to:

- ✅ Firebase Projects management
- ✅ Firestore databases, collections, and documents
- ✅ Firebase Authentication user management
- ✅ Cloud Functions deployment and management
- ✅ Firebase Hosting
- ✅ Firebase Storage

---

## Testing Firebase Access

After setting up authentication, you can test access:

### Via Scripts
```bash
./scripts/verify-firebase-mcp.sh
```

### Via Firebase CLI
```bash
firebase projects:list
cd fyp-backend
firebase firestore:indexes
```

### Via Firebase MCP (from Claude Code or GitHub Copilot)
The Firebase MCP server will now automatically authenticate and provide access to Firebase resources.

---

## Security Recommendations

1. **Never commit service account keys** - They're in `.gitignore`
2. **Rotate keys regularly** - Every 90 days recommended
3. **Use least-privilege permissions** - Grant only necessary roles
4. **Use Firebase CLI login for local dev** - Simpler and safer
5. **Use service accounts only for CI/CD** - Where necessary

---

## Files Created/Modified

### New Files
- `docs/FIREBASE_MCP_SETUP_GUIDE.md` - Complete setup and troubleshooting guide
- `scripts/setup-firebase-mcp.sh` - Interactive setup script
- `scripts/verify-firebase-mcp.sh` - Diagnostic verification script
- `scripts/README.md` - Scripts documentation
- `docs/FIREBASE_MCP_RESOLUTION.md` - This summary document

### Modified Files
- `README.md` - Added Firebase MCP server information and commands

---

## Next Steps for User

### Immediate (to use Firebase MCP now)

1. **Run the setup script:**
   ```bash
   cd /path/to/FYP
   ./scripts/setup-firebase-mcp.sh
   ```

2. **Choose authentication method:**
   - Option 1: Firebase CLI login (easiest)
   - Option 2: Service account key (for CI/CD)

3. **Verify it works:**
   ```bash
   ./scripts/verify-firebase-mcp.sh
   ```

### For GitHub Actions (optional)

1. Generate service account key from Google Cloud Console
2. Add `GOOGLE_APPLICATION_CREDENTIALS_JSON` secret to GitHub repository
3. The workflow will automatically configure authentication

---

## Additional Resources

- [Firebase CLI Documentation](https://firebase.google.com/docs/cli)
- [Service Account Authentication](https://cloud.google.com/docs/authentication/production)
- [Firebase MCP Server](https://github.com/firebase/firebase-mcp-server)

---

## Conclusion

The Firebase MCP access issue has been resolved by providing:
- ✅ Clear diagnosis of the authentication problem
- ✅ Multiple authentication options with step-by-step instructions
- ✅ Automated setup and verification scripts
- ✅ Comprehensive documentation
- ✅ GitHub Actions integration support

The user can now easily configure Firebase authentication and use the Firebase MCP server with GitHub Copilot or Claude Code.
