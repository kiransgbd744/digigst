package com.ey.advisory.common;

import java.util.List;

/**
 * 
 * @author Mahesh.Golla
 *
 * @param <T>
 */

public interface BusinessRuleValidator<T> {

	public abstract List<ProcessingResult> validate(
			T document, ProcessingContext context);
}
