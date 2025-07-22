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

/**
 * This class is responsible for Handling Outward Trans Docs
 * 
 * @author Mahesh.Golla
 *
 * @param <K>
 */
@Slf4j
public class Itc04DocRowHandler<K> implements RowHandler {

	private Object[] headerData = null;

	public Itc04DocRowHandler() {
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

	public Itc04DocRowHandler(DataBlockKeyBuilder<K> keyBuilder) {
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
		removeSpecialChars(arr);
		String key = (String) keyBuilder.buildItc04DataBlockKey(arr, layout);
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

	private void removeSpecialChars(Object[] obj) {
		obj[0] = CommonUtility.capital(obj[0]); // table number
		obj[4] = CommonUtility.capital(obj[4]); // supplier Gstin
		obj[5] = CommonUtility.exponentialAndZeroCheck(obj[5]);// Delivery Challan Number
		obj[7] = CommonUtility.exponentialAndZeroCheck(obj[7]);// JW Delivery Challan Number
		obj[10] = CommonUtility.exponentialAndZeroCheck(obj[10]);// Inv No
		obj[24] = CommonUtility.exponentialAndZeroCheck(obj[24]);// Quantity
		obj[26] = CommonUtility.exponentialAndZeroCheck(obj[26]);// lossesQuantity
		obj[27] = CommonUtility.exponentialAndZeroCheck(obj[27]);// ItemAccessableAmtValidationRule
		obj[29] = CommonUtility.exponentialAndZeroCheck(obj[29]);// IgstAmtValidationRule
		obj[31] = CommonUtility.exponentialAndZeroCheck(obj[31]);// CgstAmtValidationRule
		obj[33] = CommonUtility.exponentialAndZeroCheck(obj[33]);// SgstAmtValidationRule
		obj[35] = CommonUtility.exponentialAndZeroCheck(obj[35]);// CessAmtValidationRule
		obj[37] = CommonUtility.exponentialAndZeroCheck(obj[37]);// CessSpecificAdvAmtValidationRule
		obj[39] = CommonUtility.exponentialAndZeroCheck(obj[39]);// StateCessAdvAmtValidationRule
		obj[41] = CommonUtility.exponentialAndZeroCheck(obj[41]);// StateSpecificAmtValidationRule
		obj[42] = CommonUtility.exponentialAndZeroCheck(obj[42]);// TotalValueAmtValidationRule
		obj[50] = CommonUtility.exponentialAndZeroCheck(obj[50]); // profit center1
		obj[51] = CommonUtility.exponentialAndZeroCheck(obj[51]); // profit center2
		obj[52] = CommonUtility.exponentialAndZeroCheck(obj[52]); //AccountingVochar number

	}
}
