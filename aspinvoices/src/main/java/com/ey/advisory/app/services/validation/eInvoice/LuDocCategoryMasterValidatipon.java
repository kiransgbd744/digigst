package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.DocCategoryCache;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
@Component("LuDocCategoryMasterValidatipon")
public class LuDocCategoryMasterValidatipon
		implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultDocCatoryCache")
	private DocCategoryCache Cache;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getDocCategory() != null
				&& !document.getDocCategory().isEmpty()) {
			String docCatogy = document.getDocCategory();

			Cache = StaticContextHolder.getBean("DefaultDocCatoryCache",
					DocCategoryCache.class);
			int n = Cache.findDocCategory(trimAndConvToUpperCase(docCatogy));

			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_CATEGORY);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10006",
						"Invalid Doc Category as per Master", location));

			}
		}
		return errors;
	}

}
