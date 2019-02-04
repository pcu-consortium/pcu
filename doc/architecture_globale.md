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

Déploiement
-----------

Le schéma représente l'infrastructure de production prévue pour la plateforme ouverte aux partenaires.

![Architecture production](./include/pcu_architecture_prod.png)

Architecture cible
------------------

![Architecture cible](./include/pcu_architecture_target.png)


