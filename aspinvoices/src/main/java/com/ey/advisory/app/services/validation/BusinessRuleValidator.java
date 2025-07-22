package com.ey.advisory.app.services.validation;

import java.util.List;

import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

public interface BusinessRuleValidator<T> {

	public abstract List<ProcessingResult> validate(
			T document, ProcessingContext context);
}
