package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Gstr1HsnFileUploadDocRowHandler<K> implements RowHandler {
	private Object[] headerData = null;

	public Gstr1HsnFileUploadDocRowHandler() {
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

	public Gstr1HsnFileUploadDocRowHandler(DataBlockKeyBuilder<K> keyBuilder) {
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
		arr[0] = CommonUtility.singleQuoteCheck(arr[0]);
		arr[1] = CommonUtility.singleQuoteCheck(arr[1]);
		arr[2] = CommonUtility.singleQuoteCheck(arr[2]);
		arr[3] = CommonUtility.singleQuoteCheck(arr[3]);
		arr[4] = CommonUtility.singleQuoteCheck(arr[4]);
		arr[5] = CommonUtility.singleQuoteCheck(arr[5]);
		arr[6] = CommonUtility.singleQuoteCheck(arr[6]);
		arr[7] = CommonUtility.singleQuoteCheck(arr[7]);
		arr[8] = CommonUtility.singleQuoteCheck(arr[8]);
		arr[9] = CommonUtility.singleQuoteCheck(arr[9]);
		arr[10] = CommonUtility.singleQuoteCheck(arr[10]);
		arr[11] = CommonUtility.singleQuoteCheck(arr[11]);
		arr[12] = CommonUtility.singleQuoteCheck(arr[12]);
		arr[13] = CommonUtility.singleQuoteCheck(arr[13]);
		arr[14] = CommonUtility.singleQuoteCheck(arr[14]);

		arr[1] = CommonUtility.capital(arr[1]);
		arr[2] = CommonUtility.capital(arr[2]);
		arr[3] = CommonUtility.capital(arr[3]);
		arr[4] = CommonUtility.capital(arr[4]);
		arr[6] = CommonUtility.capital(arr[6]);
		

		if (arr[8] == null) {
			arr[8] = "0.00";
		}
		arr[8] = getRate(arr[8]);
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

	private static Object getRate(Object obj){
		try{
			if(obj == null)
				return obj;
			
			if (obj.toString().length() <= 4) {
				obj = new BigDecimal(obj.toString()).setScale(2, RoundingMode.HALF_UP).toString();
			}
		} catch(Exception e){
			LOGGER.debug("Invalid Rate {}",obj);
			}
		
		return obj;
	}
}
