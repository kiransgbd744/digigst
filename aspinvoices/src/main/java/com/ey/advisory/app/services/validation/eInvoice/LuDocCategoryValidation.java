package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.DocCategoryCache;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
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
@Component("LuDocCategoryValidation")
public class LuDocCategoryValidation
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
				List<OutwardTransDocLineItem> items = document.getLineItems();
				IntStream.range(0, items.size()).forEach(idx -> {
					OutwardTransDocLineItem item = items.get(idx);

					if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()
							&& item.getHsnSac().length() > 1
							&& GSTConstants.SERVICES_CODE.equalsIgnoreCase(
									item.getHsnSac().substring(0, 2))) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.DOC_CATEGORY);
						TransDocProcessingResultLoc location 
						            = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER10006", "Invalid Doc Category as per Master",
								location));
					}
				});
			}
		}
		return errors;
	}

}
