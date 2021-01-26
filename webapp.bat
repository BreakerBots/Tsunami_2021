@echo off

set app = "src\webapp\breakerboard-win32-ia32\breakerboard.exe"

IF EXIST %app% (
	Start "" "%app%" -i silent
)
IF NOT EXIST %app% (
	powershell -command "Expand-Archive -Force '%~dp0src\webapp\breakerboard-win32-ia32.zip' '%~dp0src\webapp'"
	Call webapp.bat
)

