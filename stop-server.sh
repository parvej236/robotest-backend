#!/bin/bash

# ════════════════════════════════════════════════════════════════
#  RoboTest — Stop Live Server & Tunnel
#  Usage: ./stop-server.sh
# ════════════════════════════════════════════════════════════════

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

QUIET=false
if [ "$1" == "--quiet" ]; then
  QUIET=true
fi

if [ "$QUIET" = false ]; then
  echo -e "\n${BOLD}${CYAN}Stopping RoboTest Full-Stack Server & Tunnel...${NC}\n"
fi

# Stop Backend via PID or process name
if [ -f backend.pid ]; then
  BPID=$(cat backend.pid)
  if kill -0 "$BPID" 2>/dev/null; then
    kill "$BPID" 2>/dev/null || kill -9 "$BPID" 2>/dev/null
    [ "$QUIET" = false ] && echo -e "${GREEN}✔ Stopped backend (PID: $BPID)${NC}"
  fi
  rm -f backend.pid
fi

# Also kill any leftover java robotest processes
pkill -f "robotest-backend" 2>/dev/null || true

# Stop Cloudflare Tunnel via PID or process name
if [ -f cloudflared.pid ]; then
  TPID=$(cat cloudflared.pid)
  if kill -0 "$TPID" 2>/dev/null; then
    kill "$TPID" 2>/dev/null || kill -9 "$TPID" 2>/dev/null
    [ "$QUIET" = false ] && echo -e "${GREEN}✔ Stopped Cloudflare Tunnel (PID: $TPID)${NC}"
  fi
  rm -f cloudflared.pid
fi

# Also kill any leftover cloudflared tunnel processes matching rmecad-live
pkill -f "cloudflared tunnel run" 2>/dev/null || true

if [ "$QUIET" = false ]; then
  echo -e "\n${BOLD}${GREEN}✔ All background processes stopped successfully.${NC}\n"
fi
