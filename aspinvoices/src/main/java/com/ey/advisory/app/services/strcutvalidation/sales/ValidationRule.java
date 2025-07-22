package com.ey.advisory.app.services.strcutvalidation.sales;

import java.util.List;

import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public interface ValidationRule {

	public List<ProcessingResult> isValid(Object obj, Object[] row, 
			                                 TabularDataLayout layout);
}
