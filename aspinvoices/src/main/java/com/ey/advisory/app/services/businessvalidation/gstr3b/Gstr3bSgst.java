package com.ey.advisory.app.services.businessvalidation.gstr3b;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr3bExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Gstr3bSgst implements BusinessRuleValidator<Gstr3bExcelEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr3bExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		String description = document.getDescription();

		BigDecimal sgstAmt = BigDecimal.ZERO;
		String sgst = (document.getSgstAmnt() != null)
				? document.getSgstAmnt().trim() : null;
		if (sgst != null && !sgst.isEmpty()) {
			sgstAmt = NumberFomatUtil.getBigDecimal(sgst);
		}
		if (description != null && !description.isEmpty()) {
			description = description.replaceAll("\\s", "").toUpperCase();
			if (GSTConstants.desriptionGstr3B.equalsIgnoreCase(description)) {
				return errors;
			} else {
				Set<String> errorLocations = new HashSet<>();
				if (sgstAmt.compareTo(BigDecimal.ZERO) < 0) {
					errorLocations.add(GSTConstants.SGST_AMOUNT);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							"ER6109", "Invalid SGST Amount", location));
					return errors;
				}
			}
		} else {
			Set<String> errorLocations = new HashSet<>();
			if (sgstAmt.compareTo(BigDecimal.ZERO) < 0) {
				errorLocations.add(GSTConstants.SGST_AMOUNT);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
						"ER6109", "Invalid SGST Amount", location));
				return errors;
			}
		}
		return errors;
	}

}
