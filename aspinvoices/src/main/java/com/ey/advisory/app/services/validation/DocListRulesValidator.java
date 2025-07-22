package com.ey.advisory.app.services.validation;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.Document;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

/**
 * This class is responsible for validating a list of Documents of type T. The
 * method returns a map with the processing results associated with each 
 * document key K.
 *
 * @param <T> The type of documents to be validated.
 * 
 * @param <K> The type of unique key for each document. 
 * 
 * @author Sai.Pakanati 
 */
public interface DocListRulesValidator<T extends Document, K> {
	
	public abstract Map<K, List<ProcessingResult>> validate(
			List<T> documents, ProcessingContext context);

}
