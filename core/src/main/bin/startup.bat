@echo off

echo.
echo \#\#
echo \#\# Copyright 2004-2005 Auster Solutions do Brasil
echo \#\#
echo \#\#  --- Minerva Report Manager v1.0.0 ---
echo \#\#
echo.

if "%ANT_HOME%"=="" goto noAntHome
if not exist "%ANT_HOME%\lib\ant.jar" goto noAntHome

if "%JAVA_HOME%"=="" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome


set MINERVA_HOME=%cd%\..


if ""%1""==""--help"" goto printHelp
if ""%1""==""-help"" goto printHelp
if ""%1""==""-h"" goto printHelp
if ""%1""==""-?"" goto printHelp
if ""%1""==""/?"" goto printHelp
if ""%1""=="""" goto printUsage


# Starting Minerva
echo.
echo Starting Minerva Server
echo.

%JAVA_HOME%/bin/java -cp %ANT_HOME%/lib/ant-launcher.jar -Dant.home=%ANT_HOME% -Dbasedir=%MINERVA_HOME% -Dconfiguration.file=%CONFIGURATION_FILE% org.apache.tools.ant.launch.Launcher -file %MINERVA_HOME%/bin/minerva-run.xml

echo.
echo [ Finished ]
echo.
goto end



:noAntHome
echo ANT_HOME is set incorrectly or Ant could not be located. Please set ANT_HOME.
goto end

:noJavaHome
echo JAVA_HOME is set incorrectly or java could not be located. Please set JAVA_HOME.
goto end

:printHelp
%JAVA_HOME%/bin/java -cp %ANT_HOME%/lib/ant-launcher.jar -Dant.home=%ANT_HOME% -Dbasedir=%MINERVA_HOME% org.apache.tools.ant.launch.Launcher -file %MINERVA_HOME%/bin/minerva-run.xml server.help
goto end

:printUsage
echo.
echo Usage: startup.bat <configuration-file>
echo.
  
:end
