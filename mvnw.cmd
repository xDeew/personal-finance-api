@echo off
setlocal

set "MVNW_DIR=%~dp0"
set "MVN_CMD="

if defined MAVEN_HOME if exist "%MAVEN_HOME%\bin\mvn.cmd" set "MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd"
if not defined MVN_CMD if exist "%ProgramFiles%\apache-maven-3.9.10\bin\mvn.cmd" set "MVN_CMD=%ProgramFiles%\apache-maven-3.9.10\bin\mvn.cmd"
if not defined MVN_CMD for %%I in (mvn.cmd) do set "MVN_CMD=%%~$PATH:I"

if not defined MVN_CMD (
  echo Maven executable not found. Install Maven or set MAVEN_HOME.
  exit /b 1
)

call "%MVN_CMD%" %*
exit /b %ERRORLEVEL%
