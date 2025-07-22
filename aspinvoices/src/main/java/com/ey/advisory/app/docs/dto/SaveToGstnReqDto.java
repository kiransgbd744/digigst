/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hemasundar.J
 *
 */
@Data
@NoArgsConstructor
public class SaveToGstnReqDto {

	@Expose
	private String gstin;
	
	@Expose
	private String returnPeriod;
	
	@Expose
	private Long userRequestId;
	
	@Expose
	private String section;
	
	@Expose
	private List<Long> docIds;

	public SaveToGstnReqDto(String gstin, String returnPeriod,
			Long userRequestId, String section) {
		super();
		this.gstin = gstin;
		this.returnPeriod = returnPeriod;
		this.userRequestId = userRequestId;
		this.section = section;
	}

	public SaveToGstnReqDto(String gstin, String returnPeriod,
			Long userRequestId, String section, List<Long> docIds) {
		super();
		this.gstin = gstin;
		this.returnPeriod = returnPeriod;
		this.userRequestId = userRequestId;
		this.section = section;
		this.docIds = docIds;
	}
	
	
	
}
