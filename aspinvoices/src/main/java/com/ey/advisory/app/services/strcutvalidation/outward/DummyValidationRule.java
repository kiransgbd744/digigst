package com.ey.advisory.app.services.strcutvalidation.outward;

import java.util.List;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
public class DummyValidationRule implements ValidationRule {
	
	private static final DummyValidationRule SINGLETON 
				= new DummyValidationRule();
	
	private static final List<ProcessingResult> LIST = 
			new ImmutableList.Builder<ProcessingResult>().build();
	
	private DummyValidationRule() {}
	

	@Override
	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row,
			TabularDataLayout layout) {
		return LIST;
	}
	
	/**
	 * Get a singleton instance of this validation rule.
	 * @return
	 */
	public static DummyValidationRule getInstance() {
		return SINGLETON;
	}

}

