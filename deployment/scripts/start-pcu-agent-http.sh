#!/bin/bash

java -Dlogging.config="../config/logback.xml"  -jar ../pcu-collectors-agent-http.jar ../config/agent-http.json
