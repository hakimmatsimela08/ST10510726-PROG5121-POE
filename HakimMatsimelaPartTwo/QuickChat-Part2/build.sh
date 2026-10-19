#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes dist
java -m jdk.compiler/com.sun.tools.javac.Main --release 11 -encoding UTF-8 -d build/classes src/hakimmokadimatsimelapartone/*.java
java -m jdk.jartool/sun.tools.jar.Main --create --file dist/QuickChat-Part2.jar --main-class hakimmokadimatsimelapartone.HakimMokadiMatsimelaPartOne -C build/classes .
java -m jdk.jartool/sun.tools.jar.Main --create --file dist/QuickChat-Part2-tests.jar --main-class hakimmokadimatsimelapartone.PartTwoTests -C build/classes .
java -m jdk.jartool/sun.tools.jar.Main --create --file dist/QuickChat-Part2-sources.jar -C src .
java -jar dist/QuickChat-Part2-tests.jar
