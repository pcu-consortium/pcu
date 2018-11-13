#!/bin/bash

java --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED -Dloader.path=lib,config,../config/pcu-platform -jar ../lib/pcu-platform-server.jar