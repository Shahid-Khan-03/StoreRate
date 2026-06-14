@echo off
setlocal enabledelayedexpansion

echo =========================================
echo Store Rating App - Frontend Setup
echo =========================================
echo.

echo Step 1: Checking package.json...
if exist "package.json" (
    echo ✓ package.json found
) else (
    echo ✗ package.json not found. Please run this from the Frontend directory.
    pause
    exit /b 1
)

echo.
echo Step 2: Installing dependencies...
echo Command: npm install
echo.

call npm install

if !errorlevel! equ 0 (
    echo.
    echo ✓ Dependencies installed successfully!
    echo.
    echo =========================================
    echo Next Steps:
    echo =========================================
    echo.
    echo 1. Make sure the backend is running on http://localhost:8080
    echo.
    echo 2. Start the development server:
    echo    npm run dev
    echo.
    echo 3. Open your browser to http://localhost:5173
    echo.
    echo 4. If modules are still not resolving:
    echo    - Delete node_modules folder
    echo    - Clear npm cache: npm cache clean --force
    echo    - Reinstall: npm install
    echo.
    echo =========================================
    pause
) else (
    echo.
    echo ✗ npm install failed. Please check your internet connection.
    pause
    exit /b 1
)
