package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.CountryCodeCache;
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
public class LuOriginCountryValidation 
             implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultCountryCodeCache")
	private CountryCodeCache Cache;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			String originCountry = item.getOriginCountry();
			if (originCountry != null
					&& !originCountry.isEmpty()) {
				
			
				Cache = StaticContextHolder.getBean("DefaultCountryCodeCache",
						CountryCodeCache.class);
				int n = Cache.findCountryCode(originCountry);

				if (n <= 0) {

					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.ORIGIN_COUNTRY);
					TransDocProcessingResultLoc location 
					           = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER10051",
							"Invalid Origin Country", location));
				}
			}
		});
		
		return errors;
	}

}
