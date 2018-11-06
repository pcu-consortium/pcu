#!/bin/bash

java -Dloader.path=lib,config,../config \
    -jar ../pcu-platform-server.jar \
    --spring.application.json='{"pcu.index.type":"ES6","pcu.index.file":"../config/pcuindex.json", "kafka.topic.ingest":"PcuPlatformServerIT-Ingest", "kafka.topic.addDocument":"PcuPlatformServerIT-Ingest","spring.kafka.consumer.group-id":"pcu-platform","spring.kafka.bootstrap-servers":"localhost:9092","spring.kafka.producer.value-serializer":"org.springframework.kafka.support.serializer.JsonSerializer","spring.kafka.consumer.value-deserializer":"org.springframework.kafka.support.serializer.JsonDeserializer","spring.kafka.consumer.properties.spring.json.trusted.packages":"org.pcu.platform"}'
