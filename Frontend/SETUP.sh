#!/bin/bash

# Frontend Setup Instructions for Store Rating App

echo "========================================="
echo "Store Rating App - Frontend Setup"
echo "========================================="

# Step 1: Navigate to Frontend directory
echo ""
echo "Step 1: Navigate to Frontend directory..."
cd "$(dirname "$0")"

# Step 2: Install dependencies
echo ""
echo "Step 2: Installing dependencies..."
echo "Command: npm install"
npm install

# Check if npm install succeeded
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Dependencies installed successfully!"
    echo ""
    echo "========================================="
    echo "Next Steps:"
    echo "========================================="
    echo ""
    echo "1. Make sure the backend is running on http://localhost:8080"
    echo ""
    echo "2. Start the development server:"
    echo "   npm run dev"
    echo ""
    echo "3. Open your browser to http://localhost:5173"
    echo ""
    echo "4. If modules are still not resolving:"
    echo "   - Delete node_modules folder: rm -r node_modules"
    echo "   - Clear npm cache: npm cache clean --force"
    echo "   - Reinstall: npm install"
    echo ""
    echo "========================================="
else
    echo ""
    echo "❌ npm install failed. Please check your internet connection and try again."
    exit 1
fi
