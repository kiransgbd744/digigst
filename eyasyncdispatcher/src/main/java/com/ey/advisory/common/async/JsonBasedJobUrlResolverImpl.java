package com.ey.advisory.common.async;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.Message;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This class extracts the value of the key named 'type' in the message JSON.
 * Depending on the 'type' value, the value to be used as the JobBundle is
 * returned. These values are hard coded in this class for now, but later can be
 * moved out to DB and loaded using the ConfigManager class.
 * 
 * If 'type' key doesn't exist OR if the 'type' value represents an unknown
 * message type, then a null value is returned as the Job Bundle. The caller of
 * the resolveJobUrl() needs to handle this null condition and take necessary
 * actions.
 * 
 * @author Sai.Pakanati
 *
 */

@Component("JsonBasedJobUrlResolverImpl")
public class JsonBasedJobUrlResolverImpl implements JobUrlResolver {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(JsonBasedJobUrlResolverImpl.class);

	private Map<String, Queue<String>> taskurlsMap = new ConcurrentHashMap<>();

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
				String[] value = ele.getAsString().split(",");
				taskurlsMap.put(key, new LinkedList<>(Arrays.asList(value)));
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
	public Pair<String, String> resolveJobUrl(Message message) {
		String taskType = message.getMessageType();
		String bundleName = null;
		Queue<String> queue = taskurlsMap.get(taskType);
		synchronized (queue) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Before polling bundleName from queue-> {} for Jobid::{}",
						queue, message.getId());
			bundleName = queue.poll();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"After polling bundleName from queue-> {} for Jobid::{}",
						queue, message.getId());
			queue.offer(bundleName);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"After offering bundleName to queue-> {} for Jobid::{}",
						queue, message.getId());
		}
		try {
			String resource = properties.getProperty(bundleName);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"About to Load the Jobbundle %s mapped to Job Category %s",
						resource, message.getMessageType());
				LOGGER.debug(msg);
			}

			return new Pair<>(resource, bundleName);
		} catch (Exception ex) {
			String msg = String.format(
					"Unable to get the url "
							+ "TaskProcessor bean with the name: '%s'",
					bundleName);
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public String replaceTaskUrl(String jobName, String jobBundleNames) {
		if (!taskurlsMap.containsKey(jobName)) {
			String msg = String.format(
					"The Job '%s' is not mapped to any Job Bundle", jobName);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		String[] bundles = jobBundleNames.split(",");
		for (String jobBundleName : bundles) {
			String resource = properties.getProperty(jobBundleName);
			if (resource == null) {
				String msg = String.format(
						"The Job Bundle '%s' is not available in the properties",
						jobName);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
		}
		String prevJobBundles = taskurlsMap.get(jobName).toString();
		taskurlsMap.put(jobName, new LinkedList<>(Arrays.asList(bundles)));
		return prevJobBundles;

	}
}
