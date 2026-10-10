@echo off
setlocal enabledelayedexpansion
title BioGenome Analysis Platform - Modules 1-6 (CO1-CO6)
cd /d "%~dp0"

echo =======================================================
echo     BIOGENOME COMPUTATIONAL PLATFORM (Modules 1-6)
echo =======================================================

if not exist bin mkdir bin

:: Auto-detect JDK / Javac
set "JAVAC_CMD=javac"
set "JAVA_CMD=java"

if exist "%USERPROFILE%\.jdks\openjdk-26.0.2\bin\javac.exe" (
    set "JAVAC_CMD=%USERPROFILE%\.jdks\openjdk-26.0.2\bin\javac.exe"
    set "JAVA_CMD=%USERPROFILE%\.jdks\openjdk-26.0.2\bin\java.exe"
) else if exist "C:\Program Files\Java\jdk*\bin\javac.exe" (
    for /d %%D in ("C:\Program Files\Java\jdk*") do (
        set "JAVAC_CMD=%%D\bin\javac.exe"
        set "JAVA_CMD=%%D\bin\java.exe"
    )
)

echo [1/2] Compiling BioGenome Java sources...
"%JAVAC_CMD%" --release 8 -Xlint:-options -d bin src\m1\*.java src\m2\*.java src\m3\*.java src\m4\*.java src\m5\*.java src\m6\*.java src\Main.java 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [NOTE] Using existing pre-compiled classes in bin\...
) else (
    echo [SUCCESS] Source code compiled successfully.
)

echo.
echo [2/2] Launching BioGenome Platform...
echo =======================================================
echo.

"%JAVA_CMD%" -cp bin Main
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Execution encountered an issue.
    echo Trying fallback java command...
    java -cp bin Main
)

echo.
echo Program exited.
pause
