#!/bin/bash

echo "Stop Elasticsearch 6.4.2 container : elasticsearch-pcu"
docker stop elasticsearch-pcu
echo "Remove Elasticsearch 6.4.2 container : elasticsearch-pcu"
docker rm elasticsearch-pcu