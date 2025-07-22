
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
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AAROrgRate
		implements ARBusinessRuleValidator<Gstr1AAsEnteredAREntity> {

	@Autowired
	@Qualifier("DefaultRateCache")
	private RateCache rateCahe;

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredAREntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getOrgRate() != null && !document.getOrgRate().isEmpty()) {
			BigDecimal orgRate = BigDecimal.ZERO;
			String rate = document.getOrgRate();
			if (rate != null && !rate.isEmpty()) {
				orgRate = NumberFomatUtil.getBigDecimal(rate);
			}
			rateCahe = StaticContextHolder.getBean("DefaultRateCache",
					RateCache.class);

			int n = rateCahe.findByIgst(orgRate);
			if (n <= 0) {
				errorLocations.add(GSTConstants.OrgRate);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5109",
						"Invalid Org Rate", location));
				return errors;
			}
		}
		return errors;
	}

}
