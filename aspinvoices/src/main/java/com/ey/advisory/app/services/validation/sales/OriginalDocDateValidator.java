/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.ORIGINAL_DOC_DATE;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
*//**
 * 
 * @author Hemasundar.J
 * 
 *         BR_OUTWARD_76
 * 
 *         This class is responsible for performing business rule Amendment
 *         cannot be done backdated.
 * 
 *//*
@Component("OriginalDocDateValidator")
public class OriginalDocDateValidator
		implements DocRulesValidator<OutwardTransDocument> {
	private static final List<String> DOC_TYPES_REQUIRING_IMPORTS 
	= ImmutableList
			.of(GSTConstants.RNV,GSTConstants.RCR,
					GSTConstants.RDR,GSTConstants.RRFV);

	*//**
	 * Original document date should be prior to the Return period in case of
	 * RNV/RCR/RDR/RRFV.
	 *//*
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if(document.getDocType()!=null && !document.getDocType().isEmpty()){
			if(document.getTaxperiod()!=null 
					&& !document.getTaxperiod().isEmpty()){
				if(document.getOrigDocDate()!=null){
		String doctype = document.getDocType();
		//String taxPeriod = document.getTaxperiod();
		String tax = "01" + document.getTaxperiod();
		DateTimeFormatter formatter = 
				DateTimeFormatter.ofPattern("ddMMyyyy");
	
	// Calculate the last day of the month.
	LocalDate returnPeriod = LocalDate.parse(tax, formatter);
		if (DOC_TYPES_REQUIRING_IMPORTS.contains(
				         trimAndConvToUpperCase(doctype))) {
			
			*//**
			 * taxPeriod has only month and year so any common date we are
			 * choosing for both returnPeriod and originalDocDate.
			 *//*
			
			if ((document.getOrigDocDate().compareTo(returnPeriod) > 0)) {
				errorLocations.add(ORIGINAL_DOC_DATE);
				TransDocProcessingResultLoc location 
				             = new TransDocProcessingResultLoc(
						errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER58",
						"Invalid Original Document Date or Original Document "
								+ "Date is missing",
						location));

			}
			}
		}
		}
		}
		return errors;
	}

}

*/