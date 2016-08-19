package mapping;

/**
 * @author : Sarah Bourgeois 
 * @author : Frederic Massart
 * 
 * Description : properties of the update mapping for type : sql, transaction and Mbean
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
		
		@JsonProperty("transaction_name")
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

			// ******* Field *******
			public class Fields {

				private FieldAttribute name;
				private FieldAttribute raw;

				@JsonProperty("transaction_name")
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

				// ******* Field Attribute *******
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
