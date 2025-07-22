package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class PostSeptFYValidation implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> DOC_TYPE_IMPORTS = ImmutableList.of(
			GSTConstants.RNV,GSTConstants.RCR,
			GSTConstants.RDR);
	private static final String SEPTEMBER="09";
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		
		List<ProcessingResult> errors = new ArrayList<>();
       
        if(document.getTaxperiod()==null ||document.getTaxperiod().isEmpty() ) return errors;
        if(document.getDocType()==null ||document.getDocType().isEmpty() ) return errors;
        if(GSTConstants.CR.equalsIgnoreCase(document.getDocType().toUpperCase()) 
        		&& document.getDocDate()==null )return errors;
        if(DOC_TYPE_IMPORTS.contains(document.getDocType().toUpperCase()) 
        		&& document.getPreceedingInvoiceDate()==null) return errors;
        LocalDate docDate = document.getDocDate();
        if(GSTConstants.CR.equalsIgnoreCase(document.getDocType().toUpperCase()) 
        		&& document.getPreceedingInvoiceDate()!=null ){
        	docDate = document.getPreceedingInvoiceDate();
        }
        if(DOC_TYPE_IMPORTS.contains(document.getDocType().toUpperCase())){
        	docDate=document.getPreceedingInvoiceDate();
        }
		String finYear = GenUtil.getFinYear(docDate);
		Integer derivedFy = GenUtil.convertTaxPeriodToInt(SEPTEMBER+finYear.substring(0, 2)+finYear.substring(4, 6));
		Integer derivedTaxperiod = GenUtil.convertTaxPeriodToInt(document.getTaxperiod());
		if(derivedTaxperiod > derivedFy){
			if(GSTConstants.CR.equalsIgnoreCase(document.getDocType())){
				if(document.getPreceedingInvoiceDate()!=null){
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.DOC_TYPE);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER15220",
							"Credit Note cannot be reported post September of "
							+ "next financial year (based on Preceeding document date)",
							location));
	            return errors;	
				}
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15216",
						"Credit Note same cannot be reported post "
						+ "September of next financial year (based on document date)",
						location));
            return errors;	
			}
			if(GSTConstants.RNV.equalsIgnoreCase(document.getDocType())){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15217",
						"Invoice cannot be amended post September of next financial "
						+ "year (based on preceeding document date)",
						location));
            return errors;	
			}
			if(GSTConstants.RCR.equalsIgnoreCase(document.getDocType())){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15218",
						"Credit Note cannot be amended post September "
						+ "of next financial year (based on preceeding document date)",
						location));
            return errors;	
			}
			if(GSTConstants.RDR.equalsIgnoreCase(document.getDocType())){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15219",
						"Debit Note cannot be amended post September of next "
						+ "financial year (based on preceeding document date)",
						location));
            return errors;	
			}
		}
		
		

		return errors;
	}
}
