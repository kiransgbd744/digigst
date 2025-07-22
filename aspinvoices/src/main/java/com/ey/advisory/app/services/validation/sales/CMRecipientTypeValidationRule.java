package com.ey.advisory.app.services.validation.sales;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.RecipientTypeCache;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

@Component("CMRecipientTypeValidationRule")
public class CMRecipientTypeValidationRule
		implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultRecipientTypeCache")
	private RecipientTypeCache recipientTypeCache;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getCustOrSuppType() != null
				&& !document.getCustOrSuppType().isEmpty()) {
			recipientTypeCache = StaticContextHolder.getBean(
					"DefaultRecipientTypeCache", RecipientTypeCache.class);
			int  n = 
					recipientTypeCache.findRecType(
							trimAndConvToUpperCase(document.getCustOrSuppType()));
			if(n<=0){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RECIPIENTTYPE);
				TransDocProcessingResultLoc location 
				                    = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, " ER0051",
						"Invalid Recipient Type.", location));

			}
		}

		return errors;
	}
}
