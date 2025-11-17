#!/bin/bash

# ============================================
# Wish Tracker - Manual Seed Data Script
# ============================================
# This script can be run while the app is running to add seed data
# via SQL (requires access to the database)
# Usage: ./seed-data.sh

set -e

echo "╔════════════════════════════════════════════════════════════╗"
echo "║         Wish Tracker - Manual Database Seeding            ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Note: This script is for manual database seeding.${NC}"
echo -e "${YELLOW}For automatic seeding, use:${NC}"
echo -e "  ${BLUE}./run-with-seed.sh${NC}        (Production mode)"
echo -e "  ${BLUE}./dev-run-with-seed.sh${NC}    (Development mode)"
echo ""
echo -e "${YELLOW}Or run with Maven:${NC}"
echo -e "  ${BLUE}mvn spring-boot:run -Dspring-boot.run.profiles=seed${NC}"
echo ""
echo -e "${GREEN}✓ For seeding, please run the application with the 'seed' profile${NC}"
echo ""
