/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr2;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingResult;

/**
 * @author Laxmi.Salukuti
 *
 */
public class InwardDocSaveFinalRespDto {

	private List<InwardDocSaveRespDto> savedDocsResp;

	private int totalRecords;

	private int processed;

	private int businessValidationErrors;

	private int structuralValidationErrors;

	private int errors;

	private List<String> jobParamsList;
	
	private List<String> metaDataRevIntJobParamsList;

	Map<String, List<ProcessingResult>> processingResults;

	/**
	 * @return the savedDocsResp
	 */
	public List<InwardDocSaveRespDto> getSavedDocsResp() {
		return savedDocsResp;
	}

	/**
	 * @param savedDocsResp
	 *            the savedDocsResp to set
	 */
	public void setSavedDocsResp(List<InwardDocSaveRespDto> savedDocsResp) {
		this.savedDocsResp = savedDocsResp;
	}

	/**
	 * @return the totalRecords
	 */
	public int getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @param totalRecords
	 *            the totalRecords to set
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
	 * @param processed
	 *            the processed to set
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
	 * @param businessValidationErrors
	 *            the businessValidationErrors to set
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
	 * @param structuralValidationErrors
	 *            the structuralValidationErrors to set
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
	 * @param errors
	 *            the errors to set
	 */
	public void setErrors(int errors) {
		this.errors = errors;
	}

	/**
	 * @return the jobParamsList
	 */
	public List<String> getJobParamsList() {
		return jobParamsList;
	}

	/**
	 * @param jobParamsList
	 *            the jobParamsList to set
	 */
	public void setJobParamsList(List<String> jobParamsList) {
		this.jobParamsList = jobParamsList;
	}

	/**
	 * @return the processingResults
	 */
	public Map<String, List<ProcessingResult>> getProcessingResults() {
		return processingResults;
	}

	/**
	 * @param processingResults
	 *            the processingResults to set
	 */
	public void setProcessingResults(
			Map<String, List<ProcessingResult>> processingResults) {
		this.processingResults = processingResults;
	}

	/**
	 * @return the metaDataRevIntJobParamsList
	 */
	public List<String> getMetaDataRevIntJobParamsList() {
		return metaDataRevIntJobParamsList;
	}

	/**
	 * @param metaDataRevIntJobParamsList the metaDataRevIntJobParamsList to set
	 */
	public void setMetaDataRevIntJobParamsList(
			List<String> metaDataRevIntJobParamsList) {
		this.metaDataRevIntJobParamsList = metaDataRevIntJobParamsList;
	}

}
