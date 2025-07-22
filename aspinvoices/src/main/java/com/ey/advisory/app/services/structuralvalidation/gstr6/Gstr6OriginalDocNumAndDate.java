package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;
/**
 * @author Siva.Nandam
 *
 */
public class Gstr6OriginalDocNumAndDate 
     implements BusinessRuleValidator<Gstr6DistributionExcelEntity> {

	private static final List<String> DOC_TYPE = ImmutableList.of(

			GSTConstants.CR, GSTConstants.RCR, GSTConstants.RNV);
	
	@Override
	public List<ProcessingResult> validate(
			Gstr6DistributionExcelEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (DOC_TYPE.contains(trimAndConvToUpperCase(document.getDocType()))) {
			if (document.getOrgDocNum() == null
					|| document.getOrgDocNum().isEmpty()) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORG_CREDIT_NOTE_NUM);
				TransDocProcessingResultLoc location 
				              = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER3019",
						"Original Document Number cannot be left blank.",
						location));
			}
			if (document.getOrgDocDate() == null
					) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORG_CREDIT_NOTE_DATE);
				TransDocProcessingResultLoc location 
				               = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, " ER3021",
						"Original Document Date cannot be left blank.",
						location));
			}
		}
		return errors;
	}

}
