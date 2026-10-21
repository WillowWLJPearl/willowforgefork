@echo off
setlocal
cd /d "%~dp0"
if not exist "userdata" mkdir "userdata"
"%~dp0runtime\bin\java.exe" -Xmx4096m -Dfile.encoding=UTF-8 -Dforge.assetsDir=forge-gui/ -Dneo.dataDir=userdata -cp "app\*;lib\*" forge.neo.NeoMain ui
echo.
echo Exit code: %errorlevel%
pause
