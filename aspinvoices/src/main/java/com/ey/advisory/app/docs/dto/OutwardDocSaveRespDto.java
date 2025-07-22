package com.ey.advisory.app.docs.dto;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingResult;

/**
 * This class responsible for transferring response data required for UI and SCI 
 * and for Raw File Processing 
 * @author Mohana.Dasari
 *
 */
public class OutwardDocSaveRespDto {

	private List<DocSaveRespDto> savedDocsResp;
	
	private int totalRecords;
	
	private int processed;
	
	private int businessValidationErrors;
	
	private int structuralValidationErrors;
	
	private int errors;
	
	Map<String, List<ProcessingResult>> processingResults;

	/**
	 * @return the savedDocsResp
	 */
	public List<DocSaveRespDto> getSavedDocsResp() {
		return savedDocsResp;
	}

	/**
	 * @param savedDocsResp the savedDocsResp to set
	 */
	public void setSavedDocsResp(List<DocSaveRespDto> savedDocsResp) {
		this.savedDocsResp = savedDocsResp;
	}

	/**
	 * @return the totalRecords
	 */
	public int getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @param totalRecords the totalRecords to set
	 */
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * @return the processed
	 */
	public int getProcessed() {
		return processed;
	}

	/**
	 * @param processed the processed to set
	 */
	public void setProcessed(int processed) {
		this.processed = processed;
	}

	/**
	 * @return the businessValidationErrors
	 */
	public int getBusinessValidationErrors() {
		return businessValidationErrors;
	}

	/**
	 * @param businessValidationErrors the businessValidationErrors to set
	 */
	public void setBusinessValidationErrors(int businessValidationErrors) {
		this.businessValidationErrors = businessValidationErrors;
	}

	/**
	 * @return the structuralValidationErrors
	 */
	public int getStructuralValidationErrors() {
		return structuralValidationErrors;
	}

	/**
	 * @param structuralValidationErrors the structuralValidationErrors to set
	 */
	public void setStructuralValidationErrors(int structuralValidationErrors) {
		this.structuralValidationErrors = structuralValidationErrors;
	}

	/**
	 * @return the errors
	 */
	public int getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(int errors) {
		this.errors = errors;
	}

	public Map<String, List<ProcessingResult>> getProcessingResults() {
		return processingResults;
	}

	public void setProcessingResults(
			Map<String, List<ProcessingResult>> processingResults) {
		this.processingResults = processingResults;
	}
}
