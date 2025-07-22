/*package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
import com.ey.advisory.app.caches.CategoryCache;
import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

*//**
 * @author Siva.Nandam
 *
 *//*
public class LuCatoryValidation
		implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultCatoryCache")
	private CategoryCache Cache;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (document.getCategory() != null
				&& !document.getCategory().isEmpty()) {
			String catogery = document.getCategory();

			Cache = StaticContextHolder.getBean("DefaultCatoryCache",
					CategoryCache.class);
			int n = Cache.findCategory(trimAndConvToUpperCase(catogery));

			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.CATEGORY);
				TransDocProcessingResultLoc location 
				        = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10003",
						"Invalid Category as per Master", location));
			}
		}
		return errors;
	}

}
*/