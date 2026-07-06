@echo off
setlocal ENABLEDELAYEDEXPANSION

:: --- CONFIGURATION ---
:: Path to your Phoebus product folder
set "PH_HOME=C:\Users\Starblend\Documents\phoebus-5.0.5\phoebus-product\target"
:: Path to this widget JAR
set "WIDGET_JAR=%~dp0target\sparkline-1.0-SNAPSHOT.jar"
set "WIDGET_JAR2=C:\Users\Starblend\Documents\NetBeansProjects\widget\target\widget-0.13.0-SNAPSHOT.jar"
set "WIDGET_JAR3=C:\Users\Starblend\Documents\NetBeansProjects\map-widget\target\Map-Widget-0.13.0-SNAPSHOT.jar

:: 1. Locate the main Phoebus product JAR
FOR /F "tokens=* USEBACKQ" %%F IN (`dir /B /S "%PH_HOME%\product-*.jar"`) DO (SET "PH_JAR=%%F")

if "%PH_JAR%"=="" (
    echo [ERROR] Could not find Phoebus product JAR in %PH_HOME%
    pause
    exit /b
)

echo [INFO] Launching Phoebus with Custom Widget Suite...

:: 2. Launch using the Launcher class and explicit classpath
java -Dfile.encoding=UTF-8 ^
     -cp "%PH_JAR%;%WIDGET_JAR%;%WIDGET_JAR2%;%WIDGET_JAR3%" ^
     org.phoebus.product.Launcher %*
pause
