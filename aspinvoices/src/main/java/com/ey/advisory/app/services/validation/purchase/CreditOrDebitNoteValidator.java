/*package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.DOC_TYPE;
import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.CR;
import static com.ey.advisory.app.data.entities.client.GSTConstants.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.collect.ImmutableList;

public class CreditOrDebitNoteValidator 
                       implements DocRulesValidator<InwardTransDocument>{

	private static final List<String> CR_OR_DR_REQUIRING_IMPORTS = 
			ImmutableList.of(CR,DR);
	@Autowired
    @Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository docRepository;
	
	private LocalDate origDocDate;
	
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		// TODO Auto-generated method stub
		docRepository = StaticContextHolder.getBean("InwardTransDocRepository", 
				InwardTransDocRepository.class);
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> locations = new ArrayList<>();
		
		if(document.getOrigDocDate()!=null){
			 origDocDate=document.getOrigDocDate();
			}
		List<InwardTransDocLineItem>  items = document.getLineItems();
		 LocalDate June_2017 = LocalDate.of(2017, 07, 01); 
		
		   
		    String groupCode = TenantContext.getTenantId();
		   if(document.getOrigDocDate() !=null){
			   if(document.getSgstin()!=null 
					   && !document.getSgstin().isEmpty()){
				   
			  if(document.getOrigDocNo()!=null 
					  && !document.getOrigDocNo().isEmpty()){
				 if(document.getDocType()!=null 
						 && !document.getDocType().isEmpty()) {
			 
					String sgstin = document.getSgstin() ;
					String origDocNo = document.getOrigDocNo() ;
			   int i = docRepository.findInvoices(sgstin, origDocNo, 
					   origDocDate);
			   
		   
		IntStream.range(0, items.size()).forEach(idx -> {
			InwardTransDocLineItem item = items.get(idx);
			 	if(CR_OR_DR_REQUIRING_IMPORTS.contains(document.getDocType())){
				if(document.isCrDrPreGst() == true){
					if((document.getOrigDocDate().compareTo(June_2017) > 0)){
						locations.add(DOC_TYPE);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
										idx, locations.toArray());

						errors.add(new ProcessingResult(APP_VALIDATION, "ER057", 
								"Invalid Original Document Number or "
							+ "Original Document Number is missing",location));		
		}
				}
				else	if(document.isCrDrPreGst() == false){
					if((document.getOrigDocDate().compareTo(June_2017) < 0)){
						locations.add(DOC_TYPE);
						locations.add(ORIGINAL_DOC_NO);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
										idx, locations.toArray());

						errors.add(new ProcessingResult(APP_VALIDATION, "ER057", 
								"Invalid Original Document Number or "
							+ "Original Document Number is missing",location));		
		}
				}
			
			}
			else if(CR_OR_DR_REQUIRING_IMPORTS.contains(document.getDocType())){
				
				if(document.isCrDrPreGst() == true 
						|| document.isCrDrPreGst() == false){
			if(i<=0){
				
				locations.add(DOC_TYPE);
				locations.add(ORIGINAL_DOC_NO);
				
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								idx, locations.toArray());

				errors.add(new ProcessingResult(APP_VALIDATION, "ER057", 
						"Invalid Original Document Number or "
						+ "Original Document Number is missing",location));		
				
			}}
			}
			
		
		});
		   }
			  } 
	}}
		   return errors;	   
	}

}

*/