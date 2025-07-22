package com.ey.advisory.common;

import java.util.List;

/**
 * This is a decorator interface over the validator.
 * 
 * @author 
 *
 * @param <T>
 */
public interface DocRulesValidatorChain<T> {
	/**
	 * 
	 * @param document
	 * @param context
	 * @return
	 */
	public abstract List<ProcessingResult> validate(
				T document, ProcessingContext context);
	
}
