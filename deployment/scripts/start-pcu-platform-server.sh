#!/bin/bash

java -Dloader.path=lib,config,../config -jar ../pcu-platform-server.jar --spring.application.json='{"pcu.index.type":"ES6","pcu.index.file":"../config/pcuindex.json"}'
