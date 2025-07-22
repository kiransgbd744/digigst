package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class OriginalDocumentNo implements DocRulesValidator<InwardTransDocument> {
	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = ImmutableList
			.of("RNV","RSLF","RDR","RCR","RFV","RRFV","CR","DR");
@Override
public List<ProcessingResult> validate(InwardTransDocument document,
		ProcessingContext context) {
	List<ProcessingResult> errors = new ArrayList<>();
	if (document.getDocType() != null && !document.getDocType().isEmpty()) {
		
			if (ORGDOCNUM_REQUIRING_IMPORTS
					.contains(document.getDocType())) {
				if((!(document.getOrigDocNo() != null )) 
						|| document.getOrigDocNo().isEmpty()){
						
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);
					TransDocProcessingResultLoc location = new 
							TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER057",
							"Invalid Original Document Number "
								+ "or Original Document Number is missing",
							location));
				}
//document.getCifValue();
			
		}
		
	}

	return errors;
}
}
