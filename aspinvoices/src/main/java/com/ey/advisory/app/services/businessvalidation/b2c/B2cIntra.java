/**
 * 
 */
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
 * @author Mahesh.Golla
 *
 */
public class B2cIntra implements BusinessRuleValidator<OutwardB2cExcelEntity> {

	@Override
	public List<ProcessingResult> validate(OutwardB2cExcelEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		String supplierGstin = (document.getSgstin() != null)
				? document.getSgstin().substring(0, 2) : document.getSgstin();
		String pos = (document.getPos() != null)
				? document.getPos().substring(0, 2) : document.getPos();
		String sec7OfIgstFlag = document.getSec7OfIgstFlag();

		BigDecimal igstAmt = BigDecimal.ZERO;
		String igsts = (document.getIgstAmt() != null)
				? document.getIgstAmt().trim() : null;
		if (igsts != null && !igsts.isEmpty()) {
			igstAmt = NumberFomatUtil.getBigDecimal(igsts);
		}

		if (supplierGstin != null && pos != null) {
			if (supplierGstin.equalsIgnoreCase(pos)) {
				if (sec7OfIgstFlag != null && !sec7OfIgstFlag.isEmpty()
						&& sec7OfIgstFlag.equalsIgnoreCase(GSTConstants.N)) {
					if (igstAmt.compareTo(BigDecimal.ZERO) > 0) {
						errorLocations.add(GSTConstants.IGST_AMOUNT);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER0277",
										" IGST cannot be applied in case of "
										+ "INTRA state supply.",
										location));
						return errors;

					}

				}
			}

		}

		return errors;
	}

}
