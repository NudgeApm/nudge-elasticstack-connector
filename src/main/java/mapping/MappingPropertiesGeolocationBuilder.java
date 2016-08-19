package mapping;

import mapping.MappingPropertiesGeoLocation.Properties.Name;

public class MappingPropertiesGeolocationBuilder {

	public static MappingPropertiesGeoLocation createMappingGeolocationProperties() {
		
		MappingPropertiesGeoLocation mpgl = new MappingPropertiesGeoLocation();
		MappingPropertiesGeoLocation.Properties properties = mpgl.new Properties();
		mpgl.setPropertiesElement(properties);
		Name name = properties.new Name();
		properties.setName(name);
		return mpgl;
	}
	
	
	public static MappingPropertiesGeoLocation buildGeolocationMappingProperties(String type, boolean geohash, boolean geohashPrefix, int geohashPrecision) {
		MappingPropertiesGeoLocation mpgl = MappingPropertiesGeolocationBuilder.createMappingGeolocationProperties();
		mpgl.getPropertiesElement().getName().setType(type);
		mpgl.getPropertiesElement().getName().setGeohash(geohash);
		mpgl.getPropertiesElement().getName().setGeohash_prefix(geohashPrefix);
		mpgl.getPropertiesElement().getName().setGeohash_precision(geohashPrecision);
		return mpgl;
	}
}
