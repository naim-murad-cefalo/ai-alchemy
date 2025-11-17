#!/bin/bash

# ============================================
# Wish Tracker - Development Run with Seed Data
# ============================================
# This script runs the application in development mode with seed data
# (No build step - faster for development)
# Usage: ./dev-run-with-seed.sh

set -e

echo "╔════════════════════════════════════════════════════════════╗"
echo "║    Wish Tracker - Development Mode with Seed Data         ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Error: Maven is not installed or not in PATH${NC}"
    echo "Please install Maven: https://maven.apache.org/install.html"
    exit 1
fi

echo -e "${BLUE}🚀 Starting application in development mode...${NC}"
echo -e "${YELLOW}This will create demo data in the database${NC}"
echo ""
echo -e "${YELLOW}Demo User:${NC}"
echo -e "  Email: demo@wishtracker.com"
echo -e "  Name: Demo User"
echo ""
echo -e "${YELLOW}Seed Data Includes:${NC}"
echo -e "  ✓ 6 Categories (Travel, Books, Fitness, Career, Hobbies, Learning)"
echo -e "  ✓ 7 Wishes in 'WISH' status"
echo -e "  ✓ 6 Wishes in 'IN_PROGRESS' status"
echo -e "  ✓ 6 Wishes in 'ACHIEVED' status"
echo ""
echo -e "${GREEN}Starting server with hot reload enabled...${NC}"
echo -e "${YELLOW}Press Ctrl+C to stop${NC}"
echo ""

# Run with Maven Spring Boot plugin for development
mvn spring-boot:run -Dspring-boot.run.profiles=seed

echo ""
echo -e "${GREEN}Application stopped.${NC}"
