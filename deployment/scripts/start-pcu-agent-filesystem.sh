#!/bin/bash

java -Dlogging.config="../config/logback.xml"  -jar ../pcu-collectors-agent-jar-with-dependencies.jar ../config/agent.json
