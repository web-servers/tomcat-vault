@echo off
rem -------------------------------------------------------------------------
rem Vault tool script for Windows
rem -------------------------------------------------------------------------
rem
rem A tool for management securing sensitive strings

@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT" setlocal

if "%OS%" == "Windows_NT" (
  set "DIRNAME=%~dp0%"
) else (
  set DIRNAME=.\
)

pushd "%DIRNAME%.."
set "VAULT_HOME=%CD%"
popd

rem Setup Tomcat specific properties
if "x%JAVA_HOME%" == "x" (
  set  JAVA=java
  echo JAVA_HOME is not set. Unexpected results may occur.
  echo Set JAVA_HOME to the directory of your local JDK to avoid this message.
) else (
  set "JAVA=%JAVA_HOME%\bin\java"
)

rem Find jboss-modules.jar, or we can't continue
set "VAULT_RUNJAR=%VAULT_HOME%\lib\tomcat-vault.jar"
if not exist "%VAULT_RUNJAR%" (
  echo Could not locate "%VAULT_RUNJAR%".
  echo Please check that you are in the bin directory when running this script.
  goto END
)

rem Set classpath with tomcat jars
if "x%VAULT_CLASSPATH%" == "x" (
  set "VAULT_CLASSPATH=%VAULT_RUNJAR%;%VAULT_HOME%\lib\tomcat-util.jar;%VAULT_HOME%\bin\tomcat-juli.jar"
)

rem Display our environment
set help=F
if "%*" == "-h" set help=T
if "%*" == "--help" set help=T
if "%help%" == "F" (
echo =========================================================================
echo.
echo   Tomcat Vault Tool
echo.
echo   VAULT_HOME: "%VAULT_HOME%"
echo.
echo   JAVA: "%JAVA%"
echo.
echo   JAVA_OPTS: "%JAVA_OPTS%"
echo.
echo =========================================================================
echo.
)

"%JAVA%" %JAVA_OPTS% ^
    -cp "%VAULT_CLASSPATH%" ^
     org.apache.tomcat.vault.VaultTool ^
     %*

:END
