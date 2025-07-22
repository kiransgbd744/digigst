package com.ey.advisory.app.services.strcutvalidation.inward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableMap;

/**
 * @author Siva.Nandam
 *
 */
public class HeaderInwardStructuralValidationUtil {

	private HeaderInwardStructuralValidationUtil() {
	}

	//Header fileds
	private static final Map<String, Integer> HEADER_FIELDS = 
			new ImmutableMap.Builder<String, Integer>()
			.put(GSTConstants.DIVISION, 1)
			.put(GSTConstants.PURCHASEORG, 1)
			.put(GSTConstants.USERACCESS1, 1)
			.put(GSTConstants.USERACCESS2, 1)
			.put(GSTConstants.USERACCESS3, 1)
			.put(GSTConstants.USERACCESS4, 1)
			.put(GSTConstants.USERACCESS5, 1)
			.put(GSTConstants.USERACCESS6, 1)
				.put(GSTConstants.RETURN_PREIOD, 1)
				.put(GSTConstants.RecipientGSTIN, 1)
				.put(GSTConstants.DOC_TYPE,1)
				.put(GSTConstants.DOC_NO,1)
				.put(GSTConstants.DOC_DATE,1)
				.put(GSTConstants.PRE_GST,1)
				.put(GSTConstants.SGSTIN,1)
				.put(GSTConstants.SUPPLIER_TYPE,1)
				.put(GSTConstants.DifferentialPercentageFlag,1)
				.put(GSTConstants.POS,1)
				.put(GSTConstants.PORT_CODE,1)
				.put(GSTConstants.BillOfEntry,1)
				.put(GSTConstants.BillOfEntryDate,1)
				.put(GSTConstants.SECTION7OFIGSTFLAG,1)
				.put(GSTConstants.CLAIMREFUNDFLAG,1)
				.put(GSTConstants.AUTOPOPULATED,1)
				.put(GSTConstants.ReverseCharge,1)
				.put(GSTConstants.ITC_ENTITLEMENT,1)
				.put(GSTConstants.POSTING_DATE,1)
				.put(GSTConstants.EWay_BillNo,1)
				.put(GSTConstants.EWay_BillDate,1)
				
				.build();

	private static boolean isHeaderField(String fieldName) {
		return HEADER_FIELDS.containsKey(fieldName);
	}

	static Map<String, List<ProcessingResult>> keyMap = new HashMap<>();

	public static List<ProcessingResult> eliminateDuplicates(
			List<ProcessingResult> errors) {

		List<ProcessingResult> nonHeaderErrors = 
					new ArrayList<ProcessingResult>();
		Map<String, ProcessingResult> headerErrorsMap = 
					new HashMap<>();
		
		for (ProcessingResult error : errors) {
			TransDocProcessingResultLoc loc = 
					(TransDocProcessingResultLoc) error.getLocation();
			Object[] fields = loc.getFieldIdentifiers();
			if (fields.length == 0) {
				nonHeaderErrors.add(error);
				continue;
			}
			
			String fieldName = (String) fields[0];
			boolean isHeaderField = isHeaderField(fieldName);
			
			if (isHeaderField) {
				String key = fieldName + error.getCode();
				headerErrorsMap.putIfAbsent(key, error);
			} else {
				nonHeaderErrors.add(error);
			}
			
		}

		List<ProcessingResult> retResult = new ArrayList();
		//added hon_header_errors
		retResult.addAll(nonHeaderErrors);
		//added header_errors
		retResult.addAll(headerErrorsMap.values());
		
		
		return retResult;
	}

}
