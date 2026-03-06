# Firebase MCP Server Setup Guide

## Overview

This guide explains how to configure Firebase authentication for the Firebase MCP server to work with GitHub Copilot and Claude Code in this repository.

## Current Issue

The Firebase MCP server cannot access your Firebase project (`isa-fyp`) and Firestore because authentication is not configured. You'll see errors like:

```
Failed to authenticate, have you run firebase login?
```

## Why This Happens

The Firebase MCP server requires authentication credentials to access your Firebase project. There are three ways to authenticate:

1. **Service Account Key File** (recommended for CI/CD)
2. **Firebase CLI Login** (recommended for local development)
3. **Application Default Credentials** (for Google Cloud environments)

Currently, none of these are configured in your environment.

## Solution Options

### Option 1: Firebase CLI Login (Easiest for Local Development)

This is the simplest method for local development and testing.

**Steps:**

1. Install Firebase CLI if you haven't already:
   ```bash
   npm install -g firebase-tools
   ```

2. Login to Firebase:
   ```bash
   firebase login
   ```

   This will open a browser window where you can authenticate with your Google account.

3. Verify the login:
   ```bash
   firebase projects:list
   ```

   You should see your `isa-fyp` project listed.

4. The Firebase MCP server will now automatically use these credentials.

**Pros:**
- Quick and easy setup
- Works immediately with Firebase MCP
- No need to manage credential files

**Cons:**
- Only works on your local machine
- Credentials are tied to your user account
- Not suitable for CI/CD or shared environments

### Option 2: Service Account Key File (Recommended for CI/CD)

This method uses a service account JSON key file for authentication. This is what the GitHub Actions workflow is configured to use.

**Steps:**

1. **Generate a Service Account Key:**

   a. Go to the [Google Cloud Console](https://console.cloud.google.com/)

   b. Select your Firebase project (`isa-fyp`)

   c. Navigate to: **IAM & Admin** → **Service Accounts**

   d. Find the Firebase Admin SDK service account (usually named like `firebase-adminsdk-xxxxx@isa-fyp.iam.gserviceaccount.com`)

   e. Click the three dots (⋮) → **Manage Keys** → **Add Key** → **Create New Key**

   f. Select **JSON** format and click **Create**

   g. Save the downloaded JSON file securely

2. **For Local Development:**

   Save the service account key to the repository:
   ```bash
   # From the root of the repository
   cp /path/to/your/downloaded-key.json firebase-sa.json
   cp /path/to/your/downloaded-key.json fyp-backend/firebase-sa.json
   ```

   Set the environment variable:
   ```bash
   export GOOGLE_APPLICATION_CREDENTIALS="/home/runner/work/FYP/FYP/firebase-sa.json"
   ```

   **IMPORTANT:** Never commit the actual service account key to git! It's already in `.gitignore`.

3. **For GitHub Actions:**

   a. In your GitHub repository, go to **Settings** → **Secrets and variables** → **Actions**

   b. Click **New repository secret**

   c. Name: `GOOGLE_APPLICATION_CREDENTIALS_JSON`

   d. Value: Paste the entire contents of the JSON file

   e. Click **Add secret**

   The workflow in `.github/workflows/copilot-setup-steps.yml` is already configured to use this secret.

**Pros:**
- Works in CI/CD environments
- Can be shared across team members securely
- Granular permission control

**Cons:**
- Requires more setup steps
- Need to manage credential files securely
- Need to rotate keys periodically

### Option 3: Application Default Credentials (For Google Cloud)

If you're running in a Google Cloud environment (Cloud Run, Compute Engine, etc.), you can use Application Default Credentials.

**Steps:**

1. Ensure your environment has the appropriate service account attached
2. Grant the service account necessary Firebase permissions
3. The Firebase MCP server will automatically detect and use these credentials

This option is not applicable for local development.

## Verifying Firebase MCP Access

After setting up authentication, you can verify it works by running the helper script:

```bash
./scripts/verify-firebase-mcp.sh
```

Or manually test with the Firebase CLI:

```bash
cd fyp-backend
firebase projects:list
firebase firestore:indexes
```

## Firebase MCP Server Configuration

The Firebase MCP server is configured in the repository Copilot settings to work with the `fyp-backend` directory, which contains:

- `firebase.json` - Firebase project configuration
- `.firebaserc` - Project aliases (configured for `isa-fyp`)
- `firestore.rules` - Firestore security rules
- `firestore.indexes.json` - Firestore indexes

## Troubleshooting

### "Failed to authenticate" Error

**Cause:** No authentication method is configured.

**Solution:** Follow Option 1 or Option 2 above.

### "No firebase.json file was found" Error

**Cause:** The Firebase MCP server is looking in the wrong directory.

**Solution:** The MCP server should be configured to use `/home/runner/work/FYP/FYP/fyp-backend` as the project directory. This is automatically configured.

### "Permission denied" Errors

**Cause:** The authenticated account doesn't have sufficient permissions.

**Solution:**
1. For service accounts: Grant the service account the following roles in IAM:
   - **Firebase Admin**
   - **Cloud Datastore User** (for Firestore)

2. For user accounts: Ensure your Google account has Owner or Editor role on the Firebase project.

### Empty firebase-sa.json File

**Cause:** The GitHub secret `GOOGLE_APPLICATION_CREDENTIALS_JSON` is not configured or is empty.

**Solution:** Follow the steps in "Option 2: Service Account Key File" to add the secret.

## Security Best Practices

1. **Never commit service account keys to git**
   - The `.gitignore` file already excludes `firebase-sa.json`
   - Always use GitHub Secrets for CI/CD

2. **Rotate service account keys regularly**
   - Create new keys every 90 days
   - Delete old keys after rotation

3. **Use least-privilege permissions**
   - Only grant necessary roles to service accounts
   - Use separate service accounts for different environments (dev, staging, prod)

4. **Audit access regularly**
   - Review service account usage in Cloud Console
   - Remove unused service accounts and keys

## What You Can Do with Firebase MCP

Once authenticated, the Firebase MCP server provides access to:

- **Firebase Projects**: List and manage projects
- **Firestore**: Query databases, collections, and documents
- **Firebase Authentication**: Manage users and auth providers
- **Cloud Functions**: Deploy and manage functions
- **Firebase Hosting**: Deploy web apps
- **Firebase Storage**: Manage files and storage buckets

## Additional Resources

- [Firebase CLI Documentation](https://firebase.google.com/docs/cli)
- [Service Account Authentication](https://cloud.google.com/docs/authentication/production)
- [Firebase MCP Server](https://github.com/firebase/firebase-mcp-server)
- [GitHub Actions Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

## Quick Reference Commands

```bash
# Login to Firebase CLI
firebase login

# List projects
firebase projects:list

# Set active project
firebase use isa-fyp

# Deploy Firestore rules
cd fyp-backend
firebase deploy --only firestore:rules

# Deploy Cloud Functions
firebase deploy --only functions

# Check Firestore indexes
firebase firestore:indexes

# Export environment variable (Linux/Mac)
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/firebase-sa.json"

# Export environment variable (Windows)
set GOOGLE_APPLICATION_CREDENTIALS=C:\path\to\firebase-sa.json
```
