# Firebase and Serena Setup Guide

This document explains the current status of Firebase and Serena setup in this repository and provides instructions for completing authentication.

## ✅ What's Already Set Up

### Serena MCP Server
- ✅ **Serena is fully configured and operational**
- ✅ All Serena tools are accessible
- ✅ Project onboarding completed with 6 memory files:
  - `project_overview` - Project purpose, features, and tech stack
  - `suggested_commands` - Essential development commands
  - `code_style_conventions` - Kotlin style guide and patterns
  - `task_completion_checklist` - Steps to complete tasks
  - `codebase_structure` - Directory layout and architecture
  - `firebase_configuration` - Firebase setup and commands

### Firebase Configuration
- ✅ Firebase project configured (ID: `isa-fyp`)
- ✅ Firebase backend directory exists at `fyp-backend/`
- ✅ All Firebase config files present:
  - `fyp-backend/.firebaserc` - Project alias
  - `fyp-backend/firebase.json` - Services configuration
  - `fyp-backend/firestore.rules` - Security rules
  - `fyp-backend/firestore.indexes.json` - Database indexes
- ✅ Firebase MCP server tools are accessible
- ✅ GitHub workflow configured to create service account file

## ⚠️ What Needs Action: Firebase Authentication

### Current Authentication Status
**Firebase MCP is NOT authenticated** because the service account file is empty.

### The Issue
1. Your GitHub workflow (`.github/workflows/copilot-setup-steps.yml`) is configured to write the service account JSON from the secret `GOOGLE_APPLICATION_CREDENTIALS_JSON`
2. The file `/home/runner/work/FYP/FYP/firebase-sa.json` exists but contains only 1 byte (empty)
3. This indicates the GitHub secret `GOOGLE_APPLICATION_CREDENTIALS_JSON` may not be properly configured

### How to Fix Firebase Authentication

#### Option 1: Verify GitHub Secret (Recommended)
1. Go to your GitHub repository: https://github.com/TALMasterA/FYP
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Check if `GOOGLE_APPLICATION_CREDENTIALS_JSON` secret exists
4. If it doesn't exist or is incorrect:
   - Download your service account key from Firebase Console:
     - Go to [Firebase Console](https://console.firebase.google.com/)
     - Select project `isa-fyp`
     - Click **Project Settings** → **Service Accounts**
     - Click **Generate New Private Key**
     - Download the JSON file
   - Create/update the secret `GOOGLE_APPLICATION_CREDENTIALS_JSON` with the contents of the downloaded JSON file
5. The next time the workflow runs, it will create the `firebase-sa.json` file automatically

#### Option 2: Manual Service Account File (For Local Testing)
If you have the service account JSON file locally:

```bash
# Copy your service account file to the root directory
cp /path/to/your/service-account.json /home/runner/work/FYP/FYP/firebase-sa.json

# Verify the environment variable is set
echo $GOOGLE_APPLICATION_CREDENTIALS
# Should output: /home/runner/work/FYP/FYP/firebase-sa.json

# Now Firebase commands should work
cd fyp-backend
firebase projects:list
```

#### Option 3: Interactive Login (Temporary - Not Available in CI)
For local development only:
```bash
firebase login
```
This opens a browser for Google authentication.

## 🧪 How to Verify Setup

### Verify Serena
```bash
# List Serena memories
# Use the MCP tool: mcp__serena__list_memories
```
Expected output: Array of 6 memory files

### Verify Firebase Authentication
```bash
# Check if service account file has content
wc -c firebase-sa.json
# Should show more than 1 byte (typically 2000+ bytes)

# Check Firebase environment
# Use MCP tool: mcp__firebase__firebase_get_environment
```
Expected output: Should show authenticated user and project info

### Verify Firebase Project Access
```bash
# Try listing Firebase projects
# Use MCP tool: mcp__firebase__firebase_list_projects
```
Expected output: List of Firebase projects including `isa-fyp`

## 📋 Common Firebase Operations

Once authenticated, you can use these Firebase MCP tools:

### Project Management
- `firebase_list_projects` - List all accessible Firebase projects
- `firebase_get_project` - Get current project details
- `firebase_get_environment` - Check authentication status

### App Management
- `firebase_list_apps` - List all apps in the project
- `firebase_get_sdk_config` - Get Firebase configuration for an app

### Initialization & Setup
- `firebase_init` - Initialize Firebase services
- `firebase_create_project` - Create a new Firebase project
- `firebase_create_app` - Register a new app in the project

## 🔐 Security Notes

- ⚠️ **NEVER commit** `firebase-sa.json` to git (it's gitignored)
- ⚠️ **NEVER commit** `app/google-services.json` to git (it's gitignored)
- ✅ Always use GitHub Secrets for CI/CD workflows
- ✅ Service accounts should have minimal required permissions
- ✅ Rotate service account keys periodically

## 📚 Additional Resources

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase CLI Documentation](https://firebase.google.com/docs/cli)
- [Service Account Setup](https://firebase.google.com/docs/admin/setup#initialize-sdk)
- Project README: `/home/runner/work/FYP/FYP/README.md`
- Serena memories: Use `mcp__serena__read_memory` to access project documentation

## 🎯 Summary

### Serena Status: ✅ READY
Serena is fully configured and operational with complete project knowledge.

### Firebase Status: ⚠️ NEEDS AUTH
Firebase is configured but requires authentication. Complete authentication by:
1. Ensuring the GitHub secret `GOOGLE_APPLICATION_CREDENTIALS_JSON` is properly set
2. OR manually providing a valid service account JSON file
3. OR using interactive login for local development

Once authentication is complete, all Firebase MCP tools will be fully functional.
