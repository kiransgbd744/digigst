package com.ey.advisory.app.services.validation.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class SgstAmount
		implements B2csBusinessRuleValidator<Gstr1AsEnteredB2csEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		BigDecimal sgstAmt = BigDecimal.ZERO;
		String sgsts = (document.getSgstAmt() != null)
				? document.getSgstAmt().trim() : null;
		if (sgsts != null) {
			sgstAmt = NumberFomatUtil.getBigDecimal(sgsts);
		}
		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			if (document.getNewPos() != null
					&& !document.getNewPos().isEmpty()) {
				if (!document.getSgstin().trim().substring(0, 2)
						.equalsIgnoreCase(document.getNewPos().trim())) {
					if (BigDecimal.ZERO.compareTo(sgstAmt) != 0) {
						errorLocations.add(SGST_AMOUNT);
						TransDocProcessingResultLoc location =
								new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER5029",
										"if First two digits of supplier GSTIN "
												+ "are different from New POS then  "
												+ "SGST Amount should be Blank or 0. ",
										location));
					}
				}
			}
		}
		return errors;
	}
}
