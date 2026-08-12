#!/bin/bash

# ════════════════════════════════════════════════════════════════
#  RoboTest — Ultimate Universal 1-Command Server Starter
#  Builds Frontend, Packages Backend, Launches App & Tunnel in Background
#  Usage: ./start-server.sh [URL] [TUNNEL_NAME]
# ════════════════════════════════════════════════════════════════

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo -e "\n${BOLD}${CYAN}====================================================${NC}"
echo -e "${BOLD}${CYAN}   RoboTest Universal Full-Stack Server Starter     ${NC}"
echo -e "${BOLD}${CYAN}====================================================${NC}\n"

# ── 1. Read / Sync Environment Variables ──────────────────────────
BACKEND_ENV="$SCRIPT_DIR/.env"

if [ ! -f "$BACKEND_ENV" ]; then
  echo -e "${YELLOW}Creating default .env file from .env.example...${NC}"
  if [ -f "$SCRIPT_DIR/.env.example" ]; then
    cp "$SCRIPT_DIR/.env.example" "$BACKEND_ENV"
  else
    cat <<EOF > "$BACKEND_ENV"
DATABASE_URL=jdbc:postgresql://localhost:5432/robotest
PGUSER=robotest
POSTGRES_PASSWORD=robotest123
FRONTEND_URL=https://live.rmecad.top
APP_BASE_URL=https://live.rmecad.top
MAIL_USER=your-email@gmail.com
MAIL_PASS=your-mail-pass
GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET=YOUR_GOOGLE_CLIENT_SECRET
EOF
  fi
fi

# Parse URL from parameter or .env
set -a
source "$BACKEND_ENV"
set +a

TUNNEL_URL="${1:-${FRONTEND_URL:-https://live.rmecad.top}}"
TUNNEL_NAME="${2:-rmecad-live}"
TUNNEL_URL="${TUNNEL_URL%/}"

# Ensure FRONTEND_URL and APP_BASE_URL match in .env
if grep -q "^FRONTEND_URL=" "$BACKEND_ENV"; then
  sed -i '' "s|^FRONTEND_URL=.*|FRONTEND_URL=$TUNNEL_URL|" "$BACKEND_ENV" 2>/dev/null || sed -i "s|^FRONTEND_URL=.*|FRONTEND_URL=$TUNNEL_URL|" "$BACKEND_ENV"
fi
if grep -q "^APP_BASE_URL=" "$BACKEND_ENV"; then
  sed -i '' "s|^APP_BASE_URL=.*|APP_BASE_URL=$TUNNEL_URL|" "$BACKEND_ENV" 2>/dev/null || sed -i "s|^APP_BASE_URL=.*|APP_BASE_URL=$TUNNEL_URL|" "$BACKEND_ENV"
fi

echo -e "${GREEN}✔ Unified Target URL:${NC} ${BOLD}${CYAN}$TUNNEL_URL${NC}"

# ── 2. Check / Auto-Install Docker (if needed) ────────────────────
install_docker() {
  if ! command -v docker &> /dev/null; then
    echo -e "${YELLOW}Docker is not installed.${NC}"
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
      echo -e "${BLUE}Attempting automatic Docker installation on Linux...${NC}"
      curl -fsSL https://get.docker.com | sh
      sudo usermod -aG docker $USER || true
      echo -e "${GREEN}✔ Docker installed successfully.${NC}"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
      echo -e "${YELLOW}Please install Docker Desktop for macOS: https://docs.docker.com/desktop/install/mac-install/${NC}"
    fi
  fi
}

install_docker

# ── 3. Clean up existing processes ────────────────────────────────
if [ -f "$SCRIPT_DIR/stop-server.sh" ]; then
  echo -e "${BLUE}[1/4]${NC} Stopping previous instances..."
  bash "$SCRIPT_DIR/stop-server.sh" --quiet || true
fi

# ── 4. Build Frontend & Embed into Spring Boot Static Folder ─────
FRONTEND_DIR="$SCRIPT_DIR/../robotest-frontend"
STATIC_DIR="$SCRIPT_DIR/src/main/resources/static"

if [ -d "$FRONTEND_DIR" ]; then
  echo -e "${BLUE}[2/4]${NC} Building frontend at $FRONTEND_DIR..."
  echo "VITE_API_URL=$TUNNEL_URL" > "$FRONTEND_DIR/.env"
  (cd "$FRONTEND_DIR" && npm run build)
  
  echo -e "${BLUE}[3/4]${NC} Embedding static frontend files into Spring Boot..."
  mkdir -p "$STATIC_DIR"
  rm -rf "$STATIC_DIR"/*
  cp -r "$FRONTEND_DIR/dist/"* "$STATIC_DIR/"
  echo -e "${GREEN}✔ Static files embedded successfully${NC}"
fi

# ── 5. Package & Launch App ──────────────────────────────────────
echo -e "${BLUE}[4/4]${NC} Packaging and starting backend service..."

if command -v docker &> /dev/null && command -v docker-compose &> /dev/null || docker compose version &> /dev/null; then
  echo -e "${CYAN}Docker detected. Using Docker mode...${NC}"
  if [ -f "$SCRIPT_DIR/../docker-compose.yml" ]; then
    (cd "$SCRIPT_DIR/.." && docker compose up -d --build)
  else
    mvn clean package -DskipTests -q
    nohup java -jar target/robotest-backend-1.0.0.jar > backend.log 2>&1 &
    echo $! > backend.pid
  fi
else
  echo -e "${CYAN}Running in Native Java mode...${NC}"
  mvn clean package -DskipTests -q
  nohup java -jar target/robotest-backend-1.0.0.jar > backend.log 2>&1 &
  echo $! > backend.pid
fi

# ── 6. Start Cloudflare Tunnel in Background ──────────────────────
if command -v cloudflared &> /dev/null; then
  echo -e "${BLUE}Starting Cloudflare Tunnel '$TUNNEL_NAME' in background...${NC}"
  nohup cloudflared tunnel run "$TUNNEL_NAME" > cloudflared.log 2>&1 &
  echo $! > cloudflared.pid
  echo -e "${GREEN}✔ Cloudflare Tunnel started${NC}"
else
  echo -e "${YELLOW}cloudflared CLI not found. Running server locally on port 8080.${NC}"
fi

sleep 3

echo -e "\n${BOLD}${GREEN}====================================================${NC}"
echo -e "${BOLD}${GREEN}   SUCCESS! RoboTest Full-Stack Platform is LIVE!   ${NC}"
echo -e "${BOLD}${GREEN}====================================================${NC}"
echo -e "\n🌐 Public URL:   ${BOLD}${CYAN}$TUNNEL_URL${NC}"
echo -e "📄 Backend Log:  ${CYAN}tail -f backend.log${NC}"
echo -e "📄 Tunnel Log:   ${CYAN}tail -f cloudflared.log${NC}"
echo -e "🛑 Stop Server:  ${CYAN}./stop-server.sh${NC}"
echo -e "\n${YELLOW}You can safely close this terminal window now.${NC}\n"
