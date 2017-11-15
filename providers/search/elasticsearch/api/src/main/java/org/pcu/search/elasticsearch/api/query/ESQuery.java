package org.pcu.search.elasticsearch.api.query;

import org.pcu.search.elasticsearch.api.query.clause.bool;
import org.pcu.search.elasticsearch.api.query.clause.function_score;
import org.pcu.search.elasticsearch.api.query.clause.multi_match;
import org.pcu.search.elasticsearch.api.query.clause.prefix;
import org.pcu.search.elasticsearch.api.query.clause.query_string;
import org.pcu.search.elasticsearch.api.query.clause.range;
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
   @JsonSubTypes.Type(value = range.class),
   @JsonSubTypes.Type(value = prefix.class),
   @JsonSubTypes.Type(value = query_string.class),
   @JsonSubTypes.Type(value = script.class),
   @JsonSubTypes.Type(value = function_score.class)
})
/* else :
Caused by: com.fasterxml.jackson.databind.exc.InvalidTypeIdException: Could not resolve type id 'range' into a subtype of [simple type, class org.pcu.search.elasticsearch.api.query.ESQuery]: known type ids = [ESQuery]
 at [Source: org.apache.cxf.transport.http.AbstractHTTPDestination$1@644d5f9d; line: 1, column: 11] (through reference chain: org.pcu.search.elasticsearch.api.query.ESQueryMessage["query"])
   at com.fasterxml.jackson.databind.exc.InvalidTypeIdException.from(InvalidTypeIdException.java:42)
   at com.fasterxml.jackson.databind.DeserializationContext.unknownTypeIdException(DeserializationContext.java:1477)
   at com.fasterxml.jackson.databind.DeserializationContext.handleUnknownTypeId(DeserializationContext.java:1170)
   at com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase._handleUnknownTypeId(TypeDeserializerBase.java:282)
   at com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase._findDeserializer(TypeDeserializerBase.java:156)
 */
public interface ESQuery {

}
