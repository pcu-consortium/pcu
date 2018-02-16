package org.pcu.features.connector.crawler;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.pcu.providers.search.api.PcuDocument;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zaxxer.hikari.HikariDataSource;

/**
 * A crawl batch that polls a JDBC database for a paginated list of lines
 * and converts them to PCU documents using a conversion function to be
 * implemented.
 * @author mardut
 *
 */
public abstract class JDBCCrawlBatchBase extends CrawlBatch {

   /** used to ser/deser JDBC and build jdbcTemplate */
   private LinkedHashMap<String,Object> jdbcProperties;
   /** ponly cache */
   @JsonIgnore
   private JdbcTemplate jdbcTemplate;
   //private String query = "select * from mytable where id > ? order by id asc limit 1000";
   private String querySelect = "select * from mytable";
   protected String idFieldName = "id";
   private long batchSize = 1000;
   private Object lastPolledId = null;

   /** for REST ser only */
   public JDBCCrawlBatchBase() {
      
   }
   
   public JDBCCrawlBatchBase(Crawler2 crawler) {
      super(crawler);
   }
   
   /*
   public List<CrawlBatch> doNextPageItemsAndReturnNextCrawlBatches() {
      //final List<PcuDocument> additionalPcuDocs = new ArrayList<PcuDocument>();
      //SqlRowSet b = jdbcTemplate.queryForRowSet(query, new Object[] { lastPolledId });
      List<PcuDocument> pcuDocs = buildNextJdbcPcuDocuments();
      
      if (pcuDocs != null) { // or hasDoc ?
         // TODO get links and add to queue
         crawler.registerForIndexing(pcuDocs);
         return Arrays.asList(new CrawlBatch[] { this });
      } // else ex. folder // NOO else batch completed
      return null;
   }
   */

   @Override
   public PcuDocumentsMapResult buildNextPagePcuDocumentsAndCrawlBatches() {
      //SqlRowSet b = jdbcTemplate.queryForRowSet(query, new Object[] { lastPolledId });
      List<PcuDocument> pcuDocs;
      String queryOrderByLimit = " order by " + idFieldName + " asc limit " + batchSize;
      if (lastPolledId == null) {
         pcuDocs = getJdbcTemplate().query(querySelect + queryOrderByLimit,
               this::mapToPcuDocument);
      } else {
         pcuDocs = getJdbcTemplate().query(querySelect + " where id > ? " + queryOrderByLimit,
               new Object[] { lastPolledId }, this::mapToPcuDocument);
      }
      if (pcuDocs.isEmpty()) {
         return null; // this batch is done
      }
      lastPolledId = (Long) pcuDocs.get(pcuDocs.size() - 1).getByPath("id");
      return new PcuDocumentsMapResult(pcuDocs, Arrays.asList(new CrawlBatch[] { this }));
   }
   
   protected abstract PcuDocument mapToPcuDocument(ResultSet rs, long rowNum) throws SQLException;

   public List<LinkedHashMap<String, Object>> nextPageItems1() {
      //SqlRowSet b = jdbcTemplate.queryForRowSet(query, new Object[] { lastPolledId });
      List<LinkedHashMap<String, Object>> rowMaps = getJdbcTemplate().query("TODO", new Object[] { lastPolledId }, (rs, rowNum) -> {
         LinkedHashMap<String, Object> rowMap = new LinkedHashMap<String, Object>();
         rowMap.put("id", rs.getLong("id"));
         return rowMap; // new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")
      });
      if (rowMaps.isEmpty()) {
         return null; // no more
      }
      lastPolledId = (Long) rowMaps.get(rowMaps.size() - 1).get("id");
      return rowMaps;
   }
   

   /** if no cachedJdbcTemplate yet, lazy inits it from jdbcProperties */
   public JdbcTemplate getJdbcTemplate() {
      if (this.jdbcTemplate == null) {
         HikariDataSource ds = new HikariDataSource();
         try {
            BeanUtils.populate(ds, jdbcProperties);
         } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("error setting up ds from props", e);
         }
         this.jdbcTemplate = new JdbcTemplate(ds);
      }
      return this.jdbcTemplate;
   }
   /**
    * only use in tests, otherwise rather setJdbcProperties()
    * @param jdbcTemplate
    */
   public void setCachedJdbcTemplate(JdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
   }
   /**
    * returns value of last deser 
    * @return
    */
   public LinkedHashMap<String,Object> getJdbcProperties() {
      return jdbcProperties;
   }
   /**
    * Allows YAML or JSON deser i.e. if no jdbcTemplate yet.
    * Can also be used alternatively to setJdbcTemplate().
    * @param jdbcProperties
    */
   public void setJdbcProperties(LinkedHashMap<String,Object> jdbcProperties) {
      this.jdbcProperties = jdbcProperties;
   }
   public String getQuerySelect() {
      return querySelect;
   }
   public void setQuerySelect(String querySelect) {
      this.querySelect = querySelect;
   }
   public String getIdFieldName() {
      return idFieldName;
   }
   public void setIdFieldName(String idFieldName) {
      this.idFieldName = idFieldName;
   }
   public long getBatchSize() {
      return batchSize;
   }
   public void setBatchSize(long batchSize) {
      this.batchSize = batchSize;
   }
   public Object getLastPolledId() {
      return lastPolledId;
   }
   /** for recovery on failure */
   public void setLastPolledId(Object lastPolledId) {
      this.lastPolledId = lastPolledId;
   }

}
