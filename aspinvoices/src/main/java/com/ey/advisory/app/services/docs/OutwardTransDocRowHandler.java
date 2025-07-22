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
 * This class is responsible for Handling Outward Trans Docs
 * 
 * @author Mohana.Dasari
 *
 * @param <K>
 */
public class OutwardTransDocRowHandler<K> implements RowHandler {

    private static final Logger LOGGER = LoggerFactory
	    .getLogger(OutwardTransDocRowHandler.class);

    private Object[] headerData = null;

    public OutwardTransDocRowHandler() {
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

    public OutwardTransDocRowHandler(DataBlockKeyBuilder<K> keyBuilder) {
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
	removeSpecialChars(arr);
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
    private void removeSpecialChars(Object[] obj) {
    	obj[21] = CommonUtility.singleQuoteCheck(obj[21]); // return period
    	obj[25] = CommonUtility.capital(obj[25]);// doc no
		obj[25] = CommonUtility.singleQuoteCheck(obj[25]);// doc no
		obj[25] = CommonUtility.exponentialAndZeroCheck(obj[25]);// Doc No
		obj[28] = CommonUtility.singleQuoteCheck(obj[28]);// org doc no
		obj[28] = CommonUtility.exponentialAndZeroCheck(obj[28]);// org Doc No
		obj[42] = CommonUtility.singleQuoteCheck(obj[42]); //BillToStateValidationRule
		obj[43] = CommonUtility.singleQuoteCheck(obj[43]); // ShipToStateValidationRule
		obj[44] = CommonUtility.singleQuoteCheck(obj[44]); // PosValidationRule
		obj[47] = CommonUtility.exponentialAndZeroCheck(obj[47]);// Shipping bill no
		obj[51] = CommonUtility.singleQuoteCheck(obj[51]); //HSNSACValidationRule
		obj[107] = CommonUtility.exponentialAndZeroCheck(obj[107]);// EWayBillNumberValidationRule
		
		obj[92] = CommonUtility.singleQuoteCheck(obj[92]); // UserDefinedField1
		obj[93] = CommonUtility.singleQuoteCheck(obj[93]); // UserDefinedField2
		obj[94] = CommonUtility.singleQuoteCheck(obj[94]); // UserDefinedField3
		obj[95] = CommonUtility.singleQuoteCheck(obj[95]); // UserDefinedField4
		obj[96] = CommonUtility.singleQuoteCheck(obj[96]); // UserDefinedField5
		obj[97] = CommonUtility.singleQuoteCheck(obj[97]); // UserDefinedField6
		obj[98] = CommonUtility.singleQuoteCheck(obj[98]); // UserDefinedField7
		obj[99] = CommonUtility.singleQuoteCheck(obj[99]); // UserDefinedField8
		obj[100] = CommonUtility.singleQuoteCheck(obj[100]); // UserDefinedField9
		obj[101] = CommonUtility.singleQuoteCheck(obj[101]); // UserDefinedField10
		obj[102] = CommonUtility.singleQuoteCheck(obj[102]); // UserDefinedField11
		obj[103] = CommonUtility.singleQuoteCheck(obj[103]); // UserDefinedField12
		obj[104] = CommonUtility.singleQuoteCheck(obj[104]); // UserDefinedField13
		obj[105] = CommonUtility.singleQuoteCheck(obj[105]); // UserDefinedField14
		obj[106] = CommonUtility.singleQuoteCheck(obj[106]); // UserDefinedField15
		    }
}
