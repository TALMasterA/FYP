#!/bin/bash

# Firebase MCP Server Verification Script
# This script checks if Firebase authentication is properly configured

set -e

echo "=========================================="
echo "Firebase MCP Server Configuration Check"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Track overall status
ISSUES_FOUND=0

# Function to print status
print_status() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓${NC} $2"
    else
        echo -e "${RED}✗${NC} $2"
        ISSUES_FOUND=$((ISSUES_FOUND + 1))
    fi
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# Check 1: Firebase CLI installed
echo "Checking Firebase CLI installation..."
if command -v firebase &> /dev/null; then
    FIREBASE_VERSION=$(firebase --version)
    print_status 0 "Firebase CLI is installed (version: $FIREBASE_VERSION)"
else
    print_status 1 "Firebase CLI is not installed"
    echo "  Install with: npm install -g firebase-tools"
fi
echo ""

# Check 2: Firebase project directory
echo "Checking Firebase project directory..."
if [ -d "fyp-backend" ]; then
    print_status 0 "fyp-backend directory exists"

    # Check firebase.json
    if [ -f "fyp-backend/firebase.json" ]; then
        print_status 0 "firebase.json exists"
    else
        print_status 1 "firebase.json is missing"
    fi

    # Check .firebaserc
    if [ -f "fyp-backend/.firebaserc" ]; then
        print_status 0 ".firebaserc exists"
        PROJECT_ID=$(grep -o '"default"[[:space:]]*:[[:space:]]*"[^"]*"' fyp-backend/.firebaserc | cut -d'"' -f4)
        echo "  Project ID: $PROJECT_ID"
    else
        print_status 1 ".firebaserc is missing"
    fi
else
    print_status 1 "fyp-backend directory not found"
fi
echo ""

# Check 3: Service Account Key File
echo "Checking service account key file..."
SA_FILES=("firebase-sa.json" "fyp-backend/firebase-sa.json")
SA_FOUND=false

for SA_FILE in "${SA_FILES[@]}"; do
    if [ -f "$SA_FILE" ]; then
        FILE_SIZE=$(stat -f%z "$SA_FILE" 2>/dev/null || stat -c%s "$SA_FILE" 2>/dev/null)
        if [ "$FILE_SIZE" -gt 100 ]; then
            print_status 0 "$SA_FILE exists and is not empty"
            SA_FOUND=true

            # Validate JSON structure
            if command -v python3 &> /dev/null; then
                if python3 -c "import json; json.load(open('$SA_FILE'))" 2>/dev/null; then
                    print_status 0 "$SA_FILE is valid JSON"
                else
                    print_status 1 "$SA_FILE is not valid JSON"
                fi
            fi
        else
            print_status 1 "$SA_FILE exists but is empty"
        fi
    fi
done

if [ "$SA_FOUND" = false ]; then
    print_warning "No service account key file found"
    echo "  This is OK if you're using 'firebase login' authentication"
fi
echo ""

# Check 4: Environment Variables
echo "Checking environment variables..."
if [ -n "$GOOGLE_APPLICATION_CREDENTIALS" ]; then
    print_status 0 "GOOGLE_APPLICATION_CREDENTIALS is set"
    echo "  Path: $GOOGLE_APPLICATION_CREDENTIALS"

    if [ -f "$GOOGLE_APPLICATION_CREDENTIALS" ]; then
        print_status 0 "File at GOOGLE_APPLICATION_CREDENTIALS path exists"
    else
        print_status 1 "File at GOOGLE_APPLICATION_CREDENTIALS path does not exist"
    fi
else
    print_warning "GOOGLE_APPLICATION_CREDENTIALS is not set"
    echo "  This is OK if you're using 'firebase login' authentication"
fi
echo ""

# Check 5: Firebase CLI Authentication
echo "Checking Firebase CLI authentication..."
if command -v firebase &> /dev/null; then
    cd fyp-backend 2>/dev/null || true

    if firebase projects:list &> /dev/null; then
        print_status 0 "Firebase CLI is authenticated"

        # Try to list the specific project
        if firebase projects:list | grep -q "isa-fyp"; then
            print_status 0 "Project 'isa-fyp' is accessible"
        else
            print_warning "Project 'isa-fyp' not found in accessible projects"
            echo "  You may need to be granted access to this project"
        fi
    else
        print_status 1 "Firebase CLI is not authenticated"
        echo "  Run 'firebase login' to authenticate"
    fi

    cd - > /dev/null 2>&1 || true
fi
echo ""

# Summary
echo "=========================================="
echo "Summary"
echo "=========================================="
echo ""

if [ $ISSUES_FOUND -eq 0 ]; then
    echo -e "${GREEN}All checks passed!${NC}"
    echo "Firebase MCP server should be able to access your Firebase project."
    exit 0
else
    echo -e "${RED}Found $ISSUES_FOUND issue(s)${NC}"
    echo ""
    echo "Authentication Status:"

    # Determine authentication method
    if [ "$SA_FOUND" = true ] && [ -n "$GOOGLE_APPLICATION_CREDENTIALS" ]; then
        echo -e "  ${GREEN}✓${NC} Service Account authentication is configured"
    elif command -v firebase &> /dev/null && firebase projects:list &> /dev/null 2>&1; then
        echo -e "  ${GREEN}✓${NC} Firebase CLI authentication is configured"
    else
        echo -e "  ${RED}✗${NC} No authentication method is configured"
        echo ""
        echo "Next Steps:"
        echo "  1. For local development: Run 'firebase login'"
        echo "  2. For CI/CD: Set up service account key (see docs/FIREBASE_MCP_SETUP_GUIDE.md)"
    fi
    exit 1
fi
