package com.ey.advisory.app.services.strcutvalidation.itc04;

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
public class Itc04HeaderStructuralValidationUtil {

	private Itc04HeaderStructuralValidationUtil() {
	}

	// Header Fields
	private static final Map<String, Integer> HEADER_FIELDS = new ImmutableMap.Builder<String, Integer>()

			.put(GSTConstants.JOB_WORKER_GSTIN, 1)
			.put(GSTConstants.JOB_WORKER_STATE_CODE, 1)
			.put(GSTConstants.JOB_WORKER_TYPE, 1)
			.put(GSTConstants.JOB_WORKER_ID, 1)
			.put(GSTConstants.JOB_WORKER_NAME, 1)
			.put(GSTConstants.POSTING_DATE, 1).put(GSTConstants.UserID, 1)
			.put(GSTConstants.CompanyCode, 1)
			.put(GSTConstants.ACCOUNTING_VOCHAR_NUM, 1)
			.put(GSTConstants.ACCVOCHDATE, 1).build();

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
			if (loc != null) {
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

		List<ProcessingResult> retResult = new ArrayList();
		// added non_header errors.
		retResult.addAll(nonHeaderErrors);
		// added header_errors
		retResult.addAll(headerErrorsMap.values());

		return retResult;
	}

}
