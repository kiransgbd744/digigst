package com.ey.advisory.core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
/**
 * 
 * @author Balakrishna.S
 *
 */


@Data
public class EinvoiceProcessedReqDto extends SearchCriteria{

	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId = new ArrayList<>();
    
	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;
    @Expose
	@SerializedName("taxPeriodTo")
    private String taxPeriodTo;
    
    @Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;
    
    @Expose
	@SerializedName("tableType")
	private List<String> tableType = new ArrayList<>();
    @Expose
	@SerializedName("docFromDate")
	private LocalDate docFromDate;
	
    @Expose
	@SerializedName("docToDate")
	private LocalDate docToDate;
   
    @Expose
	@SerializedName("EINVGenerated")
	private List<String> EINVGenerated = new ArrayList<>();
	@Expose
	@SerializedName("EWBGenerated")
	private List<String> EWBGenerated = new ArrayList<>();
    
	@Expose
	@SerializedName("AutoDraftedGSTN")
	private List<String> AutoDraftedGSTN = new ArrayList<>();
    
	public EinvoiceProcessedReqDto() {
		super(SearchTypeConstants.PROCESSED_RECORDS_SEARCH);
	}
	
}
