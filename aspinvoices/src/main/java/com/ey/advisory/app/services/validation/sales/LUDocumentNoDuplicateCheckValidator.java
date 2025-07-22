package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.DOC_NO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

@Component("LUDocumentNoDuplicateCheckValidator")
public class LUDocumentNoDuplicateCheckValidator implements 
DocRulesValidator<OutwardTransDocument>{
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository DocRepository;
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		DocRepository = StaticContextHolder.getBean(
				"DocRepository", DocRepository.class);
		List<ProcessingResult> errorsInfo = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		
		int n=DocRepository.findDuplicateCheck(
				document.getSgstin(),document.getDocDate(),
				document.getDocNo(),
				document.getFinYear());
			
		if(n>0){
			int submit=DocRepository.findDuplicateChecksubmit(
					document.getSgstin(),document.getDocDate(),
					document.getDocNo(),
					document.getFinYear());
			if(submit>0){
				
				errorLocations.add(DOC_NO);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorsInfo.add(new ProcessingResult(APP_VALIDATION,
						ProcessingResultType.INFO, "IN057",
						"Invalid  Document Number "
						, location)); 
			}
			if(submit<=0){
				//todo set all values
				
			}
		}
		return errorsInfo;
	}

}

