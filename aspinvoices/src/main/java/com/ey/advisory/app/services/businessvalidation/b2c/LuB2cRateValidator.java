package com.ey.advisory.app.services.businessvalidation.b2c;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.RateCache;
import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class LuB2cRateValidator
		implements BusinessRuleValidator<OutwardB2cExcelEntity> {

	/*@Autowired
	@Qualifier("RateRepository")
	private RateRepository rateRepository;*/
	@Autowired
	@Qualifier("DefaultRateCache")
	private RateCache rateRepository;

	@Override
	public List<ProcessingResult> validate(OutwardB2cExcelEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getRate() != null && !document.getRate().isEmpty()) {
			BigDecimal orgRate = BigDecimal.ZERO;
			String rate = document.getRate().trim();
			if (rate != null && !rate.isEmpty()) {
				orgRate = NumberFomatUtil.getBigDecimal(rate);
			}
			/*rateRepository = StaticContextHolder.getBean("RateRepository",
					RateRepository.class);*/
			rateRepository = StaticContextHolder.getBean("DefaultRateCache",
					RateCache.class);

			int n = rateRepository.findByIgst(orgRate);
			if (n <= 0) {
				errorLocations.add(GSTConstants.RATE);
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0216",
						"Invalid  Rate", location));
				return errors;
			}
		}
		return errors;
	}
}
