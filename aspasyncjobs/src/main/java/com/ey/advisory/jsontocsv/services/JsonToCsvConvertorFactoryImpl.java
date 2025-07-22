package com.ey.advisory.jsontocsv.services;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("JsonToCsvConvertorFactoryImpl")
public class JsonToCsvConvertorFactoryImpl
		implements JsonToCsvConvertorFactory {

	private Map<String, String> convertorMap = new HashMap<>();

	@PostConstruct
	public void initProperties() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("About to load the Convertors Mapping from JSON");
		}
		try (InputStream stream = this.getClass().getClassLoader()
				.getResourceAsStream("converters.json");
				InputStreamReader reader = new InputStreamReader(stream)) {
			JsonParser parser = new JsonParser();
			JsonElement root = parser.parse(reader);
			JsonObject obj = root.getAsJsonObject();
			Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
			entries.forEach(entry -> {
				String key = entry.getKey();
				JsonElement ele = entry.getValue();
				String value = ele.getAsString();
				convertorMap.put(key, value);
			});
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Loaded the Convertors Mapping "
						+ "from JSON successfully!!");
			}
		} catch (Exception ex) {
			String msg = "Unexpected error occured while "
					+ "loading the Convertors mapping from Json";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public JsonToCsvConverter getConvertor(String fileType) {
		String beanName = convertorMap.get(fileType);
		return StaticContextHolder.getBean(beanName, JsonToCsvConverter.class);
	}

}
