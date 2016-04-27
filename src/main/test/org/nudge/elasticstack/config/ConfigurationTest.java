package org.nudge.elasticstack.config;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTest {

	@Test
	public void getProperty_environnement_variable() {
		//given 
		String key = "export_type";
		String expectedValue = "test"; 
		System.setProperty("nes." + key, expectedValue);
		Configuration conf = new Configuration();
		
		//when 
		String result = conf.getProperty(key, null); 
		
		//then 
        Assert.assertEquals(expectedValue, result);
	}
	
	
	@Test
	public void getProperty_props_conf(){
		//given
		Configuration conf = new Configuration();
		Properties props = conf.getProperties();
		String key = "adress";
		String expectedValue = "paris";
		props.setProperty(key, expectedValue);
		
		// when
		String result = conf.getProperty(key, null);
		
		// then
		Assert.assertEquals(expectedValue, result);
	}

	
	@Test(expected = IllegalStateException.class)
	public void checkNoNull() {
		//given 
		Configuration conf = new Configuration();
		String key = "a.key";
		//when 
		conf.checkNotNull(key);
	}
	
	
}
