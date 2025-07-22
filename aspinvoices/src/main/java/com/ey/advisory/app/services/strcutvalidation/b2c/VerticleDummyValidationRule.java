package com.ey.advisory.app.services.strcutvalidation.b2c;

import java.util.List;

import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;
public class VerticleDummyValidationRule implements ValidationRule {
	
	private static final VerticleDummyValidationRule SINGLETON 
				= new VerticleDummyValidationRule();
	
	private static final List<ProcessingResult> LIST = 
			new ImmutableList.Builder<ProcessingResult>().build();
	
	private VerticleDummyValidationRule() {}
	

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		return LIST;
	}
	
	/**
	 * Get a singleton instance of this validation rule.
	 * @return
	 */
	public static VerticleDummyValidationRule getInstance() {
		return SINGLETON;
	}


	

}
