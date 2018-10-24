#!/bin/bash

java -Dlogging.config="../config/logback.xml"  -jar ../pcu-collectors-agent-filesystem.jar ../config/agent-filesystem.json
