#!/bin/bash

java -Dlogging.config="../config/pcu-agent-database/logback.xml" \
    -jar ../lib/pcu-collectors-agent-database.jar ../config/pcu-agent-database/agent-database.json
