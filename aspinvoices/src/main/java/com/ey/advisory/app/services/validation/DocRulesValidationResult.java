package com.ey.advisory.app.services.validation;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

/**
 * This class wraps the validation results. This is the base class which 
 * contains a map of errors/info messages for each document. If any additional
 * information has to be embedded in the results, this class can be extended
 * and used.
 * 
 * @author Sai.Pakanati
 *
 * @param <K> The document key type. For example we can use 'String' as the
 * document key type if we use Invoice key for sales document. The document key
 * uniquely identifies a document.
 */
public class DocRulesValidationResult<K> {
	
	protected Map<K, List<ProcessingResult>> processingResults;

	public DocRulesValidationResult(
				Map<K, List<ProcessingResult>> processingResults) {
		this.processingResults = processingResults;
	}
	
	public Map<K, List<ProcessingResult>> getProcessingResults() {
		return processingResults;
	}
	
	/**
	 * Check if there are any ERROR messages associated with the 
	 * validation.
	 * 
	 * @param key
	 * @return
	 */
	public boolean hasErrors(K key) {	
		List<ProcessingResult> results = processingResults.get(key);
		return results.stream()
			.anyMatch(r -> r.getType() == ProcessingResultType.ERROR);
	}
	
	/**
	 * Check if there are any INFO messages associated with validation.
	 * @param key
	 * @return
	 */
	public boolean hasInfo(K key) {	
		List<ProcessingResult> results = processingResults.get(key);
		return results.stream()
			.anyMatch(r -> r.getType() == ProcessingResultType.INFO);
	}
	

}
