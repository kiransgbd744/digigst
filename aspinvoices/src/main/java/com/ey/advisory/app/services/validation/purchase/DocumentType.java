package com.ey.advisory.app.services.validation.purchase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import com.ey.advisory.app.caches.DocTypeCache;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Siva.Nandam
 *
 */
public class DocumentType implements DocRulesValidator<InwardTransDocument> {
	@Autowired
	@Qualifier("DefaultInwardDocTypeCache")
	private DocTypeCache docTypeCache;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if(document.getDocType()!=null && !document.getDocType().isEmpty()){
			
			docTypeCache = StaticContextHolder.getBean(
					"DefaultInwardDocTypeCache", DocTypeCache.class);
			int n = 
					docTypeCache.findDocType(
							trimAndConvToUpperCase(document.getDocType()));
		if(n<=0){
			
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DOC_TYPE);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1038",
					"Invalid Document Type",
					location));
		}
		}
		return errors;
	}

}
