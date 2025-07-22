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
public class B2cInter implements BusinessRuleValidator<OutwardB2cExcelEntity> {

	@Override
	public List<ProcessingResult> validate(OutwardB2cExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		String supplierGstin = (document.getSgstin() != null)
				? document.getSgstin().trim().substring(0, 2)
				: document.getSgstin().trim();
		String pos = (document.getPos() != null)
				? document.getPos().trim().substring(0, 2)
				: document.getPos().trim();

		BigDecimal cgstAmt = BigDecimal.ZERO;
		String cgst = (document.getCgstAmt() != null)
				? document.getCgstAmt().trim() : null;
		if (cgst != null && !cgst.isEmpty()) {
			cgstAmt = NumberFomatUtil.getBigDecimal(cgst);
		}
		BigDecimal sgstAmt = BigDecimal.ZERO;
		String sgsts = (document.getSgstAmt() != null)
				? document.getSgstAmt().trim() : null;
		if (sgsts != null && !sgsts.isEmpty()) {
			sgstAmt = NumberFomatUtil.getBigDecimal(sgsts);
		}

		BigDecimal sum = cgstAmt.add(sgstAmt);

		if (supplierGstin != null && pos != null) {
			if (!supplierGstin.equalsIgnoreCase(pos)) {

				if (sum.compareTo(BigDecimal.ZERO) > 0) {
					errorLocations.add(GSTConstants.CGST_AMOUNT);
					errorLocations.add(GSTConstants.SGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0278",
							" CGST / SGST cannot be applied in case of "
									+ "INTER state supply.",
							location));
					return errors;

				}

			}
		}

		return errors;
	}

}
