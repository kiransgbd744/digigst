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
public class InwardIntra
		implements BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		String supplierGstin = document.getSupplierGSTINorpan();
		String pos = document.getPos();
		String sec7OfIgstFlag = document.getSec70fIGSTFLAG();

		BigDecimal igstAmt = BigDecimal.ZERO;
		String igst = document.getIntegratedTaxAmount();
		if (igst != null && !igst.isEmpty()) {
			igstAmt = NumberFomatUtil.getBigDecimal(igst);
		}

		if (supplierGstin != null && pos != null) {
			if (supplierGstin.length() == 15) {
				String firstTwoDigitsOfSgtin = supplierGstin.substring(0,2);
				if (firstTwoDigitsOfSgtin.equalsIgnoreCase(pos)) {
					if (sec7OfIgstFlag != null && !sec7OfIgstFlag.isEmpty()
							&& sec7OfIgstFlag
									.equalsIgnoreCase(GSTConstants.N)) {
						if (igstAmt.compareTo(BigDecimal.ZERO) > 0) {
							errorLocations.add(GSTConstants.IGST_AMOUNT);
							TransDocProcessingResultLoc location = 
									new TransDocProcessingResultLoc(
									null, errorLocations.toArray());
							errors.add(
									new ProcessingResult(APP_VALIDATION,
											"ER0427",
											" IGST cannot be applied in case of "
													+ "INTRA state supply.",
											location));
							return errors;
						}

					}

				}
			}

			/*
			 * if (supplierGstin.length() == 10) { if (sec7OfIgstFlag != null &&
			 * !sec7OfIgstFlag.isEmpty() &&
			 * sec7OfIgstFlag.equalsIgnoreCase(GSTConstants.N)) { if
			 * (igstAmt.compareTo(BigDecimal.ZERO) > 0) {
			 * errorLocations.add(GSTConstants.IGST_AMOUNT);
			 * TransDocProcessingResultLoc location = new
			 * TransDocProcessingResultLoc( null, errorLocations.toArray());
			 * errors.add( new ProcessingResult(APP_VALIDATION, "ER0427",
			 * " IGST cannot be applied in case of " + "INTRA state supply.",
			 * location)); return errors; }
			 * 
			 * } }
			 * 
			 */
		}

		return errors;
	}

}
