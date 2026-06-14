# Frontend Installation and Setup Guide

## Project Information
- **Framework**: React 19
- **Build Tool**: Vite
- **Package Manager**: npm
- **Node.js Version Required**: 16.x or higher

## Step 1: Verify Node.js Installation

```bash
node --version
npm --version
```

Both commands should return version numbers.

## Step 2: Install Dependencies

Navigate to the Frontend directory and run:

```bash
npm install
```

This will install all required modules:
- **react**: ^19.2.6
- **react-dom**: ^19.2.6
- **react-router-dom**: ^6.30.4 (routing)
- **axios**: ^1.17.0 (HTTP client)
- **bootstrap**: ^5.3.8 (UI framework)
- **jwt-decode**: ^4.0.0 (JWT parsing)
- Plus all dev dependencies

## Step 3: Start Development Server

```bash
npm run dev
```

The server will start on `http://localhost:5173` by default.

## Available Scripts

```bash
# Development server
npm run dev

# Build for production
npm build

# Preview production build
npm run preview

# Run ESLint
npm run lint
```

## Troubleshooting Module Resolution Issues

### Issue: "Module not found: Can't resolve 'bootstrap'"

**Solution 1: Clean Install**
```bash
# Delete node_modules
rm -r node_modules
# or on Windows:
rmdir /s /q node_modules

# Clear npm cache
npm cache clean --force

# Reinstall
npm install
```

**Solution 2: Check package.json**
- Ensure all required dependencies are listed
- Verify JSON syntax is valid
- No missing commas or extra brackets

**Solution 3: Verify Imports**
All import statements should be:
```javascript
import 'bootstrap/dist/css/bootstrap.min.css';
import { BrowserRouter as Router } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
```

### Issue: "Cannot find module 'react-router-dom'"

This means node_modules is missing or incomplete. Run:
```bash
npm install react-router-dom@^6.30.4
```

Or reinstall all dependencies:
```bash
npm install
```

### Issue: Port 5173 already in use

Run the dev server on a different port:
```bash
npm run dev -- --port 5174
```

## Environment Setup

The frontend expects the backend API to be running on:
```
http://localhost:8080/api
```

If your backend is on a different URL, edit `src/services/api.js`:
```javascript
const API_BASE_URL = 'http://your-api-url:8080/api';
```

## Project Structure

```
Frontend/
├── src/
│   ├── components/
│   │   ├── Navbar.jsx
│   │   └── ProtectedRoute.jsx
│   ├── context/
│   │   └── AuthContext.jsx
│   ├── pages/
│   │   ├── Home.jsx
│   │   ├── Login.jsx
│   │   ├── Register.jsx
│   │   ├── Stores.jsx
│   │   ├── StoreDetail.jsx
│   │   ├── AdminDashboard.jsx
│   │   └── StoreForm.jsx
│   ├── services/
│   │   └── api.js
│   ├── utils/
│   │   └── useAuth.js
│   ├── App.jsx
│   ├── App.css
│   ├── index.css
│   └── main.jsx
├── package.json
├── vite.config.js
├── index.html
└── .npmrc
```

## Verification Checklist

- [ ] Node.js version 16+ installed
- [ ] npm version 8+ installed
- [ ] Ran `npm install` successfully
- [ ] Backend is running on http://localhost:8080
- [ ] Run `npm run dev` without errors
- [ ] Frontend loads on http://localhost:5173
- [ ] No "Module not found" errors in console
- [ ] All pages load and render correctly

## Quick Setup Command (Windows)

```bash
cd Frontend
npm install
npm run dev
```

## Quick Setup Command (Linux/Mac)

```bash
cd Frontend
npm install
npm run dev
```

## Support Files

- `SETUP.bat` - Windows setup script
- `SETUP.sh` - Linux/Mac setup script
- `.npmrc` - NPM configuration for reliable resolution
- `package.json` - All dependencies listed
