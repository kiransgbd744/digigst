package com.ey.advisory.app.services.validation;

import java.util.List;
import com.ey.advisory.common.ProcessingContext;

/**
 * This is the service responsible for validating a list of Documents of type T
 * and having a document key of type K (Mostly we use a string as a document 
 * key to uniquely identify a document). The implementation of this interface
 * is responsible for validating a list of documents efficiently. This method
 * returns a DocRulesValidationResult object which contains the validation
 * errors/information messages related to each document.
 * 
 * @author Sai.Pakanati
 *
 */
public interface DocRulesValidatorService<T, K> {
	
	public abstract  DocRulesValidationResult<K> validate(
				List<T> documents, ProcessingContext context);

}
