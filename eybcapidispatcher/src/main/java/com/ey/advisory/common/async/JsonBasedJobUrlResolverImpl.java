package com.ey.advisory.common.async;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.Message;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.annotation.PostConstruct;

/**
 * This class extracts the value of the key named 'type' in the message JSON.
 * Depending on the 'type' value, the Spring Bean to be used as the
 * TaskProcessor instance is returned. These values are hard coded in this class
 * for now, but later can be moved out to DB and loaded using the ConfigManager
 * class.
 * 
 * If 'type' key doesn't exist OR if the 'type' value represents an unknown
 * message type, then a null value is returned as the TaskProcessor. The caller
 * of the getMessageTaskProcessor() needs to handle this null condition and take
 * necessary actions.
 * 
 * @author Sai.Pakanati
 *
 */

@Component("JsonBasedJobUrlResolverImpl")
public class JsonBasedJobUrlResolverImpl implements JobUrlResolver {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(JsonBasedJobUrlResolverImpl.class);

	private Map<String, String> taskurlsMap = new HashMap<>();

	private Properties properties = new Properties();

	@PostConstruct
	public void initProperties() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("About to load the Task urls Mapping from JSON");
		}
		try (InputStream stream = this.getClass().getClassLoader()
				.getResourceAsStream("taskurls.json");
				InputStreamReader reader = new InputStreamReader(stream);
				InputStream is = this.getClass().getClassLoader()
						.getResourceAsStream("jobbundles.properties")) {
			JsonParser parser = new JsonParser();
			JsonElement root = parser.parse(reader);
			JsonObject obj = root.getAsJsonObject();
			Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
			entries.forEach(entry -> {
				String key = entry.getKey();
				JsonElement ele = entry.getValue();
				String value = ele.getAsString();
				taskurlsMap.put(key, value);
			});
			this.properties = new Properties();
			properties.load(is);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Loaded the Task urls Mapping "
						+ "from JSON successfully!!");
			}
		} catch (FileNotFoundException ex) {
			String msg = String.format(
					"The Job Bundles " + "file '%s' not found!!",
					"jobbundles.properties");
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		} catch (Exception ex) {
			String msg = "Unexpected error occured while "
					+ "loading the Task urls mapping from Json";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public String resolveJobUrl(Message message) {
		String taskType = message.getMessageType();
		String beanName = taskurlsMap.get(taskType);

		if (beanName == null) {
			String msg = String.format(
					"The task URL '%s' is  " + "not mapped to a TaskProcessor",
					taskType);
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		try {
			String resource = properties.getProperty(beanName);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"About to Load the Jobbundle %s mapped to Job Category %s",
						resource, message.getMessageType());
				LOGGER.debug(msg);
			}
			return resource;
		} catch (Exception ex) {
			String msg = String.format(
					"Unable to get the url "
							+ "TaskProcessor bean with the name: '%s'",
					beanName);
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}

}
