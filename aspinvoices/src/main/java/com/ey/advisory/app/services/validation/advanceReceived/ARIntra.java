package com.ey.advisory.app.services.validation.advanceReceived;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredAREntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Anand3.M
 *
 */
public class ARIntra
		implements ARBusinessRuleValidator<Gstr1AsEnteredAREntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredAREntity document,
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
				if (igstAmt.compareTo(BigDecimal.ZERO) !=0) {
					errorLocations.add(GSTConstants.IGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5507",
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