package json.bean;

	
	public class EventLayer extends NudgeEvent {
		
		public EventLayer(String name, long responseTime, String date, long count) {
			super(name, responseTime, date, count, "layer");
		}


}
