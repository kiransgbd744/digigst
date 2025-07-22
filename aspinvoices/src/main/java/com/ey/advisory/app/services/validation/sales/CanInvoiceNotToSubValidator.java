/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants.
                                                              APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.SUPPLY_TYPE;

import java.time.LocalDate;
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
import com.ey.advisory.app.data.repositories.client.
												OutwardTransDocumentRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
*//**
 * 
 * @author Mahesh.Golla
 *
 * BR_OUTWARD_51
 *//*
*//**
 *  
 *  This class responsible for In case of Cancelled Invoice, the original 
 *  invoice of same already saved on GSTN but not yet submitted, put the 
 *  "Delete" flag for the original records and report to GSTN
 * 
 *
 *//*

@Component("canInvoiceNotToSubValidator")
public class CanInvoiceNotToSubValidator
		implements DocRulesValidator<OutwardTransDocument> {
	private LocalDate docDate;
	private String docNumber;
	private String csgstin;
	private String sgstin;
	private String SupplyType;
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();
		docRepository = StaticContextHolder
				.getBean("DocRepository", DocRepository.class);
		
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<OutwardTransDocLineItem> items =  document.getLineItems();
		Set<String> errorLocations = new HashSet<>();
		if(SupplyType !=null && !SupplyType.isEmpty()){
		SupplyType = document.getSupplyType();
		}
		if(sgstin !=null && !sgstin.isEmpty()){
		 sgstin = document.getSgstin();
		}
		if(csgstin !=null && !csgstin.isEmpty()){
		csgstin = document.getCgstin();
		}
		if(docNumber !=null && !docNumber.isEmpty()){
		 docNumber = document.getDocNo();
		}
		if(docDate != null){
			docDate = document.getDocDate();
		}
		
		IntStream.range(0, items.size()).forEach(idx->{
			if(SupplyType !=null && !SupplyType.isEmpty()){
				if(docNumber !=null && !docNumber.isEmpty()){
					if(csgstin !=null && !csgstin.isEmpty()){
						if(sgstin !=null && !sgstin.isEmpty()){
					
			if(SupplyType.equalsIgnoreCase("CAN")){
				
			

				
				int deleteCanInv = 
						  docRepository.
						  getOrginalDetails(sgstin,csgstin,docNumber,docDate); 
				if(docDate !=null && deleteCanInv>0){
					errorLocations.add(SUPPLY_TYPE);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ERRXXX ",
							"Original Document is not reported in the current "
							+ "tax period ",
							location));
				}
			}
					}}}}
		});		

		return errors;
	}

}
*/