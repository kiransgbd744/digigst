package com.ey.advisory.app.services.structuralvalidation.gstr9;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.repositories.client.RateRepository;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnAsEnteredEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr9RateValidator
		implements BusinessRuleValidator<Gstr9HsnAsEnteredEntity> {

	@Autowired
	@Qualifier("RateRepository")
	private RateRepository rateRepository;

	@Override
	public List<ProcessingResult> validate(Gstr9HsnAsEnteredEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getRateOfTax() != null) {
			rateRepository = StaticContextHolder.getBean("RateRepository",
					RateRepository.class);
			String rate = document.getRateOfTax();
			int count = rateRepository.findByIgst(new BigDecimal(rate));
			if (count == 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add("RATE");
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER6187",
						"Invalid Rate as per Rate Master", location));
				return errors;
			}
		}

		return errors;
	}
}
