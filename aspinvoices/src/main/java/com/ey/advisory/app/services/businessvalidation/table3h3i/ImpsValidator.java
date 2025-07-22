/**
 * 
 */
package com.ey.advisory.app.services.businessvalidation.table3h3i;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Mahesh.Golla
 *
 */
public class ImpsValidator
		implements BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		String transactionFlag = document.getTransactionFlag();
		if (transactionFlag != null && !transactionFlag.isEmpty()) {
			if (transactionFlag.equalsIgnoreCase(GSTConstants.IMPS)) {

				BigDecimal cgst = BigDecimal.ZERO;
				String cgsts = document.getCentralTaxAmount();
				if (cgsts != null && !cgsts.isEmpty()) {
					cgst = NumberFomatUtil.getBigDecimal(cgsts);
				}

				BigDecimal sgst = BigDecimal.ZERO;
				String sgstss = document.getStateUTTaxAmount();
				if (sgstss != null && !sgstss.isEmpty()) {
					sgst = NumberFomatUtil.getBigDecimal(sgstss);
				}

				BigDecimal amount = cgst.add(sgst);
				if (amount.compareTo(BigDecimal.ZERO) > 0) {
					errorLocations.add(GSTConstants.CGST_AMOUNT);
					errorLocations.add(GSTConstants.SGST_AMOUNT);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0429",
							" In case of IMPS, CGST & SGST/UTGST Tax Amount "
									+ "cannot be applied",
							location));
					return errors;

				}

			}
		}

		return errors;
	}

}
