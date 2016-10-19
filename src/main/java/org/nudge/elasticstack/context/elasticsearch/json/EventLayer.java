package org.nudge.elasticstack.context.elasticsearch.json;

/**
 * @author : Sarah Bourgeois
 * @author : Frederic Massart
 */

public class EventLayer extends NudgeEvent {

	public EventLayer(String name, long responseTime, String date, long count, String transactionId) {
		super(name, responseTime, date, count, "layer", transactionId);
	}

}
