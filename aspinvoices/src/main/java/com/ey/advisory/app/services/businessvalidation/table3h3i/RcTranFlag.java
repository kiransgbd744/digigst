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
public class RcTranFlag
		implements BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		String transactionFlag = document.getTransactionFlag();
		if (transactionFlag != null && !transactionFlag.isEmpty()
				&& transactionFlag.equalsIgnoreCase(GSTConstants.RC)) {
			String supplierGSTINorpan = document.getSupplierGSTINorpan();
			if (supplierGSTINorpan != null && !supplierGSTINorpan.isEmpty()
					&& supplierGSTINorpan.length() == 10) {
				String sec70fIGSTFLAG = document.getSec70fIGSTFLAG();
				if (sec70fIGSTFLAG != null && !sec70fIGSTFLAG.isEmpty()
						&& !sec70fIGSTFLAG.equalsIgnoreCase(GSTConstants.Y)) {
					BigDecimal igst = BigDecimal.ZERO;
					String igstAmt = document.getIntegratedTaxAmount();
					if (igstAmt != null && !igstAmt.isEmpty()) {
						igst = NumberFomatUtil.getBigDecimal(igstAmt);
					}
					if (igst.compareTo(BigDecimal.ZERO) > 0) {
						errorLocations.add(GSTConstants.IGST_AMOUNT);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER0431",
										" In case of RC & PAN is provided & Section7ofIGST "
												+ "is not equal to Y, then IGST tax amount cannot"
												+ " be applied",
										location));
						return errors;

					}
				}

			}
			return errors;
		}

		return errors;
	}
}
