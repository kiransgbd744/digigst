package com.ey.advisory.app.services.validation;

import java.util.List;

import com.ey.advisory.common.Document;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

/**
 * The implementation of this class is responsible for validating the rules
 * for the specified document.
 * 
 * @author Sai.Pakanati
 *
 * @param <T>
 */
public interface DocRulesValidator<T extends Document> {
	
	/**
	 * 
	 * @param document
	 * @param context
	 * @return
	 */
	public abstract List<ProcessingResult> validate(
				T document, ProcessingContext context);

}
