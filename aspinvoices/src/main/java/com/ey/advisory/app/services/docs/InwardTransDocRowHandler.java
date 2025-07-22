package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * This class is responsible for Handling Inward Trans Docs
 * 
 * @author Mohana.Dasari
 *
 * @param <K>
 */
public class InwardTransDocRowHandler<K> implements RowHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InwardTransDocRowHandler.class);
	
	private Object[] headerData = null;
	public InwardTransDocRowHandler() {
	}

	public Object[] getHeaderData() {
		return headerData;
	}

	@Override
	public void handleHeaderRow(int rowNo, Object[] row, TabularDataLayout layout) {
		 // clone the shared array passed by the framework.		
		Object[] arr = row.clone();
		headerData = arr;
	}


	/**
	 * The map to be populated with the the data from the file.
	 */
	private Map<String, List<Object[]>> documentMap = new LinkedHashMap<>();

	private DataBlockKeyBuilder<K> keyBuilder;

	public InwardTransDocRowHandler(DataBlockKeyBuilder<K> keyBuilder) {
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
		removeSpecialChars(arr);
		String key = (String) keyBuilder.buildDataBlockKey(arr, layout);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Dockey while traversing from Excel is {} ", key);
		}
		if (!isRowEmpty) {
		    documentMap.computeIfAbsent(key, k -> new ArrayList<>()).add(arr);
		}
		return true;
    }


	public Map<String, List<Object[]>> getDocumentMap() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("documentMap in InwardTransDocRowHandler"
					+ documentMap.toString());
		}
		return documentMap;
	}
	private void removeSpecialChars(Object[] obj) {
		
		obj[20] = CommonUtility.singleQuoteCheck(obj[20]); // ReturnPeriod
		obj[24] = CommonUtility.capital(obj[24]); // Doc no
		obj[24] = CommonUtility.singleQuoteCheck(obj[24]); // Doc no
		obj[24] = CommonUtility.exponentialAndZeroCheck(obj[24]);// Doc No
		obj[26] = CommonUtility.singleQuoteCheck(obj[26]); // Org doc no
		obj[26] = CommonUtility.exponentialAndZeroCheck(obj[26]);// org Doc No
		obj[40] = CommonUtility.singleQuoteCheck(obj[40]); // Pos
		obj[41] = CommonUtility.singleQuoteCheck(obj[41]); // state applying cess
		obj[47] = CommonUtility.singleQuoteCheck(obj[47]); // Hsn
		obj[48] = CommonUtility.singleQuoteCheck(obj[48]); // item code
		obj[113] = CommonUtility.exponentialAndZeroCheck(obj[113]);// Ey bill number
	}
}


