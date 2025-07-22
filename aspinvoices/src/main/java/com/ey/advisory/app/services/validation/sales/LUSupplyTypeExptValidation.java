/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.ORIGINAL_DOC_DATE;
import static com.ey.advisory.app.data.entities.client.GSTConstants.ORIGINAL_DOC_NO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

public class LUSupplyTypeExptValidation  
                           implements DocRulesValidator<OutwardTransDocument> {
	
	private static final List<String> SUPPLY_TYPE_IMPORTS = ImmutableList
			.of(GSTConstants.EXPT,GSTConstants.EXPWT);
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository DocRepository;
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		DocRepository = StaticContextHolder.getBean(
				"DocRepository", DocRepository.class);
		List<ProcessingResult> errorsInfo = new ArrayList<>();
		
		
		if(document.getSgstin()!=null 
				&& !document.getSgstin().isEmpty()){
			if(document.getOrigDocNo()!=null 
					&& !document.getOrigDocNo().isEmpty()){
				if(document.getOrigDocDate()!=null){
					if(document.getDocType()!=null 
							&& !document.getDocType().isEmpty()){
						
			if(GSTConstants.N.equalsIgnoreCase(document.getCrDrPreGst())){	
		if(document.getSupplyType()!=null 
				&& !document.getSupplyType().isEmpty()){
			if(SUPPLY_TYPE_IMPORTS.contains(
					trimAndConvToUpperCase(document.getSupplyType())) 
				&& GSTConstants.RNV.equalsIgnoreCase(document.getDocType())){
			
				int n=DocRepository.findDuplicateKeyDocnoAndDocdate(
						document.getSgstin(),document.getOrigDocNo(),
							document.getOrigDocDate());
				if(n<=0){
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(ORIGINAL_DOC_DATE);
					errorLocations.add(ORIGINAL_DOC_NO);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errorsInfo.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN200",
							"Invalid Original Document Number "
							+ "or Invalid Original Document Date", 
							location)); 	
				}
				
			}		
		if((!SUPPLY_TYPE_IMPORTS.contains(document.getSupplyType())) 
				&& GSTConstants.RNV.equalsIgnoreCase(document.getDocType())){
			int n=DocRepository.findDuplicateKeyDocnoAndDocdateandINV(
						document.getSgstin(),document.getOrigDocNo(),
						document.getOrigDocDate());
				if(n<=0){
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(ORIGINAL_DOC_DATE);
					errorLocations.add(ORIGINAL_DOC_NO);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errorsInfo.add(new ProcessingResult(APP_VALIDATION,
							ProcessingResultType.INFO, "IN200",
							"Invalid Original Document Number "
							+ "or Invalid Original Document Date",
						
							location)); 	
				}
			}
						}
			}
			}
					}
				}
		}
		return errorsInfo;
	}

}
*/