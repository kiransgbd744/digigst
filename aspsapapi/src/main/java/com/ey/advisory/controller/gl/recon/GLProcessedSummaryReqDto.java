package com.ey.advisory.controller.gl.recon;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GLProcessedSummaryReqDto extends SearchCriteria{
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
    
	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;
	
    @Expose
	@SerializedName("taxPeriodTo")
    private String taxPeriodTo;
        
    @Expose
	@SerializedName("gstins")
	private List<String> gstins = new ArrayList<>();
    
    @Expose
	@SerializedName("transType")
	private List<String> transType = new ArrayList<>();
    
    @Expose
   	@SerializedName("transactionType")
   	private String transactionType ;
    
    public GLProcessedSummaryReqDto() {
		super(SearchTypeConstants.PROCESSED_RECORDS_SEARCH);
	}

}
