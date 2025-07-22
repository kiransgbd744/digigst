package com.ey.advisory.app.inward.einvoice;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ravindra V S
 *
 */
@Getter
@Setter
public class EinvoiceNestedDetailedReportRequestDto {
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("gstins")
	private List<String> gstins ;
	
	@Expose
	@SerializedName("fromTaxPeriod")
	private String fromTaxPeriod;
	
	@Expose
	@SerializedName("toTaxPeriod")
	private String toTaxPeriod;
	
	@Expose
	@SerializedName("fromDate")
	private String fromDate;
	
	@Expose
	@SerializedName("toDate")
	private String toDate;
	
	@Expose
	@SerializedName("criteria")
	private String criteria;
	
	@Expose
	@SerializedName("supplyType")
	private List<String> supplyType;
	
	@Expose
	@SerializedName("irnStatus")
	private List<String> irnSts;

	@Expose
	@SerializedName("docNum")
	private String docNum;
	
	@Expose
	@SerializedName("irnNo")
	private String irnNo;
	
	@Expose
	@SerializedName("vendorGstin")
	private String vendorGstin;
	
	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;
	
	@Expose
	@SerializedName("ids")
	private List<Long> ids;
	
	@Expose
	@SerializedName("type")
	private String type;
	
}
