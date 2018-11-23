## Run

### Master

```bash
docker run \
    --name spark-master \
    -h spark-master \
    -p 8083:8083 \
    -p 7077:7077 \
   	-e SPARK_MASTER_PORT=7077 \
   	-e SPARK_MASTER_WEBUI_PORT=8083 \
   	-e ENABLE_INIT_DAEMON=false \
    -d pcu/spark-master:2.4.0-hadoop2.7
```

### Worker

```bash
docker run \
    --name spark-worker-1 \
    --link spark-master:spark-master \
    -p 8084:8084 \
    -p 7078:7078 \
    -e ENABLE_INIT_DAEMON=false \
	-e SPARK_MASTER_WEBUI_PORT=8083 \
    -e SPARK_WORKER_PORT=7078 \
	-e SPARK_WORKER_WEBUI_PORT=8084 \
    -e SPARK_MASTER=spark://spark-master:7077 \
    -d pcu/spark-worker:2.4.0-hadoop2.7
```

### Jupyter

#### Configuration

password : jupyter

#### Example of notebook

```python
import findspark
findspark.init()
import pyspark
import random
sc = pyspark.SparkContext(appName="Pi",master="spark://spark-master:7077")
num_samples = 100000000
def inside(p):     
  x, y = random.random(), random.random()
  return x*x + y*y < 1
count = sc.parallelize(range(0, num_samples)).filter(inside).count()
pi = 4 * count / num_samples
print(pi)
sc.stop()
```