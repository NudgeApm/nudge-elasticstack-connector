package org.nudge.elasticstack.json.bean;

/**
 * @author : Sarah Bourgeois 
 * @author : Frederic Massart
 * 
 * Description : properties of the new mapping which will be send to elasticsearch
 */

import com.fasterxml.jackson.annotation.JsonProperty;

public class MappingProperties {

	@JsonProperty("properties")
	private Properties propertiesElement;

	public MappingProperties() {
		this.propertiesElement = new Properties();
	}

	public Properties getPropertiesElement() {
		return propertiesElement;
	}

	public void setPropertiesElement(Properties propertiesElement) {
		this.propertiesElement = propertiesElement;
	}

	// ===========================
	// Inner class
	// ===========================

	// ***** Properties ******
	public class Properties {

		private Name name;

		public Name getName() {
			return name;
		}

		public void setName(Name name) {
			this.name = name;
		}

		public class Name {
			private String type;
			private Fields fields;

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

			public Fields getFields() {
				return fields;
			}

			public void setFields(Fields fields) {
				this.fields = fields;
			}

			// ******* Field Attribute *******
			public class Fields {

				private FieldAttribute name;
				private FieldAttribute raw;

				public FieldAttribute getName() {
					return name;
				}

				public void setName(FieldAttribute name) {
					this.name = name;
				}

				public FieldAttribute getRaw() {
					return raw;
				}

				public void setRaw(FieldAttribute raw) {
					this.raw = raw;
				}

				public class FieldAttribute {
					private String type;
					private String index;

					public String getType() {
						return type;
					}

					public void setType(String type) {
						this.type = type;
					}

					public String getIndex() {
						return index;
					}

					public void setIndex(String index) {
						this.index = index;
					}
				}
				public void setName(String code) {
					return;

				}
			}
		}
	}

}
