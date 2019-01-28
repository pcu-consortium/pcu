Architecture Globale de la plateforme PCU
=========================================

Architecture globale
--------------------

Le schéma représente l'architecture globale de ce qui est fait à ce jour pour la plateforme PCU dans ce repository.

![Architecture globale](./include/pcu_architecture_y2.png)

Modules :
* PCU platform server
* PCU agent de collecte de fichiers filesystem
* PCU agent de collecte web http
* PCU agent de collecte base de données compatibles JDBC

Logiciels :
* Index : Elasticsearch
* Message broker : Apache Kafka

Architecture cible
------------------

TODO recupérer les schémas de la dernière présentation