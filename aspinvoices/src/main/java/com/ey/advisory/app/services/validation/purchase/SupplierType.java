package com.ey.advisory.app.services.validation.purchase;

import java.util.ArrayList;
import java.util.HashSet;
/**
 * Siva krishna
 */
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.RecipientTypeCache;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

public class SupplierType implements DocRulesValidator<InwardTransDocument> {

	@Autowired
	@Qualifier("DefaultRecipientTypeCache")
	private RecipientTypeCache recipientTypeCache;
	
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
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
				errorLocations.add(GSTConstants.SUPPLIER_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1056",
						"Invalid Supplier Type.", location));

			}
		}

		return errors;
	}
}
