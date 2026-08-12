# RoboTest Full-Stack Platform — Universal Deployment Guide

Full-stack Spring Boot 3.2 + Vue 3 platform with zero-configuration automated background deployment, single `.env` synchronization, and Cloudflare Tunnel integration.

---

## 🚀 Master 1-Click Setup & Server Start

You can run the entire platform (Frontend, Backend, and Cloudflare Tunnel) in the background on any machine with a single command. 

Place `run-all.sh` in your main workspace folder (outside the repositories):

```text
my-workspace/
├── run-all.sh        <-- Master runner script
├── stop-all.sh       <-- Master stop script
├── robotest-backend/ <-- Backend Git repository
└── robotest-frontend/ <-- Frontend Git repository
```

### ⚡ Run Master Command:
```bash
./run-all.sh
```

*(Optional custom URL / tunnel: `./run-all.sh https://live.rmecad.top rmecad-live`)*

---

### 🛡️ What `run-all.sh` does automatically:

1. **Automatic `.env` Creation:**
   - `.env` files are **never committed to GitHub** (kept in `.gitignore` for security).
   - If `.env` is missing, `run-all.sh` automatically creates it safely from `.env.example`.
2. **Unified URL Synchronization:**
   - Automatically synchronizes `FRONTEND_URL`, `APP_BASE_URL` (in backend `.env`) and `VITE_API_URL` (in frontend `.env`) to your live domain (`https://live.rmecad.top`).
3. **Auto Docker Setup:**
   - Detects Docker/Docker Compose. Automatically installs Docker on Linux if missing.
4. **Frontend Build & Embed:**
   - Compiles Vue 3 frontend (`robotest-frontend`) and embeds static build assets directly into `robotest-backend/src/main/resources/static/`.
5. **Backend Packaging:**
   - Packages Spring Boot executable JAR (`target/robotest-backend-1.0.0.jar`).
6. **Persistent Background Execution:**
   - Launches Spring Boot backend and Cloudflare Tunnel (`rmecad-live`) via `nohup` in the background.
   - **You can close your terminal and the website stays online!**

---

## 🛠️ Server Management Commands

| Task | Command |
|------|---------|
| **Start Everything** | `./run-all.sh` |
| **Stop Everything** | `./stop-all.sh` |
| **View Backend Logs** | `tail -f robotest-backend/backend.log` |
| **View Tunnel Logs** | `tail -f robotest-backend/cloudflared.log` |

---

## 🌐 One-Time Cloudflare Tunnel Setup (`live.rmecad.top`)

To map your domain (`live.rmecad.top`) to your local machine:

1. **Authenticate Cloudflare CLI:**
   ```bash
   cloudflared tunnel login
   ```
2. **Create Named Tunnel:**
   ```bash
   cloudflared tunnel create rmecad-live
   ```
3. **Route Subdomain DNS:**
   ```bash
   cloudflared tunnel route dns rmecad-live live.rmecad.top
   ```
4. **Configure `~/.cloudflared/config.yml`:**
   ```yaml
   tunnel: <YOUR-TUNNEL-UUID>
   credentials-file: /home/<user>/.cloudflared/<YOUR-TUNNEL-UUID>.json

   ingress:
     - hostname: live.rmecad.top
       service: http://localhost:8080
     - service: http_status:404
   ```

---

## 🔐 Google OAuth2 Setup

Add your domain to [Google Cloud Console](https://console.cloud.google.com/apis/credentials):

* **Authorized JavaScript origins:** `https://live.rmecad.top`
* **Authorized redirect URIs:** `https://live.rmecad.top/login/oauth2/code/google`

---

## 💻 Local Development Mode (Optional)

To run frontend and backend separately in development mode:

### 1. Backend
```bash
cd robotest-backend
mvn spring-boot:run
```
*Backend runs on `http://localhost:8080`*

### 2. Frontend
```bash
cd robotest-frontend
npm run dev
```
*Frontend runs on `http://localhost:5173`*
