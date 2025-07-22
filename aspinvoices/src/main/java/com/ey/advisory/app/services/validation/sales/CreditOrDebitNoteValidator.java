/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants
                                                             .APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.CR;
import static com.ey.advisory.app.data.entities.client.GSTConstants.DR;

import java.time.LocalDate;
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
*//**
 * This class is Responsible  for 
 * The Debit/Credit notes raised in GST regime are required to be 
 * reported along with the Original Invoices raised even prior
 *  to GST. No validation as mentioned in BR_OUTWARD_7
 *//*

*//**
 * 
 * @author Siva.Nandam
 *BR_OUTWARD_8
 *//*

@Component("CreditOrDebitNoteValidator")
public class CreditOrDebitNoteValidator 
                        implements DocRulesValidator<OutwardTransDocument> {
	private static final List<String> CR_OR_DR_REQUIRING_IMPORTS = 
			ImmutableList.of(CR,DR);
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		
		List<ProcessingResult> errors = new ArrayList<>();

		 LocalDate July_2017 = LocalDate.of(2017, 07, 01); 
		if(document.getDocType()!=null && !document.getDocType().isEmpty()){
			 	if(CR_OR_DR_REQUIRING_IMPORTS.contains(document.getDocType())){
				if(document.isCrDrPreGst() == true){
					if((document.getOrigDocDate().compareTo(July_2017) > 0)){
						List<String> locations = new ArrayList<>();
						locations.add(GSTConstants.ORIGINAL_DOC_DATE);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
										null, locations.toArray());

					errors.add(new ProcessingResult(APP_VALIDATION, "ER058", 
								"Invalid Original Document Date "
								+ "or Original Document Date is missing",
								location));		
		}
				}
				if(document.isCrDrPreGst() == false){
					if((document.getOrigDocDate().compareTo(July_2017) < 0)){
						List<String> locations = new ArrayList<>();
						locations.add(GSTConstants.ORIGINAL_DOC_DATE);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
										null, locations.toArray());

						errors.add(new ProcessingResult(APP_VALIDATION, "ER058", 
								"Invalid Original Document Date "
								+ "or Original Document Date is missing",
								location));		
		}
				}
			
			}
		
		}
	
		   return errors;	   
	}

}
*/