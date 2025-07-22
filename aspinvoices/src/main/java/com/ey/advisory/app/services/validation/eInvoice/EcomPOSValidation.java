/*package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.StateCache;
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
public class EcomPOSValidation
		implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.Y.equalsIgnoreCase(document.getEcomTransaction())) {
			if (document.getEcomPos() != null
					&& !document.getEcomPos().isEmpty()) {
				String stateCode = document.getEcomPos();
				stateCache = StaticContextHolder.getBean("DefaultStateCache",
						StateCache.class);
				int n = stateCache.findStateCode(stateCode);
				if (n <= 0) {

					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.E_COM_POS);
					TransDocProcessingResultLoc location 
					= new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER10116",
							"Invalid Ecom POS", location));
				}
			}
		}
		return errors;
	}
}
*/