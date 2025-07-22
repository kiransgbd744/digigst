package com.ey.advisory.app.docs.errutils.sv.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.app.docs.errutils.sv.SvErrDataArrayHandler;
import com.ey.advisory.app.docs.errutils.sv.SvErrDataArrayKeyBuilder;

/**
 * 
 * @author Mohana.Dasari
 *
 * @param <K>
 */
public class OutwardDocSvErrDataArrayHandler<K>
		implements SvErrDataArrayHandler {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardDocSvErrDataArrayHandler.class);

	private SvErrDataArrayKeyBuilder<K> keyBuilder;
	
	public OutwardDocSvErrDataArrayHandler(
			SvErrDataArrayKeyBuilder<K> keyBuilder) {
		this.keyBuilder = keyBuilder;
	}

	/**
	 * The map to be populated with the the data from the request object data.
	 */
	private Map<String, List<Object[]>> documentMap = new LinkedHashMap<>();
	
	@Override
	public void handleSvErrDataArray(Object[] arr) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entered handleSvErrDataArray");
		}
		
		String key = (String) keyBuilder.buildDataArrayKey(arr);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("key in OutwardDocSvErrDataArrayHandler " + key);
		}
		documentMap.computeIfAbsent(key, k -> new ArrayList<>()).add(arr);
	}
	
	public Map<String, List<Object[]>> getDocumentMap() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("documentMap in OutwardDocSvErrDataArrayHandler"
					+ documentMap.toString());
		}
		return documentMap;
	}

}
