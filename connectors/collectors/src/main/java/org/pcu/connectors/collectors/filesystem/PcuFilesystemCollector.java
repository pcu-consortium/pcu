package org.pcu.connectors.collectors.filesystem;

import org.springframework.stereotype.Component;

import com.norconex.collector.fs.FilesystemCollector;
import com.norconex.collector.fs.FilesystemCollectorConfig;
import com.norconex.collector.fs.crawler.FilesystemCrawlerConfig;

@Component
public class PcuFilesystemCollector {

	public void execute() {
		FilesystemCollectorConfig collectorConfig = new FilesystemCollectorConfig();
		collectorConfig.setId("MyFilesystemCollector");
		collectorConfig.setLogsDir("/tmp/logs/");
		//
		FilesystemCrawlerConfig crawlerConfig = new FilesystemCrawlerConfig();
		crawlerConfig.setId("MyFilesystemCrawler");
		crawlerConfig.setStartPaths(
		        new String[]{"/home/gafou/projets/innovation/pcu/sources/git-pcu-consortium/pcu/connectors"});
		crawlerConfig.setCrawlDataStoreFactory(new PcuDatastoreFactory());
		//
		collectorConfig.setCrawlerConfigs(crawlerConfig);
		 //
		FilesystemCollector collector = new FilesystemCollector(collectorConfig);
		collector.start(true);
	}
	
}
