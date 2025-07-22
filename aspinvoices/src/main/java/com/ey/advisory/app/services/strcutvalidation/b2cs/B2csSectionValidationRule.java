package com.ey.advisory.app.services.strcutvalidation.b2cs;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class B2csSectionValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		// TODO Auto-generated method stub
		
		List<ProcessingResult> errors = new ArrayList<>();
		
		return errors;
	}

}
