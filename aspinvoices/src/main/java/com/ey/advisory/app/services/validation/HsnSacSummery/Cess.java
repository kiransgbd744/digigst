package com.ey.advisory.app.services.validation.HsnSacSummery;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr1HsnFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

public class Cess implements B2csBusinessRuleValidator<Gstr1HsnFileUploadEntity> {

	 private static DecimalFormat df = new DecimalFormat("0.00");
		@Override
		public List<ProcessingResult> validate(Gstr1HsnFileUploadEntity document, ProcessingContext context) {
			
			List<ProcessingResult> errors = new ArrayList<>();
			if(document.getCess()!=null){
				
			document.setCess(new BigDecimal(df.format(document.getCess())));
				
		}
				return errors;
			}
		}

