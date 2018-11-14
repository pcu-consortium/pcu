#!/bin/bash

java -Dlogging.config="../config/pcu-agent-filesystem/logback.xml" \
    -jar ../lib/pcu-collectors-agent-filesystem.jar ../config/pcu-agent-filesystem/agent-filesystem.json
