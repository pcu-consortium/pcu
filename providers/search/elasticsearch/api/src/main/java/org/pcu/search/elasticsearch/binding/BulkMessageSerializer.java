package org.pcu.search.elasticsearch.binding;

import java.io.IOException;

import org.pcu.search.elasticsearch.api.BulkAction;
import org.pcu.search.elasticsearch.api.BulkMessage;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Must be used by ObjectMapper WITHOUT indenting nor pretty-printing,
 * because in ES _bulk, each object's metadata or data must be in a single line
 * http://www.baeldung.com/jackson-custom-serialization
 * @author mardut
 *
 */
public class BulkMessageSerializer extends StdSerializer<BulkMessage> {
   
   public BulkMessageSerializer() {
      super(BulkMessage.class);
   }

   private static final long serialVersionUID = 5852768663142760830L;

   @Override
   public void serialize(BulkMessage bulkMessage, JsonGenerator gen, SerializerProvider provider) throws IOException {
      for (BulkAction bulkAction : bulkMessage.getActions()) {
         gen.writeObject(bulkAction.getKindToAction());
         gen.writeRaw('\n'); // else } { which triggers ES error : "type":"json_e_o_f_exception","reason":"Unexpected end-of-input: expected close marker for Object (start marker at [Source: org.elasticsearch.transport.netty4.ByteBufStreamInput@3e483f5a; line: 1, column: 1])\n at [Source: org.elasticsearch.transport.netty4.ByteBufStreamInput@3e483f5a; line: 1, column: 3]"
         gen.writeObject(bulkAction.getDoc());
         gen.writeRaw('\n'); // same
      }
   }

}
