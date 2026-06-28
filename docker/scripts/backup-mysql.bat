@echo off
REM MySQL 备份脚本 (Windows)
REM 用法: backup-mysql.bat [输出目录]
setlocal

set BACKUP_DIR=%~1
if "%BACKUP_DIR%"=="" set BACKUP_DIR=.\backups

if "%MYSQL_HOST%"=="" set MYSQL_HOST=localhost
if "%MYSQL_PORT%"=="" set MYSQL_PORT=3306
if "%MYSQL_DATABASE%"=="" set MYSQL_DATABASE=jiangou
if "%MYSQL_USER%"=="" set MYSQL_USER=root

for /f "tokens=1-3 delims=/:. " %%a in ("%date% %time%") do set TS=%%a%%b%%c
set TS=%TS: =0%
set OUTPUT=%BACKUP_DIR%\jiangou_%TS%.sql

if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

echo Backing up %MYSQL_DATABASE%@%MYSQL_HOST%:%MYSQL_PORT% -^> %OUTPUT%

mysqldump -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% --single-transaction --routines --triggers %MYSQL_DATABASE% > "%OUTPUT%"

echo Done: %OUTPUT%
endlocal
