package com.ey.advisory.app.docs.dto;

import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Setter
@Getter
public class GetApprovalStatusReqDto  extends SearchCriteria{
	
	public GetApprovalStatusReqDto(String searchType) {
		super(searchType);
	}

	@Expose
	@SerializedName("retPeriod")
	private String returnPeriod;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("docType")
	private String docType;

}