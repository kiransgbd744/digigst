package com.ey.advisory.app.docs.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.core.search.SearchResult;

/**
 * ErrorCorrectionRespDto class is responsible for transferring error invoices
 * response data from server to UI
 *
 * @author Mohana.Dasari
 */
@Component
public class DocSearchRespDto extends SearchResult<DocumentDto> {

	public DocSearchRespDto(List<DocumentDto> result) {
		super(result);
	}
 	
	/**
	 * @return the documents
	 */
	public List<? extends DocumentDto> getDocuments() {
		return result;
	}


}
