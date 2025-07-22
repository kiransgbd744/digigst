package com.ey.advisory.app.services.strcutvalidation.outward;

import java.util.List;

import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public interface ValidationRule {

	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row, 
			                                 TabularDataLayout layout);
}
