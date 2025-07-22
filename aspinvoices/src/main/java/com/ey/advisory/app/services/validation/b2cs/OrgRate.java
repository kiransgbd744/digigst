package com.ey.advisory.app.services.validation.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.RateCache;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class OrgRate
		implements B2csBusinessRuleValidator<Gstr1AsEnteredB2csEntity> {

	@Autowired
	@Qualifier("DefaultRateCache")
	private RateCache rateCache; 

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getOrgRate() != null && !document.getOrgRate().isEmpty()) {
			BigDecimal orgRate = BigDecimal.ZERO;
			String rate = document.getOrgRate().trim();
			if (rate != null && !rate.isEmpty()) {
				orgRate = NumberFomatUtil.getBigDecimal(rate);
			}
			rateCache = StaticContextHolder.getBean("DefaultRateCache",
					RateCache.class);

			int n = rateCache.findByIgst(orgRate);
			if (n <= 0) {
				errorLocations.add(GSTConstants.OrgRate);
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5015",
						"InvalidOrgRate", location));
				return errors;
			}
		}
		return errors;
	}

}
