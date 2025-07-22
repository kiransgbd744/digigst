package com.ey.advisory.app.services.strcutvalidation.outward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableMap;

/**
 * @author Mahesh.Golla
 *
 */
public class InwardHeaderStructuralValidationUtil {

	private InwardHeaderStructuralValidationUtil() {
	}

	// Header Fields
	private static final Map<String, Integer> HEADER_FIELDS = 
			new ImmutableMap.Builder<String, Integer>()
			.put(GSTConstants.IRN_DATE, 1)
			.put(GSTConstants.DOC_TYPE,1)
			.put(GSTConstants.DOC_NO, 1)
			.put(GSTConstants.DOC_DATE, 1)
			.put(GSTConstants.ReverseCharge, 1)
			.put(GSTConstants.SGSTIN, 1)
			.put(GSTConstants.RecipientGSTIN, 1)
			.put(GSTConstants.POS, 1)
			.put(GSTConstants.ITC_ENTITLEMENT, 1)
			.put(GSTConstants.BillOfEntryDate, 1)
			.put(GSTConstants.PAYMENT_DUE_DATE, 1)
			.put(GSTConstants.RETURN_PREIOD, 1)
			.put(GSTConstants.DifferentialPercentageFlag, 1)
			.put(GSTConstants.SECTION7OFIGSTFLAG, 1)
			.put(GSTConstants.POSTING_DATE, 1)
			.put(GSTConstants.EWay_BillDate, 1)
			.put(GSTConstants.PURCHASE_DATE, 1)
			.put(GSTConstants.USER_DEFINED28, 1)
			.build();

	private static boolean isHeaderField(String fieldName) {
		return HEADER_FIELDS.containsKey(fieldName);
	}

	static Map<String, List<ProcessingResult>> keyMap = new HashMap<>();

	public static List<ProcessingResult> eliminateDuplicates(
			List<ProcessingResult> errors) {
		// creating list for non_header_errors
		List<ProcessingResult> nonHeaderErrors = new ArrayList<ProcessingResult>();
		// creating Map for non_header_errors
		Map<String, ProcessingResult> headerErrorsMap = new HashMap<>();

		for (ProcessingResult error : errors) {
			TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) error
					.getLocation();
			if(loc != null){
			Object[] fields = loc.getFieldIdentifiers();
			if (fields.length == 0) {
				nonHeaderErrors.add(error);
				continue;
			}

			String fieldName = (String) fields[0];
			boolean isHeaderField = isHeaderField(fieldName);

			if (isHeaderField) {
				// creating key by using fileldName and errorCode
				String key = fieldName + error.getCode();
				headerErrorsMap.putIfAbsent(key, error);
			} else {
				nonHeaderErrors.add(error);
			}
		}
		}

		List<ProcessingResult> retResult = new ArrayList<ProcessingResult>();
		// added non_header errors.
		retResult.addAll(nonHeaderErrors);
		// added header_errors
		retResult.addAll(headerErrorsMap.values());

		return retResult;
	}

}
