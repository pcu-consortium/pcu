Spécification Technique Détaillée : modules agents de collecte PCU
==================================================================

pcu-agent
---------

Un code commun java est mis à disposition pour créer des agents.
Il se compose de :
* un module d'API *pcu-collectors-api* définissant l'interface du service de collecte
* un module de code commun *pcu-collectors-core* contenant le code en charge de l'execution de la collecte
* un module parent pour l'écriture d'un agent *pcu-collectors-agent* contenant la définition du build d'un agent

La définition des service sur base sur le mécanisme JEE Service Provider Interface (voir la documentation oracle sur le [SPI](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html))

TODO guide de dév

### Configuration

La configuration de tout agent utilisant *pcu-agent* est un fichier au format JSON passé en argument de l'execution. Il doit au moins contenir les données obligatoires suivantes :
* **collectorId** : l'indentifiant de l'agent de collecte déployé
* **datasourceId** : l'identifiant de la source de données
* **pcuPlatformUrl** : URL du serveur *pcu-platform-server*

### Guide de développement

agent-database-jdbc
-------------------

### Conception

Le module agent-database-jdbc est un agent de collecte pour les bases de données relationnelles accessibles par un driver JDBC. Il se base sur la librairie [commons-dbcp2](http://commons.apache.org/proper/commons-dbcp/).
A partir d'un requête de type SELECT chaque ligne d'une table est transformée en objet json et envoyé à l'ingestion du serveur *pcu-platform-server* à l'aide du module *pcu-client*. L'identifiant de la donnée est calculé à partir de l'identifiant de la datasource et de son index en MD5/HEX.

### Configuration

Un driver JDBC doit être ajouté au classpath de l'agent de collecte agent-database-jdbc.

Le JSON définit pour *pcu-agent* doit en plus contenir les données obligatoires suivantes :
* **pcuType** : type des données
* **pcuIndex** : indexe cible des données
* **url**: URI d'accès à la base de données
* **username** : login d'accès à la base de données
* **password** : mot de passe de la base de données
* **driver** : classe d'implémentation du driver JDBC
* **query** : requête de type SELECT utilisée pour la récupération des données

Exemple de fichier de configuration JSON :
```json
{
    "collectorId" : "pcu-release-collector-database",
    "datasourceId" : "pcu-release-datasource-database",
    "pcuPlatformUrl" : "http://localhost:8080",
    "pcuType" : "document",
    "pcuIndex" : "documents",
    "url":"jdbc:mysql://127.0.0.1:3306/testdatabase",
    "username":"root",
    "password":"password",
    "driver":"com.mysql.cj.jdbc.Driver",
    "query" :"select Nom_Commercial as title, Type_Commercial as description, PHARMA_PRODUCTS.* from PHARMA_PRODUCTS"
 }
```

agent-filesystem-norconex
-------------------------

### Conception

Le module agent-filesystem-norconex est un agent de collecte pour les fichiers sur des disques dur locaux ou réseaux. Il se base sur la librairie [Norconex Filesystem Collector](https://www.norconex.com/collectors/collector-filesystem/).
L'agent de collecte reste au plus près de la librairie Nocornex. Les implémentations spécifiques à l'envoi des données au serveur *pcu-platform-server* sont :
* **PcuFilesystemCommitter** : implémentation de l'interface Norconex *ICommitter* dont la fonction est d'envoyer les métadonnées au serveur *pcu-platform-server* à l'aide du module *pcu-client*
* **PcuFilesystemSendFilePostProcessor** : implémentation de l'interface Norconex *IFileDocumentProcessor* dont la fonction est d'envoyer les fichier au serveur *pcu-platform-server* à l'aide du module *pcu-client*
* **PcuFilesystemNorconexCollector** : contient l'instanciation (à partir de la configuration de l'agent de collecte) et l'exécution de *FilesystemCollector*

### Configuration

Le JSON définit pour *pcu-agent* doit en plus contenir les données obligatoires suivantes :
* **norconexfilesystem.config.commitFiles** : indique si l'on souhaite ou non envoyer les fichier (en plus des métadonnées) au serveur *pcu-platform-server*
* **norconexfilesystem.config.xml** : chemin vers le fichier de configuration XML au format Norconex (cf [documentation de configuration Norconex](https://www.norconex.com/collectors/collector-filesystem/configuration))
* **norconexfilesystem.config.variables** : chemin vers le fichier de configuration contant les variables au format Norconex (cf [documentation de configuration Norconex](https://www.norconex.com/collectors/collector-filesystem/configuration))

Le fichier **norconexfilesystem.config.xml** doit contenir la définition et l'utilisation des classes *PcuFilesystemCommitter* et *PcuFilesystemSendFilePostProcessor*.

Exemple de fichier de configuration JSON :
```json
{
    "collectorId" : "pcu-release-collector-filesystem",
    "datasourceId" : "pcu-release-datasource-filesystem",
    "pcuPlatformUrl" : "http://localhost:8080",
    "norconexfilesystem.config.commitFiles" : "true",
    "norconexfilesystem.config.xml" : "../config/pcu-agent-filesystem/norconex-filesystem-config.xml",
    "norconexfilesystem.config.variables" : "../config/pcu-agent-filesystem/norconex-filesystem-config.variables"
}
```
Exemple de fichier de configuration Norconex XML :
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xml>
<fscollector id="${collectorid}">

	<logsDir>${workdir}/logs</logsDir>
	<progressDir>${workdir}/progress</progressDir>

	<crawlerDefaults>
		<workDir>${workdir}</workDir>
		<startPaths>
			<path>${path}</path>
		</startPaths>

		<numThreads>2</numThreads>

		<keepDownloads>false</keepDownloads>

		<crawlDataStoreFactory class="${dataStoreFactory}" />

		<committer class="${committer}">
			<directory>${workdir}/crawledFiles</directory>
		</committer>

	</crawlerDefaults>


	<crawlers>
		<crawler id="${crawlerid}">
			<maxDocuments>${maxDocuments}</maxDocuments>
			<postImportProcessors>
				<processor
					class="org.pcu.connectors.collectors.filesystem.internal.PcuFilesystemSendFilePostProcessor"></processor>
			</postImportProcessors>
		</crawler>
	</crawlers>

</fscollector>
```
Exemple de fichier de configuration Norconex variables :
```properties
collectorid=PcuFilesystemCollector
crawlerid=PcuFilesystemCrawler
workdir=../workdirFs
path=../dist/file
dataStoreFactory=com.norconex.collector.core.data.store.impl.mvstore.MVStoreCrawlDataStoreFactory
committer=org.pcu.connectors.collectors.filesystem.internal.PcuFilesystemCommitter
maxDocuments=100
```

agent-http-norconex
-------------------

### Conception

Le module agent-http-norconex est un agent de collecte pour les sources Web. Il se base sur la librairie [Norconex HTTP Collector](https://www.norconex.com/collectors/collector-http/).
L'agent de collecte reste au plus près de la librairie Nocornex. Les implémentations spécifiques à l'envoi des données au serveur *pcu-platform-server* sont :
* **PcuHttpCommitter** : implémentation de l'interface Norconex *ICommitter* dont la fonction est d'envoyer les métadonnées au serveur *pcu-platform-server* à l'aide du module *pcu-client*
* **PcuHttpNorconexCollector** : contient l'instanciation (à partir de la configuration de l'agent de collecte) et l'exécution de *HttpCollector*

### Configuration

Le JSON définit pour *pcu-agent* doit en plus contenir les données obligatoires suivantes :
* **norconexhttp.config.xml** : chemin vers le fichier de configuration XML au format Norconex (cf [documentation de configuration Norconex](https://www.norconex.com/collectors/collector-http/configuration))
* **norconexhttp.config.variables** : chemin vers le fichier de configuration contant les variables au format Norconex (cf [documentation de configuration Norconex](https://www.norconex.com/collectors/collector-http/configuration))

Le fichier **norconexhttp.config.xml** doit contenir la définition et l'utilisation des ckasses *PcuFilesystemCommitter*.

Exemple de fichier de configuration JSON :
```json
{
    "collectorId" : "pcu-release-collector-http",
    "datasourceId" : "pcu-release-datasource-http",
    "pcuPlatformUrl" : "http://localhost:8080",
    "norconexhttp.config.xml" : "../config/pcu-agent-http/norconex-http-config.xml",
    "norconexhttp.config.variables" : "../config/pcu-agent-http/norconex-http-config.variables"
}
```
Exemple de fichier de configuration Norconex XML :
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xml>
<httpcollector id="${collectorid}">

	<logsDir>${workdir}/logs</logsDir>
	<progressDir>${workdir}/progress</progressDir>

	<crawlerDefaults>
		<workDir>${workdir}</workDir>

		<startURLs stayOnDomain="${stayOnDomain}" stayOnPort="${stayOnPort}" stayOnProtocol="${stayOnProtocol}">
			<url>${url}</url>
		</startURLs>
		<numThreads>2</numThreads>

		<keepDownloads>false</keepDownloads>

		<maxDepth>${maxDepth}</maxDepth>
		<sitemapResolverFactory ignore="${ignoreSitemapResolverFactory}" />
		<delay default="${delayDefault}" />

		<crawlDataStoreFactory class="${dataStoreFactory}" />

		<committer class="${committer}">
			<directory>${workdir}/crawledFiles</directory>
		</committer>
	</crawlerDefaults>


	<crawlers>
		<crawler id="${crawlerid}">
			<maxDocuments>${maxDocuments}</maxDocuments>
			<referenceFilters>
                <filter class="com.norconex.collector.core.filter.impl.RegexReferenceFilter" onMatch="include">
      				.*/Solutions.*
  				</filter>
            </referenceFilters>
		</crawler>
	</crawlers>

</httpcollector>
```
Exemple de fichier de configuration Norconex variables :
```properties
collectorid=PcuHttpCollector
crawlerid=PcuHttpCrawler
workdir=../workdirHttp
stayOnDomain=true
stayOnPort=true
stayOnProtocol=true
url=http://www.open-source-guide.com/Solutions
maxDepth=5
ignoreSitemapResolverFactory=true
delayDefault=5000
dataStoreFactory=com.norconex.collector.core.data.store.impl.mvstore.MVStoreCrawlDataStoreFactory
committer=org.pcu.connectors.collectors.http.internal.PcuHttpCommitter
maxDocuments=100
```
