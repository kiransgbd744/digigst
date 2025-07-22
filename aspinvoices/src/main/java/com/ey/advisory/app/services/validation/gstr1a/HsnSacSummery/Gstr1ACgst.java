package com.ey.advisory.app.services.validation.gstr1a.HsnSacSummery;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AHsnFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

public class Gstr1ACgst
		implements B2csBusinessRuleValidator<Gstr1AHsnFileUploadEntity> {

	private static DecimalFormat df = new DecimalFormat("0.00");

	@Override
	public List<ProcessingResult> validate(Gstr1AHsnFileUploadEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getCgst() != null) {

			document.setCgst(new BigDecimal(df.format(document.getCgst())));

		}
		return errors;
	}
}
