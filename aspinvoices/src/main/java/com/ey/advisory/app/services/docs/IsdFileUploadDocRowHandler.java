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
public class IsdFileUploadDocRowHandler<K> implements RowHandler {
	private Object[] headerData = null;

	public IsdFileUploadDocRowHandler() {
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

	public IsdFileUploadDocRowHandler(DataBlockKeyBuilder<K> keyBuilder) {
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
		arr[3] = CommonUtility.capital(arr[3]);
		arr[6] = CommonUtility.capital(arr[6]);
		arr[7] = CommonUtility.capital(arr[7]);
		
		arr[0] = CommonUtility.singleQuoteCheck(arr[0]);
		arr[1] = CommonUtility.singleQuoteCheck(arr[1]);
		arr[2] = CommonUtility.singleQuoteCheck(arr[2]);
		arr[3] = CommonUtility.singleQuoteCheck(arr[3]);
		arr[4] = CommonUtility.singleQuoteCheck(arr[4]);
		arr[5] = CommonUtility.singleQuoteCheck(arr[5]);
		arr[6] = CommonUtility.singleQuoteCheck(arr[6]);
		arr[7] = CommonUtility.singleQuoteCheck(arr[7]);
		
		String key = keyBuilder.buildComprehenceDataBlockKey(arr, layout);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Dockey in OutwardFileTransDocRowHandler " + key);
		}
		if (!isRowEmpty) {
			documentMap.computeIfAbsent(key, k -> new ArrayList<>()).add(arr);
		}

		return true;
	}

	public Map<String, List<Object[]>> getDocumentMap() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("documentMap in OutwardFileTransDocRowHandler"
					+ documentMap.toString());
		}
		return documentMap;
	}

}
