package org.pcu.search.elasticsearch.api.query.clause;

import org.pcu.search.elasticsearch.api.query.ESQuery;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * not analyzed
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-script-query.html
 * OBSOLETE https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-scripting-using.html
 * @author mdutoo
 *
 */
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class script extends ESScript implements ESQuery { // ESScriptQuery
   
}
