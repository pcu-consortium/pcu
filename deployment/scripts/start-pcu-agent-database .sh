#!/bin/bash

java -Dlogging.config="../config/logback.xml" \
    -jar ../pcu-collectors-agent-database.jar ../config/agent-database.json
