package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.CountryCodeCache;
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
@Component("LuCountryCodeValidation")
public class LuCountryCodeValidation
		implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultCountryCodeCache")
	private CountryCodeCache Cache;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (document.getCountryCode() != null
				&& !document.getCountryCode().isEmpty()) {
			String countryCode = document.getCountryCode();
		
			Cache = StaticContextHolder.getBean("DefaultCountryCodeCache",
					CountryCodeCache.class);
			int n = Cache.findCountryCode(countryCode);

			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.COUNTRY_CODE);
				TransDocProcessingResultLoc location 
				           = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, " ER10088",
						"Invalid CountryCode", location));
			}
		}
		return errors;
	}

}
