/**
 * 
 */
package com.ey.advisory.app.service.upload.way3recon;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadProcessedHeaderEntity;
import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadProcessedItemEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author vishal.verma
 *
 */
public class EwbUploadItemNumberValidator implements BusinessRuleValidator<EwbUploadProcessedHeaderEntity>{

	@Override
	public List<ProcessingResult> validate(EwbUploadProcessedHeaderEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();



		List<EwbUploadProcessedItemEntity> items = document.getLineItems();
		Set<String> set = new HashSet<>();
		IntStream.range(0, items.size()).forEach(idx -> {

		EwbUploadProcessedItemEntity item = items.get(idx);
		String lineNo = item.getItemSerialNo();
		/*if (lineNo == null) {
		errorLocations.add(GSTConstants.LINE_NO);
		TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
		idx, errorLocations.toArray());

		errors.add(new ProcessingResult(APP_VALIDATION, "ER0046",
		"Line number Cannot be Empty", location));
		return;
		}*/

		if (!set.add(item.getItemSerialNo())) {
		errorLocations.add(GSTConstants.LINE_NO);
		TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
		idx, errorLocations.toArray());

		errors.add(new ProcessingResult(APP_VALIDATION, "ER0514",
		"Line number in a document cannot be repeated",
		location));
		}

		});

		return errors;
	
		
	}

}
