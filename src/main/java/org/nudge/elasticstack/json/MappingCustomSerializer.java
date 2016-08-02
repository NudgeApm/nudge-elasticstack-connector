package org.nudge.elasticstack.json;

import java.io.IOException;

import org.nudge.elasticstack.json.bean.MappingProperties;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MappingCustomSerializer extends JsonSerializer<MappingProperties.Properties.Name.Fields.FieldAttribute> {
//	public class MappingCustomSerializer extends JsonSerializer<String> {

	@Override
	public void serialize(MappingProperties.Properties.Name.Fields.FieldAttribute value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		gen.writeStartObject();
	    gen.writeObjectField(value.getClass().getName(), value);
	    gen.writeEndObject();
	}

}
