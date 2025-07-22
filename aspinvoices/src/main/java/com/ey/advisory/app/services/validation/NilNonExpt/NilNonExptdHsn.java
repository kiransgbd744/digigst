package com.ey.advisory.app.services.validation.NilNonExpt;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.data.entities.client.Gstr1NilNonExemptedAsEnteredEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class NilNonExptdHsn implements
		B2csBusinessRuleValidator<Gstr1NilNonExemptedAsEnteredEntity> {

	@Autowired
	@Qualifier("DefaultHsnCache")
	private HsnCache hsnCache;

	@Override
	public List<ProcessingResult> validate(
			Gstr1NilNonExemptedAsEnteredEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		if (document.getHsn() != null && !document.getHsn().trim().isEmpty()) {

			hsnCache = StaticContextHolder.getBean("DefaultHsnCache",
					HsnCache.class);

			String hsnOrSac = document.getHsn();
			int i = hsnCache.findhsn(hsnOrSac);
			if (i <= 0) {
				errorLocations.add(HSNORSAC);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5705",
						"Invalid HSN.", location));
			}
		}
		return errors;
	}

}
