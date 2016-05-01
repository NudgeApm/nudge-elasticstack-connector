package json.bean;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Frédéric Massart
 */
@Deprecated
public class TimeSerieDeserializer extends JsonDeserializer<TimeMeasure> {

	@Override
	public TimeMeasure deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		JsonNode dataNode = node.get("data");
		if (dataNode != null) {
			Iterator<JsonNode> iterator = dataNode.iterator();
			JsonNode next = iterator.next();
			System.out.printf("vale" + next);
		}
//			System.out.println("dta is " + data);
		return new TimeMeasure();
	}


}
