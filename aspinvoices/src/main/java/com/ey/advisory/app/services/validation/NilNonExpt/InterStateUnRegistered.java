/*package com.ey.advisory.app.services.validation.NilNonExpt;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr1NilDetailsEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

public class InterStateUnRegistered implements B2csBusinessRuleValidator<Gstr1NilDetailsEntity> {
	 private static DecimalFormat df = new DecimalFormat("0.00");
	@Override
	public List<ProcessingResult> validate(Gstr1NilDetailsEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if(document.getInterStateUnregistered()!=null){
			document.setInterStateUnregistered(new BigDecimal(df.format(document.getInterStateUnregistered())));
		}
		return errors;
	}

}
*/