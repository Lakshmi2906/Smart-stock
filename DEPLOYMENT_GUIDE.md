# 🚀 SmartStock Enterprise — Complete Deployment Guide

> **Stack:** Supabase (DB) → Render (Backend JAR) → Netlify / Vercel (Frontend)

---

## 📋 OVERVIEW

```
Your Browser
    │
    ▼
Netlify / Vercel  ──── serves ────► index.html + style.css + script.js
    │
    │  (API calls to)
    ▼
Render.com  ──── runs ────► Spring Boot JAR (port 8080)
    │
    │  (reads/writes)
    ▼
Supabase  ──── PostgreSQL database
```

---

## STEP 1 — SUPABASE (Database)

### 1.1 Create Account & Project
1. Go to **https://supabase.com** → Sign Up (free)
2. Click **"New Project"**
3. Fill in:
   - **Name:** `smartstock`
   - **Database Password:** choose a strong password — **save this!**
   - **Region:** Asia (Singapore) — closest to India
4. Click **"Create new project"** → wait ~2 minutes

### 1.2 Run the Schema
1. In your Supabase project, click **"SQL Editor"** (left sidebar)
2. Click **"New query"**
3. Open `backend/src/main/resources/schema.sql` from your project
4. Copy the **entire file contents** and paste into the SQL Editor
5. Click **"Run"** (Ctrl+Enter)
6. You should see: `Success. No rows returned`

### 1.3 Get Your Credentials
1. Go to **Project Settings** → **Database**
2. Under **"Connection string"** → select **URI**
3. Copy the URI — it looks like:
   ```
   postgresql://postgres:[YOUR-PASSWORD]@db.xxxxxxxxxxxx.supabase.co:5432/postgres
   ```
4. Also go to **Project Settings → API** and note:
   - **Project URL**: `https://xxxxxxxxxxxx.supabase.co`
   - **anon/public key**: `eyJhbGciOi...`

### 1.4 Note Your Values (fill these in)
```
DB_URL = jdbc:postgresql://db.XXXX.supabase.co:5432/postgres
DB_USERNAME = postgres
DB_PASSWORD = your-db-password-from-step-1.1
```

---

## STEP 2 — RAZORPAY (Payment Keys)

1. Go to **https://razorpay.com** → Create account
2. Dashboard → **Settings → API Keys**
3. Click **"Generate Test Key"**
4. Copy:
   ```
   RAZORPAY_KEY_ID = rzp_test_XXXXXXXXXXXXXXXX
   RAZORPAY_KEY_SECRET = XXXXXXXXXXXXXXXXXXXXXXXX
   ```
5. Keep these — needed in Step 3

---

## STEP 3 — RENDER (Backend Deployment)

### 3.1 Build the JAR Locally First
Open terminal in your project folder:
```bash
cd backend
mvn clean package -DskipTests
```
This creates: `backend/target/smartstock-0.0.1-SNAPSHOT.jar`

### 3.2 Push to GitHub
```bash
# From JavaFullStack/ root
git init
git add .
git commit -m "Initial SmartStock commit"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/smartstock.git
git push -u origin main
```

### 3.3 Create Render Web Service
1. Go to **https://render.com** → Sign Up (free) → **"New +"** → **Web Service**
2. Connect your GitHub repo → Select **smartstock**
3. Fill in settings:

   | Field | Value |
   |-------|-------|
   | **Name** | `smartstock-backend` |
   | **Region** | Singapore |
   | **Branch** | `main` |
   | **Root Directory** | `backend` |
   | **Runtime** | `Java` |
   | **Build Command** | `mvn clean package -DskipTests` |
   | **Start Command** | `java -jar target/smartstock-0.0.1-SNAPSHOT.jar` |
   | **Instance Type** | Free |

4. Click **"Advanced"** → **"Add Environment Variable"** and add ALL of these:

   | Key | Value |
   |-----|-------|
   | `DB_URL` | `jdbc:postgresql://db.XXXX.supabase.co:5432/postgres` |
   | `DB_USERNAME` | `postgres` |
   | `DB_PASSWORD` | `your-supabase-password` |
   | `RAZORPAY_KEY_ID` | `rzp_test_XXXXXXXX` |
   | `RAZORPAY_KEY_SECRET` | `XXXXXXXXXXXXXXXX` |
   | `JWT_SECRET` | `any-64-character-random-string-here-make-it-long` |
   | `CORS_ORIGINS` | `https://your-netlify-app.netlify.app` |
   | `SERVER_PORT` | `8080` |

5. Click **"Create Web Service"**
6. Wait for deployment (~5-10 min). You'll get a URL like:
   ```
   https://smartstock-backend.onrender.com
   ```
   
> ⚠️ **Free Render instances sleep after 15 min of inactivity** — first request after sleep takes ~30 sec.

### 3.4 Test Backend is Live
Open browser:
```
https://smartstock-backend.onrender.com/api/products
```
Should return JSON list of products. ✅

---

## STEP 4A — NETLIFY (Frontend Deployment) ⭐ Recommended

### 4A.1 Update API URL in script.js
Open `frontend/script.js` and find/add at the top (after `'use strict';`):
```javascript
const API_BASE = 'https://smartstock-backend.onrender.com';
```

### 4A.2 Create netlify.toml
The file `frontend/netlify.toml` should already exist. If not, create it:
```toml
[build]
  publish = "."

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200
```

### 4A.3 Deploy via Netlify UI (Drag & Drop — Easiest)
1. Go to **https://netlify.com** → Sign Up → **"Add new site"** → **"Deploy manually"**
2. Drag and drop your **entire `frontend/` folder** onto the upload area
3. Done! You get a URL like: `https://amazing-name-123.netlify.app`
4. Click **"Site configuration"** → **"Change site name"** → rename to `smartstock-shop`

### 4A.4 Deploy via GitHub (Auto-deploy on push) — Better
1. **"Add new site"** → **"Import an existing project"** → GitHub
2. Select your repo
3. Build settings:

   | Field | Value |
   |-------|-------|
   | **Base directory** | `frontend` |
   | **Build command** | *(leave empty)* |
   | **Publish directory** | `frontend` |

4. Click **"Deploy site"**

### 4A.5 Update CORS on Render
After you get your Netlify URL (e.g. `https://smartstock-shop.netlify.app`):
1. Go to Render → Your service → **Environment**
2. Update `CORS_ORIGINS` to your Netlify URL
3. Click **"Save Changes"** — Render will redeploy

---

## STEP 4B — VERCEL (Alternative Frontend)

### 4B.1 Deploy via Vercel CLI
```bash
npm install -g vercel
cd frontend
vercel
```
Follow prompts:
- Set up and deploy? **Y**
- Which scope? Your account
- Link to existing project? **N**
- Project name: `smartstock-frontend`
- Directory: `./` (current frontend dir)
- Override settings? **N**

Vercel gives you: `https://smartstock-frontend.vercel.app`

### 4B.2 Deploy via Vercel Dashboard
1. Go to **https://vercel.com** → New Project → Import GitHub repo
2. Configure:

   | Field | Value |
   |-------|-------|
   | **Framework Preset** | Other |
   | **Root Directory** | `frontend` |
   | **Build Command** | *(leave empty)* |
   | **Output Directory** | `.` |

3. Click **Deploy**

### 4B.3 Update CORS on Render
Update `CORS_ORIGINS` env var to your Vercel URL and redeploy.

---

## STEP 5 — VERIFY EVERYTHING WORKS

### Checklist:
```
[ ] Supabase: SQL schema ran, tables exist (check Table Editor)
[ ] Render: /api/products returns JSON
[ ] Frontend: index.html loads on Netlify/Vercel URL
[ ] Login: OTP modal opens, enter any 10-digit number, OTP = 123456
[ ] Products: All 27 products load with images
[ ] Cart: Add to Cart works, badge updates
[ ] Cart page: cart.html shows items
[ ] Checkout: checkout.html shows order summary
```

### Quick Test URLs:
```
Backend health:   https://YOUR-BACKEND.onrender.com/api/products
Frontend home:    https://YOUR-SITE.netlify.app
Cart page:        https://YOUR-SITE.netlify.app/cart.html
Checkout:         https://YOUR-SITE.netlify.app/checkout.html
```

---

## 🔧 COMMON ISSUES & FIXES

| Problem | Fix |
|---------|-----|
| Render deploy fails | Check build logs — usually missing env vars |
| `Connection refused` on products API | DB_URL wrong — check Supabase connection string |
| CORS error in browser console | Update `CORS_ORIGINS` on Render with your Netlify URL |
| Products not loading on live site | `API_BASE` not updated in script.js |
| Render spins up slowly | Free tier — first request after sleep takes 30 sec, normal |
| Netlify shows 404 on refresh | Add `netlify.toml` redirects (Step 4A.2) |
| Login OTP not working | Use `123456` as OTP for test mode |

---

## 📁 FINAL PROJECT STRUCTURE

```
JavaFullStack/
├── frontend/          ← Deploy to Netlify or Vercel
│   ├── index.html
│   ├── cart.html
│   ├── checkout.html
│   ├── admin.html
│   ├── style.css
│   ├── script.js      ← Update API_BASE here
│   └── netlify.toml
│
├── backend/           ← Deploy to Render
│   ├── pom.xml
│   ├── .env.example   ← All required env vars listed here
│   └── src/
│       └── main/
│           ├── java/com/smartstock/
│           └── resources/
│               ├── application.properties
│               └── schema.sql   ← Run this in Supabase
│
└── DEPLOYMENT_GUIDE.md  ← This file
```

---

## 🔑 ALL ENVIRONMENT VARIABLES SUMMARY

Copy this to Render Environment Variables:

```env
DB_URL=jdbc:postgresql://db.XXXX.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=your_supabase_password
RAZORPAY_KEY_ID=rzp_test_xxxxxxxxxxxxxxxx
RAZORPAY_KEY_SECRET=xxxxxxxxxxxxxxxxxxxxxxxx
JWT_SECRET=your_64_char_random_secret_key_here_make_it_very_long_and_random
CORS_ORIGINS=https://your-site.netlify.app
SERVER_PORT=8080
```

To generate a JWT secret:
```bash
# Mac/Linux:
openssl rand -hex 32

# Or use: https://generate-random.org/api-key-generator (64 chars)
```
