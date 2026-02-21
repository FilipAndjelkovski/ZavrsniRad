#!/bin/bash
cd "$(dirname "$0")"
java -cp "lib/*:out" server.Server
