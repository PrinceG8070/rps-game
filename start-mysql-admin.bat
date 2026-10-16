@echo off
echo Requesting Administrator privileges to start MySQL80 service...
powershell -Command "Start-Process cmd -ArgumentList '/c net start MySQL80 && pause' -Verb RunAs"
echo Done. You can close this window.
