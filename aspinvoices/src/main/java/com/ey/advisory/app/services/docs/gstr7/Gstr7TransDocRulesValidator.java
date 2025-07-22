package com.ey.advisory.app.services.docs.gstr7;

import java.util.List;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
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
public interface Gstr7TransDocRulesValidator<T extends Gstr7TransDocHeaderEntity> {
	
	/**
	 * 
	 * @param document
	 * @param context
	 * @return
	 */
	public abstract List<ProcessingResult> validate(
				T document, ProcessingContext context);

}
