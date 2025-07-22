package com.ey.advisory.app.services.validation.gstr1a.InvoiceFile;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.NatureOfDocCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredInvEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public class Gstr1ANatureOfDocument
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredInvEntity> {

	@Autowired
	@Qualifier("DefaultNatureOfDocCache")
	private NatureOfDocCache natureOfDocCache;

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredInvEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		String docType = document.getNatureOfDocument();
		if (docType != null && !docType.isEmpty()) {
			natureOfDocCache = StaticContextHolder
					.getBean("DefaultNatureOfDocCache", NatureOfDocCache.class);
			docType = docType.replaceAll("\\s", "");
			int n = natureOfDocCache
					.findDocType(trimAndConvToUpperCase(docType));
			if (n <= 0) {
				errorLocations.add(GSTConstants.NatureOfDocument);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
						"ER5207", "Invalid Nature of Document", location));
			}
		}

		return errors;
	}

}
