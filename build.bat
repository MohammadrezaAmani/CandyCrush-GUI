@echo off
REM Candy Crush GUI Build Script for Windows
REM This script compiles and runs the Candy Crush GUI game

echo Candy Crush GUI - Build Script
echo ===============================

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java JDK is not installed or not in PATH.
    echo Please install Java JDK 11 or higher and try again.
    exit /b 1
)

REM Create build directories if they don't exist
if not exist bin mkdir bin
if not exist src\main\resources\images mkdir src\main\resources\images
if not exist src\main\resources\sounds mkdir src\main\resources\sounds
if not exist src\main\resources\fonts mkdir src\main\resources\fonts

echo Compiling project...

REM Compile all Java files
javac -d bin -sourcepath src\main\java src\main\java\candycrush\Main.java

REM Check if compilation was successful
if errorlevel 0 (
    echo Compilation successful!
    
    REM Copy resources to bin directory
    echo Copying resources...
    if not exist bin\resources mkdir bin\resources
    xcopy /E /Y /Q src\main\resources\* bin\resources\ >nul 2>&1
    
    REM Run the application
    echo Starting Candy Crush...
    java -cp bin candycrush.Main
) else (
    echo Compilation failed. Please check the errors above.
    exit /b 1
)