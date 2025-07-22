package com.ey.advisory.app.services.docs.einvoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class DuplicatesRemove {

	private DuplicatesRemove() {

	}

	public static Map<String, List<ProcessingResult>> eliminateDuplicates(
			Map<String, List<ProcessingResult>> processingEinvoiceResults) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();
		processingEinvoiceResults.entrySet().forEach(entry -> {
			// creating Map for non_header_errors
			Map<String, ProcessingResult> headerErrorsMap = new HashMap<>();
			String keymap = entry.getKey();
			List<ProcessingResult> errors = entry.getValue();
			for (ProcessingResult error : errors) {
				TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) error
						.getLocation();
				if(loc != null){
				Object[] fields = loc.getFieldIdentifiers();
				/*if (fields.length == 0) {
					nonHeaderErrors.add(error);
					continue;
				}*/

				String fieldName = (String) fields[0];

				// creating key by using fileldName and errorCode
				String key = fieldName + error.getCode()
						+ error.getDescription() + error.getLocation();
				headerErrorsMap.putIfAbsent(key, error);

			}
			}
			
			List<ProcessingResult> retResult = new ArrayList<>();

			retResult.addAll(headerErrorsMap.values());
			map.put(keymap, retResult);
		});
		// creating list for non_header_errors

		return map;
	}
}
