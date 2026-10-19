@echo off
setlocal
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
if not exist dist mkdir dist
java -m jdk.compiler/com.sun.tools.javac.Main --release 11 -encoding UTF-8 -d build/classes src/hakimmokadimatsimelapartone/*.java
if errorlevel 1 exit /b 1
java -m jdk.jartool/sun.tools.jar.Main --create --file dist/QuickChat-Part2.jar --main-class hakimmokadimatsimelapartone.HakimMokadiMatsimelaPartOne -C build/classes .
if errorlevel 1 exit /b 1
java -m jdk.jartool/sun.tools.jar.Main --create --file dist/QuickChat-Part2-tests.jar --main-class hakimmokadimatsimelapartone.PartTwoTests -C build/classes .
if errorlevel 1 exit /b 1
java -m jdk.jartool/sun.tools.jar.Main --create --file dist/QuickChat-Part2-sources.jar -C src .
if errorlevel 1 exit /b 1
java -jar dist/QuickChat-Part2-tests.jar
