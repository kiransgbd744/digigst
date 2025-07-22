/*package com.ey.advisory.app.services.validation.sales;


import static com.ey.advisory.app.data.entities.client.GSTConstants
                                                             .APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.DOC_DATE;
import static com.ey.advisory.app.data.entities.client.GSTConstants
                                                          .ORIGINAL_DOC_DATE;

import java.time.LocalDate;
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
*//**
 * 
 * @author Balakrishna S
 * BR_OUTWARD_42
 *
 *//*

@Component("invRnvValidator")
public class InvRnvValidator 
                           implements DocRulesValidator<OutwardTransDocument>{

//	private String docType;
	private LocalDate docDate;
	private LocalDate origDocDate;
	@Override
public List<ProcessingResult> validate(OutwardTransDocument document,
		ProcessingContext context){
	
	
	List<ProcessingResult> errors = new ArrayList<>();
	List<String> errorLocations = new ArrayList<>();
	if(document.getDocType()!=null && !document.getDocType().isEmpty()){
	//docType = document.getDocType();
		}
	if(document.getDocDate()!=null){
	 docDate=document.getDocDate();
	}
	if(document.getOrigDocDate()!=null){
	 origDocDate=document.getOrigDocDate();
	}
	DateTimeFormatter formatter=DateTimeFormatter.ofPattern("MM-yyyy");
	if(document.getDocType()!=null && !document.getDocType().isEmpty()){
		if(document.getDocDate()!=null){
			if(document.getOrigDocDate()!=null){
		
	
	 if(GSTConstants.RNV.equalsIgnoreCase(document.getDocType())){
	 {
		 if(docDate!=null){
		 if(docDate.format(formatter).equals(origDocDate.format(formatter))){ 
			 errorLocations.add(DOC_DATE);
			 errorLocations.add(ORIGINAL_DOC_DATE);
			 TransDocProcessingResultLoc location 
			                             = new TransDocProcessingResultLoc(
						errorLocations.toArray());
			 errors.add(new ProcessingResult(APP_VALIDATION, "ER229" ,""
			 		+ "Original Document & Revised Document cannot be "
			 		+ "reported in same tax period "
			 		+ "for the same Document Number",location));			 
		 }
		 } 
	 }
		
		}
		}
			}
	
	}	return errors;
}
}
*/