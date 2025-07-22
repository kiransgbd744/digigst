package com.ey.advisory.app.services.validation.sales;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
public class ReasonForCreditDebitNote 
        implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = ImmutableList
			.of(GSTConstants.RNV,GSTConstants.CR,
					GSTConstants.DR, GSTConstants.RCR, GSTConstants.RDR);
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		// TODO Auto-generated method stub
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			
				if (ORGDOCNUM_REQUIRING_IMPORTS
						.contains(trimAndConvToUpperCase(document.getDocType()))) {
					//commented this to compile as crdrReason is moved to line item
					/*if((!(document.getCrDrReason() != null )) 
							|| document.getCrDrReason().isEmpty()){
							
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);
						TransDocProcessingResultLoc location = new 
								TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION, "ER057",
								"Invalid Original Document Number "
									+ "or Original Document Number is missing",
								location));
					}*/

				
			}
			
		}

		return errors;
	}
}
