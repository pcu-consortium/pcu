Spécification Technique Détaillée : modules connecteurs PCU
===========================================================

pcu-index
---------

### Conception

Le module *pcu-index* définit le connecteur aux indexes.
Il se compose de :
* un module d'API *pcu-index-api* définissant l'interface des interactions avec les indexes
* un module de code commun *pcu-index-core* contenant le code d'instanciation du connecteur aux indexes

Par défaut l'implémentation d'un connecteur aux indexes Elasticsearch est fournie et utilisée (sources/connectors/index/elasticsearch). Elle se base sur la librarie Jest.

### Configuration

La configuration commune pour l'utilisation d'un connecteur est :
* **className** : nom de la classe d'implémentation du connecteur
  * valeur pour l'implémentation elasticsearch: *org.pcu.connectors.index.elasticsearch.PcuESIndex*
* **configuration** : json de configuration spécifique à l'implémentation du connecteur utilisée
  * valeur pour l'implémentation elasticsearch: **uri** : URL du serveur Elasticsearch

Exemple de configuration spécifique à Elasticsearch
```json
{
    "uri":"http://localhost:9200"
}
```

### Guide de développement

TODO guide de dév

pcu-storage
-----------

### Conception

Le module *pcu-storage* définit le connecteur au stockage.
Il se compose de :
* un module d'API *pcu-storage-api* définissant l'interface des interactions avec le stockage
* un module de code commun *pcu-storage-core* contenant le code d'instanciation du connecteur au stockage

Par défaut l'implémentation d'un connecteur aux stockage VFS2 est fournie et utilisée (sources/connectors/storage/vfs2). Elle se base sur la librarie VFS2. Elle ne supporte à ce jour que du Filesystem. La librairie VFS2 étant compatible HDFS, une évolution du connecteur est possible.

### Configuration

La configuration commune pour l'utilisation d'un connecteur est :
* **className** : nom de la classe d'implémentation du connecteur
  * valeur pour l'implémentation VFS2: *org.pcu.connectors.storage.vfs2.PcuVfs2Storage*
* **configuration** : json de configuration spécifique à l'implémentation du connecteur utilisée
  * valeur pour l'implémentation VFS2: **path** : URI du répértoire de stockage

Exemple de configuration spécifique à VFS2
```json
{
    "path":"../data/storage"
}
```

### Guide de développement

TODO guide de dév