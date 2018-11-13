#!/bin/bash

java -Dlogging.config="../config/pcu-agent-http/logback.xml" \
    -jar ../lib/pcu-collectors-agent-http.jar ../config/pcu-agent-http/agent-http.json
