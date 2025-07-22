package com.ey.advisory.common.async;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogLevelConfigurator {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(LogLevelConfigurator.class);
	
	//@PostConstruct
	public void init() {
		URL  url = this.getClass().getClassLoader()
				.getResource("log4j-async.xml");
		File file;
		try {
			file = new File(url.toURI());
			String log4jConfigPath = file.getAbsolutePath();
			log4jConfigPath = log4jConfigPath.replace('\\', '/');
			LOGGER.info("Path for configureAndWatch " + log4jConfigPath);
			if(url != null){}
				//DOMConfigurator.configureAndWatch(url.getPath());
		} catch (URISyntaxException e) {
			LOGGER.error("Error in Log4j.xml configureAndWatch feature"+e, e);
		}
	}
}
