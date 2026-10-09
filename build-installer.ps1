# Builds the RETROES Windows 11 installer: dist/RETROES-<version>.exe
# Requires: JDK 14+ with jpackage, WiX Toolset 3.x (candle/light on PATH or default location).
param(
    [string]$Jdk = "C:\Program Files\Java\jdk-27",
    [string]$Wix = "C:\Program Files (x86)\WiX Toolset v3.14\bin"
)

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot

# --- Preconditions ---------------------------------------------------------
$javac = Join-Path $Jdk "bin\javac.exe"
$jar   = Join-Path $Jdk "bin\jar.exe"
$jlink = Join-Path $Jdk "bin\jlink.exe"
$jpackage = Join-Path $Jdk "bin\jpackage.exe"
foreach ($exe in @($javac, $jar, $jlink, $jpackage)) {
    if (-not (Test-Path $exe)) { throw "Missing $exe - check -Jdk parameter" }
}
$candle = Join-Path $Wix "candle.exe"
if (-not (Test-Path $candle)) {
    $found = Get-Command candle -ErrorAction SilentlyContinue
    if ($found) { $Wix = Split-Path $found.Source }
    else { throw "WiX candle.exe not found - check -Wix parameter" }
}
$env:PATH = "$Wix;$env:PATH"

# Version from app/AppVersion.java
$verText = Get-Content (Join-Path $root "app\AppVersion.java") -Raw
if ($verText -notmatch 'VERSION\s*=\s*"([^"]+)"') { throw "Could not read VERSION from app/AppVersion.java" }
$version = $Matches[1]

$build = Join-Path $root "build"
$stage = Join-Path $build "stage"
$dist  = Join-Path $root "dist"
$classes = Join-Path $build "classes"
$jlinkImage = Join-Path $build "runtime"

Write-Host "==> Building RETROES v$version"

# --- Clean -----------------------------------------------------------------
if (Test-Path $build) { Remove-Item $build -Recurse -Force }
if (Test-Path $dist)  { Remove-Item $dist  -Recurse -Force }
New-Item -ItemType Directory -Path $classes, $stage, $dist | Out-Null

# --- Compile ---------------------------------------------------------------
$sources = Get-ChildItem $root -Recurse -Filter *.java |
    Where-Object { $_.FullName -notmatch '\\(build|dist|out)\\' } |
    ForEach-Object { $_.FullName }
Write-Host "==> Compiling $($sources.Count) sources"
& $javac -encoding UTF-8 -cp (Join-Path $root "lib\jorbis-0.0.17.jar") -d $classes $sources
if ($LASTEXITCODE -ne 0) { throw "javac failed" }

# --- Application jar (main jar only; jorbis ships as its own jar) ----------
Write-Host "==> Creating RETROES.jar"
& $jar --create --file (Join-Path $stage "RETROES.jar") --main-class Main -C $classes .
if ($LASTEXITCODE -ne 0) { throw "jar failed" }
Copy-Item (Join-Path $root "lib\jorbis-0.0.17.jar") $stage

# --- Stage runtime assets (resolved via AppPaths at install dir) -----------
Write-Host "==> Staging assets"
$assetDirs = @("fonts", "homepage\assets", "games\flapocalypse\assets", "games\whatTheSnake\assets", "sounds")
foreach ($rel in $assetDirs) {
    Copy-Item (Join-Path $root $rel) (Join-Path $stage $rel) -Recurse -Force
}

# --- Trimmed runtime image --------------------------------------------------
Write-Host "==> jlink runtime (java.base, java.desktop, java.logging)"
& $jlink --add-modules java.base,java.desktop,java.logging `
    --strip-debug --no-header-files --no-man-pages --output $jlinkImage
if ($LASTEXITCODE -ne 0) { throw "jlink failed" }

# --- Installer --------------------------------------------------------------
Write-Host "==> jpackage (WiX exe installer)"
& $jpackage `
    --type exe `
    --name "RETROES" `
    --app-version $version `
    --vendor "zZOKofficial" `
    --description "RETROES - retro browser-style games collection" `
    --input $stage `
    --main-jar "RETROES.jar" `
    --main-class "Main" `
    --runtime-image $jlinkImage `
    --dest $dist `
    --license-file (Join-Path $root "LICENSE") `
    --win-dir-chooser `
    --win-menu `
    --win-menu-group "RETROES" `
    --win-shortcut `
    --win-per-user-install
if ($LASTEXITCODE -ne 0) { throw "jpackage failed" }

$installer = Get-ChildItem $dist -Filter *.exe | Select-Object -First 1
if (-not $installer) { throw "No installer produced in $dist" }
Write-Host "==> Installer: $($installer.FullName) ($([math]::Round($installer.Length / 1MB, 1)) MB)"
