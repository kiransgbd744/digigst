package com.ey.advisory.app.gstr3b;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author vishal.verma
 *
 */
public interface Gstr3BStructuralValidations {

	public Map<String, List<ProcessingResult>> Validation(
			List<Object[]> rowData, TabularDataLayout layout);

}
