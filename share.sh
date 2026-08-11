#!/bin/bash

# ════════════════════════════════════════════════════════════════
#  RoboTest Backend — Share & Embed Frontend Helper
#  Usage: ./share.sh <tunnel-url>
#  Example: ./share.sh https://arrangements-lloyd-eds-oxide.trycloudflare.com
# ════════════════════════════════════════════════════════════════

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$SCRIPT_DIR/../robotest-frontend"
STATIC_DIR="$SCRIPT_DIR/src/main/resources/static"
BACKEND_ENV="$SCRIPT_DIR/.env"

TUNNEL_URL="${1%/}"

if [ -z "$TUNNEL_URL" ]; then
  echo -e "${YELLOW}Enter your public Tunnel URL (e.g. Cloudflare / ngrok):${NC}"
  read -p "URL: " TUNNEL_URL
  TUNNEL_URL="${TUNNEL_URL%/}"
fi

echo -e "\n${BOLD}${CYAN}Configuring RoboTest Backend for: ${TUNNEL_URL}${NC}\n"

# 1. Update Backend .env if it exists
if [ -f "$BACKEND_ENV" ]; then
  echo -e "${BLUE}[1/3]${NC} Updating backend .env..."
  if grep -q "^FRONTEND_URL=" "$BACKEND_ENV"; then
    sed -i '' "s|^FRONTEND_URL=.*|FRONTEND_URL=$TUNNEL_URL|" "$BACKEND_ENV" 2>/dev/null || sed -i "s|^FRONTEND_URL=.*|FRONTEND_URL=$TUNNEL_URL|" "$BACKEND_ENV"
  else
    echo "FRONTEND_URL=$TUNNEL_URL" >> "$BACKEND_ENV"
  fi

  if grep -q "^APP_BASE_URL=" "$BACKEND_ENV"; then
    sed -i '' "s|^APP_BASE_URL=.*|APP_BASE_URL=$TUNNEL_URL|" "$BACKEND_ENV" 2>/dev/null || sed -i "s|^APP_BASE_URL=.*|APP_BASE_URL=$TUNNEL_URL|" "$BACKEND_ENV"
  else
    echo "APP_BASE_URL=$TUNNEL_URL" >> "$BACKEND_ENV"
  fi
  echo -e "${GREEN}✔ Backend .env updated${NC}"
fi

# 2. Build frontend if present at sibling directory
if [ -d "$FRONTEND_DIR" ]; then
  echo -e "${BLUE}[2/3]${NC} Building frontend at $FRONTEND_DIR..."
  FRONTEND_ENV="$FRONTEND_DIR/.env"
  echo "VITE_API_URL=$TUNNEL_URL" > "$FRONTEND_ENV"
  (cd "$FRONTEND_DIR" && npm run build)
  
  echo -e "${BLUE}[3/3]${NC} Copying built static files into Spring Boot static resources..."
  mkdir -p "$STATIC_DIR"
  rm -rf "$STATIC_DIR"/*
  cp -r "$FRONTEND_DIR/dist/"* "$STATIC_DIR/"
  echo -e "${GREEN}✔ Static frontend files copied to static/${NC}"
else
  echo -e "${YELLOW}Frontend repo not found at $FRONTEND_DIR. Skipping static build copy.${NC}"
fi

echo -e "\n${BOLD}${GREEN}✔ Configuration Complete!${NC}"
echo -e "Restart the backend in IntelliJ and share: ${CYAN}$TUNNEL_URL${NC}\n"
