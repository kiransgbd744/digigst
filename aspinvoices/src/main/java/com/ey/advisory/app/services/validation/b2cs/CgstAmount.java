package com.ey.advisory.app.services.validation.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class CgstAmount
		implements B2csBusinessRuleValidator<Gstr1AsEnteredB2csEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		BigDecimal cgstAmt = BigDecimal.ZERO;
		String cgst = (document.getCgstAmt() != null)
				? document.getCgstAmt().trim() : null;
		if (cgst != null) {
			cgstAmt = NumberFomatUtil.getBigDecimal(cgst);
		}

		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			if (document.getNewPos() != null
					&& !document.getNewPos().isEmpty()) {
				if (!document.getSgstin().trim().substring(0, 2)
						.equalsIgnoreCase(document.getNewPos().trim())) {
					if (BigDecimal.ZERO.compareTo(cgstAmt) != 0) {
						errorLocations.add(CGST_AMOUNT);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER5028",
										"If First two digits of supplier GSTIN "
												+ "are different from New POS then  "
												+ "CGST Amount should be Blank or 0. ",
										location));
						return errors;
					}
				}
			}
		}
		return errors;
	}

}
