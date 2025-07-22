package com.ey.advisory.app.glrecon.dump;

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
public class GLReconDumpUploadDocRowHandler<K> implements RowHandler {
	private Object[] headerData = null;

	public GLReconDumpUploadDocRowHandler() {
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

	public GLReconDumpUploadDocRowHandler(DataBlockKeyBuilder<K> keyBuilder) {
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
		arr[11] = CommonUtility.capital(arr[11]);
		arr[12] = CommonUtility.capital(arr[12]);
		
		arr[0] = CommonUtility.singleQuoteCheck(arr[0]);
		arr[11] = CommonUtility.singleQuoteCheck(arr[11]);
		arr[12] = CommonUtility.singleQuoteCheck(arr[12]);
		
		String key = keyBuilder.buildComprehenceDataBlockKey(arr, layout);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Dockey in GlReconDumpDocRowHandler " + key);
		}
		if (!isRowEmpty) {
			documentMap.computeIfAbsent(key, k -> new ArrayList<>()).add(arr);
		}

		return true;
	}

	public Map<String, List<Object[]>> getDocumentMap() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("documentMap in GlReconDumpDocRowHandler"
					+ documentMap.toString());
		}
		return documentMap;
	}

}
