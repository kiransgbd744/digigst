package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.TransportCache;
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
@Component("LuTransportValidation")
public class LuTransportValidation implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultTransportCache")
	private TransportCache Cache;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (document.getTransportMode() != null
				&& !document.getTransportMode().isEmpty()) {
			String transportMode = document.getTransportMode();
		
			Cache = StaticContextHolder.getBean("DefaultTransportCache",
					TransportCache.class);
			int n = Cache.findTransport(transportMode.toUpperCase());
			
			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.TRANS_PORT_MODE);
				TransDocProcessingResultLoc location 
				           = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10121",
						"Invalid Transport Mode as per Master", location));
			}
		}
		return errors;
	}

}
