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

/**
 * 
 * @author Mahesh.Golla
 *
 */
public class Gstr3bCess implements BusinessRuleValidator<Gstr3bExcelEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr3bExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		String description = document.getDescription();

		BigDecimal cessAmt = BigDecimal.ZERO;
		String cess = (document.getCessAmnt() != null)
				? document.getCessAmnt().trim() : null;
		if (cess != null && !cess.isEmpty()) {
			cessAmt = NumberFomatUtil.getBigDecimal(cess);
		}
		if (description != null && !description.isEmpty()) {
			description = description.replaceAll("\\s", "").toUpperCase();
			if (GSTConstants.desriptionGstr3B.equalsIgnoreCase(description)) {
				return errors;
			} else {
				Set<String> errorLocations = new HashSet<>();
				if (cessAmt.compareTo(BigDecimal.ZERO) < 0) {
					errorLocations.add(GSTConstants.CESS_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							"ER6110", "Invalid Cess Amount.", location));
					return errors;
				}
			}
		} else {
			Set<String> errorLocations = new HashSet<>();
			if (cessAmt.compareTo(BigDecimal.ZERO) < 0) {
				errorLocations.add(GSTConstants.CESS_AMOUNT);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
						"ER6110", "Invalid Cess Amount.", location));
				return errors;
			}
		}
		return errors;
	}

}
