package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Gstr3bFileUploadDocRowHandler<K> implements RowHandler {
	private Object[] headerData = null;

	public Gstr3bFileUploadDocRowHandler() {
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

	public Gstr3bFileUploadDocRowHandler(DataBlockKeyBuilder<K> keyBuilder) {
		this.keyBuilder = keyBuilder;
	}

	@Override
	public boolean handleRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		Object[] arr = row.clone();
		boolean isRowEmpty = true;
		for (Object object : arr) {
			if (object != null) {
				isRowEmpty = false;
			}
		}
		arr[0] = CommonUtility.capital(arr[0]);
		arr[1] = CommonUtility.capital(arr[1]);
		arr[2] = CommonUtility.capital(arr[2]);
		arr[8] = CommonUtility.capital(arr[8]);
		String key = keyBuilder.buildComprehenceDataBlockKey(arr, layout);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Dockey in Gstr3bSummaryTransDocRowHandler " + key);
		}
		if (!isRowEmpty) {
			documentMap.computeIfAbsent(key, k -> new ArrayList<>()).add(arr);
		}

		return true;
	}

	public Map<String, List<Object[]>> getDocumentMap() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("documentMap in Gstr3bSummaryTransDocRowHandler"
					+ documentMap.toString());
		}
		return documentMap;
	}

}
