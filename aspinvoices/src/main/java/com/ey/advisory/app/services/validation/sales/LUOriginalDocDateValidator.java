package com.ey.advisory.app.services.validation.sales;

import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

public class LUOriginalDocDateValidator 
implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		// TODO Auto-generated method stub
		return null;
	} 


}
