@echo off
setlocal
cd /d "%~dp0"

rem User decks, settings and downloaded images stay in userdata after updates.
if not exist "userdata" mkdir "userdata"
if not exist "runtime\bin\javaw.exe" (
  echo Missing bundled Java runtime. Please extract the entire ZIP again.
  pause
  exit /b 1
)

rem Check releases before starting so no game files are replaced while playing.
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0Check-Update.ps1"

rem The game is run with a self-contained Java runtime. No Java install needed.
"%~dp0runtime\bin\javaw.exe" -Xmx4096m -Dfile.encoding=UTF-8 -Dforge.assetsDir=forge-gui -Dneo.dataDir=userdata -cp "app\*;lib\*" forge.neo.NeoMain ui
if errorlevel 1 (
  echo NeoForge exited with an error. Run Debug.cmd to see diagnostics.
  pause
)
