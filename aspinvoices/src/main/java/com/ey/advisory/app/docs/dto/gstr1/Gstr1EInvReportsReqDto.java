package com.ey.advisory.app.docs.dto.gstr1;

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
 * @author Anand3.M
 *
 */
@Data
public class Gstr1EInvReportsReqDto extends SearchCriteria {

	public Gstr1EInvReportsReqDto() {
		super(SearchTypeConstants.REPORTS_SEARCH);
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId = new ArrayList<>();

	@Expose
	@SerializedName("gstin")
	private List<String> gstin;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("fromPeriod")
	private String fromPeriod;

	@Expose
	@SerializedName("toPeriod")
	private String toPeriod;

	@Expose
	@SerializedName("status")
	private String status;

	@Expose
	@SerializedName("gstr1EinvSections")
	private List<String> gstr1EinvSections;

	@Expose
	@SerializedName("taxPeriodFrom")
	private String returnFrom;

	@Expose
	@SerializedName("taxPeriodTo")
	private String returnTo;

	@Expose
	@SerializedName("tableType")
	protected List<String> tableType;

	@Expose
	@SerializedName("docDateFrom")
	private LocalDate docDateFrom;

	@Expose
	@SerializedName("docDateTo")
	private LocalDate docDateTo;

	@Expose
	@SerializedName("eInvGenerated")
	protected List<String> eInvGenerated;

	@Expose
	@SerializedName("autoDraftedGSTN")
	protected List<String> autoDraftedGSTN;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

}
