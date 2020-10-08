package br.com.depasser.content.web.controller;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	
	private static Configuration instance = null;
	
	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		
		return instance;
	}
	
	private Logger logger = LoggerFactory.getLogger(Configuration.class);
	protected Properties properties = new Properties();
	
	protected Configuration () {
		reload();
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public void reload() {
		try {
			logger.debug("Loading configurations...");
			properties.load(getClass().getResourceAsStream("configuration.properties"));
		} catch (IOException ioe) {
			logger.error("Error while reading configuration files.", ioe);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Configuration loaded:");
			for (Object o : properties.keySet()) {
				logger.debug("\t" + o + " = " + properties.get(o));
			}
		}
	}

}