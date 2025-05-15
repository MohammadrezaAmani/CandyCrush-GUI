#!/bin/bash

# Candy Crush GUI Build Script
# This script compiles and runs the Candy Crush GUI game

# Colors for terminal output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Candy Crush GUI - Build Script${NC}"
echo "==============================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo -e "${RED}Error: Java JDK is not installed or not in PATH.${NC}"
    echo "Please install Java JDK 11 or higher and try again."
    exit 1
fi

# Create build directories if they don't exist
mkdir -p bin
mkdir -p src/main/resources/{images,sounds,fonts}

echo -e "${GREEN}Compiling project...${NC}"

# Compile all Java files
javac -d bin -sourcepath src/main/java src/main/java/candycrush/Main.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Compilation successful!${NC}"
    
    # Copy resources to bin directory
    echo "Copying resources..."
    mkdir -p bin/resources
    cp -r src/main/resources/* bin/resources/ 2>/dev/null || :
    
    # Run the application
    echo -e "${GREEN}Starting Candy Crush...${NC}"
    java -cp bin candycrush.Main
else
    echo -e "${RED}Compilation failed. Please check the errors above.${NC}"
    exit 1
fi