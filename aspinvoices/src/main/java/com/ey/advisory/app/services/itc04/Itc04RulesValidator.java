/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Itc04RulesValidator<T, K> {

	public abstract Map<K, List<ProcessingResult>> validate(List<T> documents,
			ProcessingContext context);

}