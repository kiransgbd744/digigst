package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.DOC_TYPE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

@Component("DuplicateCheckValidator6")
public class DuplicateCheckValidator6 
implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository DocRepository;
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		String groupCode = TenantContext.getTenantId();
		if(document.getDocType()!=null && !document.getDocType().isEmpty()){
			if(document.getDocDate()!=null){
				if(document.getSgstin()!=null 
						                  && !document.getSgstin().isEmpty()){
					if(document.getDocNo()!=null 
							               && !document.getDocNo().isEmpty()){
		
		
						DocRepository = StaticContextHolder.
					getBean("DocRepository",DocRepository.class);
		
	
		if(GSTConstants.INV.equalsIgnoreCase(document.getDocType())){
			int i=DocRepository.findduplicates(document.getDocDate(),
					document.getSgstin(),document.getDocNo());
		if (i>0){
				
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(DOC_TYPE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
			errors.add(
					new ProcessingResult(APP_VALIDATION, "ER207",
							"This Invoice Number has already been "
							+ "reported in an earlier tax period",
							location));
			
		}
		}
		if(GSTConstants.CR.equalsIgnoreCase(document.getDocType())){
			int j=DocRepository.findCRduplicates(document.getDocDate(),
					document.getSgstin(),document.getDocNo());
			if (j>0){
					
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(DOC_TYPE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
				errors.add(
						new ProcessingResult(APP_VALIDATION, "ER246",
								"Credit Note has already been reported "
								+ "in an earlier Tax Period",
								location));
				
			}
			}
		if(GSTConstants.DR.equalsIgnoreCase(document.getDocType())){
			int k=DocRepository.findDRduplicates(document.getDocDate(),
					document.getSgstin(),document.getDocNo());
			if (k>0){
					
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(DOC_TYPE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
				errors.add(
						new ProcessingResult(APP_VALIDATION, "ER247",
								"Debit Note has already been reported "
								+ "in an earlier Tax Period",
								location));
				
			}
			}
		
					}
				}
			}
			
		}
		return errors;
	}

}
