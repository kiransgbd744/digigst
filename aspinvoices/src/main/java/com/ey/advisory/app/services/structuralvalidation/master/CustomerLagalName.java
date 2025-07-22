package com.ey.advisory.app.services.structuralvalidation.master;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class CustomerLagalName implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();
		return errors;
	}

}
