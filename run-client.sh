#!/bin/bash
cd "$(dirname "$0")"
# Kopiraj resurse ako nisu već tu
[ ! -d "out/fxml" ] && mkdir -p out/fxml && cp src/resources/fxml/ui.fxml out/fxml/ 2>/dev/null
java --module-path lib/javafx-sdk-21.0.2/lib --add-modules javafx.controls,javafx.fxml -cp "lib/*:out" client.Client
