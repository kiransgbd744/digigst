/**
 * 
 */
package com.ey.advisory.app.services.validation.gstr1a.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AB2csIntra
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredB2csEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredB2csEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		String supplierGstin = (document.getSgstin() != null)
				? document.getSgstin().trim().substring(0, 2) : null;
		String pos = (document.getNewPos() != null)
				? document.getNewPos().trim() : null;

		BigDecimal igstAmt = BigDecimal.ZERO;
		String igsts = (document.getIgstAmt() != null)
				? document.getIgstAmt().trim() : null;
		if (igsts != null && !igsts.isEmpty()) {
			igstAmt = NumberFomatUtil.getBigDecimal(igsts);
		}

		if (supplierGstin != null && pos != null) {
			if (supplierGstin.equalsIgnoreCase(pos)) {
				if (igstAmt.compareTo(BigDecimal.ZERO) > 0) {
					errorLocations.add(GSTConstants.IGST_AMOUNT);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5502",
							" IGST cannot be applied in case of "
									+ "INTRA state supply.",
							location));
					return errors;
				}
			}
		}
		return errors;
	}

}
