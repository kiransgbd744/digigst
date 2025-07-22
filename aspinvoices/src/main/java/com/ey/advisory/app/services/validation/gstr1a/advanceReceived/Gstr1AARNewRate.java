package com.ey.advisory.app.services.validation.gstr1a.advanceReceived;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.RateCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
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
public class Gstr1AARNewRate
		implements ARBusinessRuleValidator<Gstr1AAsEnteredAREntity> {

	@Autowired
	@Qualifier("DefaultRateCache")
	private RateCache rateCache;

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredAREntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		rateCache = StaticContextHolder.getBean("DefaultRateCache",
				RateCache.class);
		if (document.getNewRate() != null && !document.getNewRate().isEmpty()) {
			BigDecimal newRate = BigDecimal.ZERO;
			String rate = document.getNewRate();
			if (rate != null && !rate.isEmpty()) {
				newRate = new BigDecimal(rate);
			}
			int n = rateCache.findByIgst(newRate);
			if (n <= 0) {
				errorLocations.add(GSTConstants.NEW_RATE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5116",
						"Invalid new Rate", location));
				return errors;
			}
		}
		return errors;
	}

}
