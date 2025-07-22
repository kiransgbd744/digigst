package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;
@Component("OriginalDocumentType")
public class OriginalDocumentType implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> DOCUMENT_TYPE_IMPORTS = 
			ImmutableList.of(GSTConstants.INV,GSTConstants.RNV,GSTConstants.CR,
					GSTConstants.RFV,GSTConstants.RRFV,GSTConstants.DR,
					GSTConstants.RCR,GSTConstants.RDR,
					GSTConstants.DLC,GSTConstants.RDLC,GSTConstants.RV,
					GSTConstants.RRV,GSTConstants.AV,GSTConstants.RAV);
	
	
	@Override
	public List<ProcessingResult> validate(
			OutwardTransDocument document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if(document.getOrigDocType()!=null && !document.getOrigDocType().isEmpty()){
		if(!(DOCUMENT_TYPE_IMPORTS.contains(
				trimAndConvToUpperCase(document.getOrigDocType())))){
			
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.ORGDOC_TYPE);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0110",
					"Invalid Original Document Type.",
					location));
		}
		}
		return errors;
	}

}
