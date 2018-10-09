#!/bin/bash


java -jar ../pcu-platform-server.jar --spring.application.json='{"pcu.indexer.type":"ES6","pcu.indexer.file":"../config/pcuindexer.json"}'