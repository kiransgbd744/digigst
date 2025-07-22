package com.ey.advisory.app.services.businessvalidation.master;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.MasterItemEntity;
import com.ey.advisory.admin.data.repositories.client.RateRepository;
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
public class ItemRate implements BusinessRuleValidator<MasterItemEntity> {

	@Autowired
	@Qualifier("RateRepository")
	private RateRepository rateRepository;

	@Override
	public List<ProcessingResult> validate(MasterItemEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getRate() != null) {
			rateRepository = StaticContextHolder.getBean("RateRepository",
					RateRepository.class);
			BigDecimal rate = document.getRate();
			int count = rateRepository.findByIgst(rate);
			if (count == 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add("RATE");
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1644",
						"Invalid Rate", location));
				return errors;
			}
		}

		return errors;
	}
}
