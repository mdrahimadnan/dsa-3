# BioGenome PowerShell Launcher
$ErrorActionPreference = "SilentlyContinue"

Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "    BIOGENOME COMPUTATIONAL PLATFORM (Modules 1-6)" -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan

$javac = "javac"
$java = "java"

$jdkPath = "$env:USERPROFILE\.jdks\openjdk-26.0.2\bin"
if (Test-Path "$jdkPath\javac.exe") {
    $javac = "$jdkPath\javac.exe"
    $java = "$jdkPath\java.exe"
}

Write-Host "[1/2] Compiling source files..." -ForegroundColor Yellow
if (!(Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }

$srcFiles = Get-ChildItem -Recurse -Filter "*.java" src | Select-Object -ExpandProperty FullName
& $javac --release 8 -Xlint:-options -d bin $srcFiles

Write-Host "[2/2] Launching BioGenome..." -ForegroundColor Green
Write-Host "=======================================================" -ForegroundColor Cyan
& $java -cp bin Main
