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

Pour développer un connecteur aux indexes pcu il faut créer un module maven contenant :

* un pom.xml suivant cet exemple :
```xml 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<artifactId>pcu-index</artifactId>
		<groupId>org.pcu-consortium.pcu</groupId>
		<version>0.1.0-SNAPSHOT</version>
	</parent>
	<artifactId>pcu-index-custom</artifactId>
	<packaging>jar</packaging>

	<name>PCU Index Custom</name>
	<description>PCU Index Custom</description>

	<dependencies>

        <!-- PCU : -->
		<dependency>
			<groupId>org.pcu-consortium.pcu</groupId>
			<artifactId>pcu-index-api</artifactId>
			<version>${project.version}</version>
		</dependency>
        <!-- Your custom dependencies : -->
    
        <!-- Logs : -->    
		<dependency>
			<groupId>commons-logging</groupId>
			<artifactId>commons-logging</artifactId>
		</dependency>

	</dependencies>

</project>
```

* une classe d'execution qui implémente l'interface `PcuIndex` :

```java
package acme.pcu.index.impl;

import org.pcu.connectors.index.PcuIndex;
import org.pcu.connectors.index.PcuIndexConfiguration;
import org.pcu.connectors.index.PcuIndexException;

import com.fasterxml.jackson.databind.JsonNode;

public class PcuCustomIndex implements PcuIndex {

	public PcuESIndex(PcuIndexConfiguration configuration) {
		// mandatory custom constructor called in PcuIndexFactory by Reflection
	}

	@Override
	public JsonNode getStatus() {
		// TODO implementation
	}

	@Override
	public boolean createDocument(JsonNode document, String index, String type, String id) throws PcuIndexException {
		// TODO implementation
	}

	@Override
	public boolean deleteDocument(String index, String type, String id) throws PcuIndexException {
		// TODO implementation
	}

	@Override
	public JsonNode getDocument(String index, String type, String id) throws PcuIndexException {
		// TODO implementation
	}

	@Override
	public JsonNode getDocuments(JsonNode searchQuery) throws PcuIndexException {
		// TODO implementation
	}

	@Override
	public boolean createIndex(String index) throws PcuIndexException {
		// TODO implementation
	}

	@Override
	public boolean deleteIndex(String index) throws PcuIndexException {
		// TODO implementation
	}

	@Override
	public void close() throws Exception {
		// TODO implementation
	}

}
```

Pour l'ajouter au module pcu-platform-server il faut le définir dans la configuration et l'ajouter le jar au classpath du serveur lors de son lancement.

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

Pour développer un connecteur au stockage pcu il faut créer un module maven contenant :

* un pom.xml suivant cet exemple :
```xml 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<artifactId>pcu-storage</artifactId>
		<groupId>org.pcu-consortium.pcu</groupId>
		<version>0.1.0-SNAPSHOT</version>
	</parent>
	<artifactId>pcu-storage-custom</artifactId>
	<packaging>jar</packaging>

	<name>PCU Storage Custom</name>
	<description>PCU Storage Custom</description>

	<dependencies>
		
        <!-- PCU : -->
        <dependency>
			<groupId>org.pcu-consortium.pcu</groupId>
			<artifactId>pcu-storage-api</artifactId>
			<version>${project.version}</version>
		</dependency>

        <!-- Your custom dependencies : -->
		
	</dependencies>

</project>
```

* une classe d'execution qui implémente l'interface `PcuStorage` :

```java
package acme.pcu.storage.impl;

import org.pcu.connectors.storage.PcuStorage;
import org.pcu.connectors.storage.PcuStorageConfiguration;
import org.pcu.connectors.storage.PcuStorageConfigurationException;
import org.pcu.connectors.storage.PcuStorageContainerNotFoundException;
import org.pcu.connectors.storage.PcuStorageException;
import org.pcu.connectors.storage.PcuStorageFileNotFoundException;

import com.fasterxml.jackson.databind.JsonNode;

public class PcuCustomStorage implements PcuStorage {

	public PcuCustomStorage(PcuStorageConfiguration configuration) {
		// mandatory custom constructor called in PcuStorageFactory by Reflection
	}

	@Override
	public JsonNode getStatus() {
		// TODO implementation
	}

	@Override
	public boolean createContainer(String containerName) throws PcuStorageException {
		// TODO implementation
	}

	@Override
	public boolean upload(InputStream content, String containerName, String id)
			throws PcuStorageContainerNotFoundException, PcuStorageException {
		// TODO implementation
	}

	@Override
	public boolean deleteContainer(String containerName) throws PcuStorageException {
		// TODO implementation
	}

	@Override
	public InputStream download(String containerName, String id)
			throws PcuStorageContainerNotFoundException, PcuStorageFileNotFoundException, PcuStorageException {
		// TODO implementation
	}

	@Override
	public boolean delete(String containerName, String id)
			throws PcuStorageContainerNotFoundException, PcuStorageException {
		// TODO implementation
	}

	@Override
	public void close() throws Exception {
        // TODO implementation
	}

}
```

Pour l'ajouter au module pcu-platform-server il faut le définir dans la configuration et l'ajouter le jar au classpath du serveur lors de son lancement.
