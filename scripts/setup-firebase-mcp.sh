#!/bin/bash

# Firebase MCP Quick Setup Script
# This script helps you configure Firebase authentication for the MCP server

set -e

echo "=========================================="
echo "Firebase MCP Server Quick Setup"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Get the repository root
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

echo "Repository root: $REPO_ROOT"
echo ""

# Check if Firebase CLI is installed
if ! command -v firebase &> /dev/null; then
    echo -e "${YELLOW}Firebase CLI is not installed.${NC}"
    echo ""
    echo "Would you like to install it now? (y/n)"
    read -r INSTALL_FIREBASE

    if [[ $INSTALL_FIREBASE =~ ^[Yy]$ ]]; then
        echo "Installing Firebase CLI..."
        if command -v npm &> /dev/null; then
            npm install -g firebase-tools
            echo -e "${GREEN}✓${NC} Firebase CLI installed successfully"
        else
            echo -e "${YELLOW}npm is not installed. Please install Node.js and npm first.${NC}"
            echo "Visit: https://nodejs.org/"
            exit 1
        fi
    else
        echo "Please install Firebase CLI manually:"
        echo "  npm install -g firebase-tools"
        exit 1
    fi
fi
echo ""

# Show authentication options
echo "Firebase MCP Server Authentication Options:"
echo ""
echo "1. Firebase CLI Login (Recommended for local development)"
echo "   - Quick and easy"
echo "   - Uses your Google account"
echo "   - Works immediately"
echo ""
echo "2. Service Account Key File (For CI/CD or shared environments)"
echo "   - More secure for production"
echo "   - Requires generating a key from Google Cloud Console"
echo "   - Can be shared across team"
echo ""
echo "Which method would you like to use? (1/2)"
read -r AUTH_METHOD

if [ "$AUTH_METHOD" = "1" ]; then
    echo ""
    echo -e "${BLUE}Setting up Firebase CLI authentication...${NC}"
    echo ""

    # Perform Firebase login
    firebase login

    # Verify authentication
    echo ""
    echo "Verifying authentication..."
    if firebase projects:list &> /dev/null; then
        echo -e "${GREEN}✓${NC} Successfully authenticated with Firebase"

        # Check if isa-fyp project is accessible
        if firebase projects:list | grep -q "isa-fyp"; then
            echo -e "${GREEN}✓${NC} Project 'isa-fyp' is accessible"
        else
            echo -e "${YELLOW}⚠${NC} Project 'isa-fyp' not found in your accessible projects"
            echo "  You may need to be granted access to this project."
        fi
    else
        echo -e "${RED}✗${NC} Authentication failed"
        exit 1
    fi

    echo ""
    echo -e "${GREEN}Setup complete!${NC}"
    echo "The Firebase MCP server can now access your Firebase project."

elif [ "$AUTH_METHOD" = "2" ]; then
    echo ""
    echo -e "${BLUE}Setting up Service Account authentication...${NC}"
    echo ""

    echo "Please follow these steps:"
    echo ""
    echo "1. Go to Google Cloud Console: https://console.cloud.google.com/"
    echo "2. Select project: isa-fyp"
    echo "3. Navigate to: IAM & Admin → Service Accounts"
    echo "4. Find the Firebase Admin SDK service account"
    echo "5. Click ⋮ → Manage Keys → Add Key → Create New Key"
    echo "6. Select JSON format and download the key"
    echo ""
    echo "Once you have the key file, enter its path:"
    read -r KEY_FILE_PATH

    # Expand path (handle ~)
    KEY_FILE_PATH="${KEY_FILE_PATH/#\~/$HOME}"

    if [ ! -f "$KEY_FILE_PATH" ]; then
        echo -e "${RED}✗${NC} File not found: $KEY_FILE_PATH"
        exit 1
    fi

    # Validate JSON
    if command -v python3 &> /dev/null; then
        if ! python3 -c "import json; json.load(open('$KEY_FILE_PATH'))" 2>/dev/null; then
            echo -e "${RED}✗${NC} Invalid JSON file"
            exit 1
        fi
    fi

    # Copy to repository locations
    echo "Copying service account key to repository locations..."
    cp "$KEY_FILE_PATH" "$REPO_ROOT/firebase-sa.json"
    cp "$KEY_FILE_PATH" "$REPO_ROOT/fyp-backend/firebase-sa.json"
    echo -e "${GREEN}✓${NC} Service account key copied"

    # Set environment variable
    echo ""
    echo "Setting GOOGLE_APPLICATION_CREDENTIALS environment variable..."
    export GOOGLE_APPLICATION_CREDENTIALS="$REPO_ROOT/firebase-sa.json"

    # Add to shell profile
    SHELL_PROFILE=""
    if [ -f "$HOME/.bashrc" ]; then
        SHELL_PROFILE="$HOME/.bashrc"
    elif [ -f "$HOME/.zshrc" ]; then
        SHELL_PROFILE="$HOME/.zshrc"
    fi

    if [ -n "$SHELL_PROFILE" ]; then
        echo ""
        echo "Would you like to add this to your shell profile ($SHELL_PROFILE)? (y/n)"
        read -r ADD_TO_PROFILE

        if [[ $ADD_TO_PROFILE =~ ^[Yy]$ ]]; then
            echo "" >> "$SHELL_PROFILE"
            echo "# Firebase MCP Server" >> "$SHELL_PROFILE"
            echo "export GOOGLE_APPLICATION_CREDENTIALS=\"$REPO_ROOT/firebase-sa.json\"" >> "$SHELL_PROFILE"
            echo -e "${GREEN}✓${NC} Added to $SHELL_PROFILE"
            echo "  Run 'source $SHELL_PROFILE' or restart your terminal"
        fi
    fi

    echo ""
    echo -e "${GREEN}Setup complete!${NC}"
    echo ""
    echo "For the current session, run:"
    echo "  export GOOGLE_APPLICATION_CREDENTIALS=\"$REPO_ROOT/firebase-sa.json\""
    echo ""
    echo -e "${YELLOW}IMPORTANT:${NC}"
    echo "  - Never commit firebase-sa.json to git (it's in .gitignore)"
    echo "  - For GitHub Actions, add the key as a secret named GOOGLE_APPLICATION_CREDENTIALS_JSON"

else
    echo "Invalid option. Please run the script again and choose 1 or 2."
    exit 1
fi

echo ""
echo "=========================================="
echo ""
echo "Next steps:"
echo "  1. Run './scripts/verify-firebase-mcp.sh' to verify the setup"
echo "  2. Read docs/FIREBASE_MCP_SETUP_GUIDE.md for more details"
echo ""
