#!/bin/bash

export SPARK_MASTER_HOST=`hostname`

. "/spark/sbin/spark-config.sh"

. "/spark/bin/load-spark-env.sh"

mkdir -p $SPARK_MASTER_LOG

export SPARK_HOME=/spark

ln -sf /dev/stdout $SPARK_MASTER_LOG/spark-master.out

cd /spark/bin && /spark/sbin/../bin/spark-class org.apache.spark.deploy.master.Master \
    --ip $SPARK_MASTER_HOST --port $SPARK_MASTER_PORT --webui-port $SPARK_MASTER_WEBUI_PORT >> $SPARK_MASTER_LOG/spark-master.out &

if [ "${1}" = "jupyter" ]; then
    echo "starting jupyter"
    jupyter notebook --port=8888 --no-browser --ip=0.0.0.0 --allow-root --NotebookApp.allow_password_change=True --NotebookApp.password='sha1:1012ea89ce58:c90ec31a32b534ce682cd2cbed19cb2234976348' --ContentsManager.root_dir='/root/'
fi