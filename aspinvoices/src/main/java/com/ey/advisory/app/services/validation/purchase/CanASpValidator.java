/*package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.ORIGINAL_DOC_NO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
*//**
 * This class is Responsible for In case Supply Type is "CAN" the 
 * tax payer is expected to provide the entire original record 
 * detail with Document Type updated as "CAN", 
 * find the original details of such record based on
 *  key: "SupplierGSTIN + DocumentNumber +  DocumentDate + CustomerGSTIN " and 
 *  delete the record from ASP. The entire record need to be 
 *  same except the Supply type which shall be CAN.
 *//*

*//**
 *
 * BR_OUTWARD_53
 * BR_OUTWARD_54
 * BR_OUTWARD_55
 * @author Siva.Nandam
 *
 *//*
@Component("CanASpValidator")
public class CanASpValidator 
            implements DocRulesValidator<OutwardTransDocument> {
	
	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository DocRepository;
	
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();
		DocRepository = StaticContextHolder.getBean("InwardTransDocRepository", 
				InwardTransDocRepository.class);
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<OutwardTransDocLineItem> items =  document.getLineItems();
		Set<String> errorLocations = new HashSet<>();
		
		if(document.getDocType()!=null && !document.getDocType().isEmpty()){
			if(document.getDocNo()!=null && !document.getDocNo().isEmpty()){
	if(document.getFinYear()!=null && !document.getFinYear().isEmpty()){
	if(document.getSupplyType()!=null && !document.getSupplyType().isEmpty()){
		if(document.getCgstin()!=null && !document.getCgstin().isEmpty())	{				
			if(document.getSgstin()!=null && !document.getSgstin().isEmpty()){
		int deleteCanInv = DocRepository.
				  findOrginalDetails(document.getSgstin(),document.getCgstin(),
						  document.getDocType(),document.getDocNo(),
						  document.getFinYear()); 
		
		IntStream.range(0, items.size()).forEach(idx->{
			
				if("CAN".equalsIgnoreCase(document.getSupplyType())){
						  
					 if(deleteCanInv<=0){
					 
					errorLocations.add(ORIGINAL_DOC_NO);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER236 ",
							"Cancelled invoice cannot be "
							+ "reported once the return for the same"
							+ " tax period is filed",
							location));
				}
				else if(deleteCanInv>0){
					 DocRepository.
					 updateOrginalDetails(document.getSgstin(),
							 document.getCgstin(),
							  document.getDocType(),document.getDocNo(),
							  document.getFinYear());
					errorLocations.add(ORIGINAL_DOC_NO);
					
					
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER236 ",
							"Cancelled invoice cannot be "
							+ "reported once the return for the same"
							+ " tax period is filed",
							location));
				}
			}
			
		});		
						}
			
		
					}
	}	}
			}
		}
		return errors;
	}

}
*/