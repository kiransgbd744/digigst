package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class VendorTransDocRowHandler<K> implements RowHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardTransDocRowHandler.class);

	private Object[] headerData = null;

	public VendorTransDocRowHandler() {
	}

	public Object[] getHeaderData() {
		return headerData;
	}

	@Override
	public void handleHeaderRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		// clone the shared array passed by the framework.
		Object[] arr = row.clone();
		headerData = arr;
	}

	/**
	 * The map to be populated with the the data from the file.
	 */
	private Map<String, List<Object[]>> documentMap = new LinkedHashMap<>();

	private DataBlockKeyBuilder<K> keyBuilder;

	public VendorTransDocRowHandler(DataBlockKeyBuilder<K> keyBuilder) {
		this.keyBuilder = keyBuilder;
	}

	@Override
	public boolean handleRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Row No in OutwardTransDocRowHandler " + rowNo);
			LOGGER.debug("Row in OutwardTransDocRowHandler " + row);
		}
		Object[] arr = row.clone();
		boolean isRowEmpty = true;
		for (Object object : arr) {
			if (object != null) {
				isRowEmpty = false;
			}
		}
		/*
		 * if(!isRowEmpty){ fileObjList.add(arr); }
		 */
		String key = (String) keyBuilder.buildDataBlockKey(arr, layout);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("key in OutwardTransDocRowHandler " + key);
		}
		if (!isRowEmpty) {
			documentMap.computeIfAbsent(key, k -> new ArrayList<>()).add(arr);
		}

		return true;
	}

	public Map<String, List<Object[]>> getDocumentMap() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("documentMap in OutwardTransDocRowHandler"
					+ documentMap.toString());
		}
		return documentMap;
	}

}
