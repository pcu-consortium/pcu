package org.pcu.search.elasticsearch.api.query;

import org.pcu.search.elasticsearch.api.query.clause.bool;
import org.pcu.search.elasticsearch.api.query.clause.multi_match;
import org.pcu.search.elasticsearch.api.query.clause.script;
import org.pcu.search.elasticsearch.api.query.clause.terms;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.swagger.annotations.ApiModel;

/**
 * Interface because no field is shared overall but some can be among concrete query classes
 * through extending another abstract class (ex. FunctionScoreFunctions).
 * @author mardut
 *
 */
@ApiModel(description = "query",
        subTypes={
            bool.class,
            multi_match.class,
            terms.class })
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
@JsonSubTypes({
   @JsonSubTypes.Type(value = bool.class),
   @JsonSubTypes.Type(value = multi_match.class),
   @JsonSubTypes.Type(value = terms.class),
   @JsonSubTypes.Type(value = script.class)
})
public interface ESQuery {

}
