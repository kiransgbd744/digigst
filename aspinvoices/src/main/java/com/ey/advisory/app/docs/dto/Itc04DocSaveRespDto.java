/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.ProcessingResult;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class Itc04DocSaveRespDto {
	


	private List<Itc04DocDetailsSaveRespDto> savedDocsResp;
	
	private int totalRecords;
	
	private int processed;
	
	private int businessValidationErrors;
	
	private int structuralValidationErrors;
	
	private int errors;
	
	private List<String> jobParamsList;
	
	private List<String> metaDataRevIntJobParamsList;
	
	Map<String, List<ProcessingResult>> processingResults;
}
