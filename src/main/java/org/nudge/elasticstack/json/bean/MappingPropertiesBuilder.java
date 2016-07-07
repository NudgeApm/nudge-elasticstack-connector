package org.nudge.elasticstack.json.bean;

/**
 * @author : Sarah Bourgeois 
 * @author : Frederic Massart
 * 
 * Description : builder for the new mapping
 */

import org.nudge.elasticstack.json.bean.MappingProperties.Properties.Name;
import org.nudge.elasticstack.json.bean.MappingProperties.Properties.Name.Fields;
import org.nudge.elasticstack.json.bean.MappingProperties.Properties.Name.Fields.FieldAttribute;

public class MappingPropertiesBuilder {

	public static MappingProperties createMappingProperties() {
		MappingProperties mappingProperties = new MappingProperties();
		MappingProperties.Properties properties = mappingProperties.new Properties();
		mappingProperties.setPropertiesElement(properties);
		Name name = properties.new Name();
		properties.setName(name);
		Fields fields = name.new Fields();
		name.setFields(fields);
		FieldAttribute nameAttribute = fields.new FieldAttribute();
		fields.setName(nameAttribute);
		FieldAttribute raw = fields.new FieldAttribute();
		fields.setRaw(raw);
		return mappingProperties;
	}

	public static MappingProperties buildMappingProperties(String propertyType, String nameFieldType,
			String nameFieldIndex, String rawFieldType, String rawFieldIndex) {
		
		MappingProperties mappingProperties = MappingPropertiesBuilder.createMappingProperties();
		mappingProperties.getPropertiesElement().getName().setType(propertyType);
		mappingProperties.getPropertiesElement().getName().getFields().getName().setType(nameFieldType);
		mappingProperties.getPropertiesElement().getName().getFields().getName().setIndex(nameFieldIndex);
		mappingProperties.getPropertiesElement().getName().getFields().getRaw().setType(rawFieldType);
		mappingProperties.getPropertiesElement().getName().getFields().getRaw().setIndex(rawFieldIndex);
		
		return mappingProperties;	
	}
}
