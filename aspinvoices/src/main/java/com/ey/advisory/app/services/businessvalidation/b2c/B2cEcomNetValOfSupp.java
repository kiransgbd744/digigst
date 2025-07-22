package com.ey.advisory.app.services.businessvalidation.b2c;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class B2cEcomNetValOfSupp
		implements BusinessRuleValidator<OutwardB2cExcelEntity> {
	private static final String ECOMNETVALOFSUPPMADE = "ECOMNETVALOFSUPPMADE";

	@Override
	public List<ProcessingResult> validate(OutwardB2cExcelEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if (document.getTcsFlag() != null && !document.getTcsFlag().isEmpty()) {
			String tcsFlag = document.getTcsFlag().trim();
			if (tcsFlag.equalsIgnoreCase(GSTConstants.Y)) {
				BigDecimal ecomNetVal = BigDecimal.ZERO;
				String ecomNetValue = (document.getEcomNetValSupp() != null)
						? document.getEcomNetValSupp().trim() : null;
				if (ecomNetValue != null && !ecomNetValue.isEmpty()) {
					ecomNetVal = NumberFomatUtil.getBigDecimal(ecomNetValue);
				}

				if (ecomNetVal.compareTo(BigDecimal.ZERO) < 0) {
					errorLocations.add(ECOMNETVALOFSUPPMADE);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(
							new ProcessingResult(APP_VALIDATION, "ER0228",
									"E-Com Value of Net Supplies made cannot"
									+ " be left blank.",
									location));
					return errors;

				}
			}
		}
		return errors;

	}

}
