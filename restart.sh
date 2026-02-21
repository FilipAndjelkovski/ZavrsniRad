#!/bin/bash
cd "$(dirname "$0")"

# Pronađi Java kompajler
JAVAC_PATH=$(find /Library/Java/JavaVirtualMachines -name "javac" 2>/dev/null | head -1)
if [ -z "$JAVAC_PATH" ]; then
    JAVAC_PATH="javac"
fi

echo "=== Zaustavljanje servera ==="
lsof -ti:5000 | xargs kill -9 2>/dev/null
lsof -ti:8080 | xargs kill -9 2>/dev/null
sleep 1

echo "=== Kompajliranje servera ==="
"$JAVAC_PATH" -cp "lib/*" -d out src/main/java/common/*.java src/main/java/server/*.java

if [ $? -ne 0 ]; then
    echo "❌ Greška pri kompajliranju servera!"
    exit 1
fi

echo "=== Kompajliranje klijenta ==="
"$JAVAC_PATH" -cp "lib/javafx-sdk-21.0.2/lib/*:lib/*" -d out src/main/java/common/*.java src/main/java/client/*.java

if [ $? -ne 0 ]; then
    echo "❌ Greška pri kompajliranju klijenta!"
    exit 1
fi

echo "=== Kopiranje resursa ==="
[ ! -d "out/fxml" ] && mkdir -p out/fxml && cp src/resources/fxml/ui.fxml out/fxml/ 2>/dev/null

echo "✅ Kompajliranje završeno!"
echo ""
echo "Sada pokreni server sa: ./run-server.sh"
echo "I klijenta sa: ./run-client.sh"
