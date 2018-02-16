package org.pcu.features.connector.crawler;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.pcu.providers.search.api.PcuDocument;

public class SamplePersonJDBCCrawlBatch extends JDBCCrawlBatchBase {

   /** for REST ser only */
   public SamplePersonJDBCCrawlBatch() {
      
   }

   public SamplePersonJDBCCrawlBatch(Crawler2 crawler) {
      super(crawler);
   }

   protected PcuDocument mapToPcuDocument(ResultSet rs, long rowNum) throws SQLException {
      PcuDocument pcuDoc = new PcuDocument();
      pcuDoc.setType("person"); // TODO index
      pcuDoc.setByPath("id", rs.getLong("id"));
      pcuDoc.setByPath("path", rs.getLong("id") + ""); // id plays the role of path
      // TODO OPT content if any (ex. ECM case) : download and upload, parse / extract and enrich pcuDoc with meta (including content store path)
      return pcuDoc; // new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")
   }
   
}
