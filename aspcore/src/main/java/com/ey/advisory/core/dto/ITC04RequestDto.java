package com.ey.advisory.core.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ITC04RequestDto extends SearchCriteria {

	public ITC04RequestDto(String searchType) {
		super(searchType);
		// TODO Auto-generated constructor stub
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId = new ArrayList<>();

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("action")
	private String action;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs = new HashMap<>();

	@Expose
	@SerializedName("type")
	private String type;

	@Expose
	@SerializedName("docKey")
	private List<String> docKey = new ArrayList<>();

	@Expose
	@SerializedName("tableType")
	private String tableType;

	@Expose
	@SerializedName("screenType")
	private String screenType;

	@Expose
	@SerializedName("gstinDeductor")
	private List<String> gstinDeductor = new ArrayList<>();

	@Expose
	@SerializedName("isDigigst")
	private Boolean isDigigst;

	/**
	 * @param searchType
	 * @param taxPeriod
	 */
	public ITC04RequestDto(String searchType, String taxPeriod) {
		super(searchType);
		this.taxPeriod = taxPeriod;
	}

	// These were add for ITC04 Stocking Tracking.
	@Expose
	@SerializedName("requestType")
	private String requestType;

	@Expose
	@SerializedName("fy")
	private String fy;

	@Expose
	@SerializedName("fromReturnPeriod")
	private String fromReturnPeriod;

	@Expose
	@SerializedName("toReturnPeriod")
	private String toReturnPeriod;

	@Expose
	@SerializedName("fromChallanDate")
	private String fromChallanDate;

	@Expose
	@SerializedName("toChallanDate")
	private String toChallanDate;
	
	@Expose
	@SerializedName("remark")
	private String remark;
	
	@Expose
	@SerializedName("comment")
	private String comment;

}
