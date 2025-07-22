package com.ey.advisory.app.services.validation.gstr1a.advanceReceived;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class Gstr1AArTransType
		implements ARBusinessRuleValidator<Gstr1AAsEnteredAREntity> {
	private static final List<String> IMPORTS = ImmutableList.of("Z", "ZL65",
			"z", "zl65", "Zl65", "zL65");

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredAREntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		String transType = document.getTransType();

		BigDecimal cgstAmount = BigDecimal.ZERO;
		String cgst = (document.getCgstAmt() != null)
				? document.getCgstAmt().trim() : null;
		if (cgst != null && !cgst.isEmpty()) {
			cgstAmount = NumberFomatUtil.getBigDecimal(cgst);
		}

		BigDecimal sgstAmount = BigDecimal.ZERO;
		String sgsts = (document.getSgstAmt() != null)
				? document.getSgstAmt().trim() : null;
		if (sgsts != null && !sgsts.isEmpty()) {
			sgstAmount = NumberFomatUtil.getBigDecimal(sgsts);
		}
		BigDecimal sgstCgstTotal = sgstAmount.add(cgstAmount);

		if (IMPORTS.contains(transType)) {
			if (sgstCgstTotal.compareTo(BigDecimal.ZERO) > 0) {
				errorLocations.add(GSTConstants.CGST_AMOUNT);
				errorLocations.add(GSTConstants.SGST_AMOUNT);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5517",
						"CGST / SGST cannot be applied in case of transaction "
								+ "type is Z or ZL65.",
						location));
				return errors;
			}
		}
		return errors;
	}

}
