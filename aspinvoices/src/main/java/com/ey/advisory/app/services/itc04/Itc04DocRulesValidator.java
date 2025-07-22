/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import java.util.List;

import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

/**
 * The implementation of this class is responsible for validating the rules for
 * the specified document.
 * 
 * @author Laxmi.Salukuti
 *
 * @param <T>
 */
public interface Itc04DocRulesValidator<T> {

	/**
	 * 
	 * @param document
	 * @param context
	 * @return
	 */
	public abstract List<ProcessingResult> validate(T document,
			ProcessingContext context);

}