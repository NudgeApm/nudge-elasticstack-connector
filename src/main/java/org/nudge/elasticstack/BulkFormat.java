package org.nudge.elasticstack;


/**
 * Convert data for the Bulk API 
 * @author Sarah Bourgeois
 */

import com.fasterxml.jackson.annotation.JsonProperty;

public class BulkFormat {

		@JsonProperty("index")
		private Index indexElement;

		public BulkFormat() {
			this.indexElement = new Index();
		}

		public Index getIndexElement() {
			return indexElement;
		}

		public void setIndexElement(Index indexElement) {
			this.indexElement = indexElement;
		}
		

		// -- Inner class Index
		
		public class Index {
			
			private String index;
			private String type;
			private String id;
			
			@JsonProperty("_index")
			public String getIndex() {
				return index;
			}
			public void setIndex(String index) {
				this.index = index;
			}
			
			@JsonProperty("_type")
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			
			@JsonProperty("_id")
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}		
			
		}	
	}