package org.nudge.elasticstack.json.bean;

/**
 * @author : Sarah Bourgeois
 * @author : Frederic Massart
 */

public class EventLayer extends NudgeEvent {

	public EventLayer(String name, long responseTime, String date, long count) {
		super(name, responseTime, date, count, "layer");
	}

}
