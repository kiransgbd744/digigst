package com.ey.advisory.app.docs.dto.gstr7;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */
@Data
public class SaveGstr7 {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("fp")
	private String taxperiod;	
	
	@Expose
	@SerializedName("tds")
	private List<Gstr7TdsDto> tdsInvoice;
	
	@Expose
	@SerializedName("tdsa")
	private List<Gstr7TdsaDto> tdsaInvoice;
	
	@Expose
	@SerializedName("status_cd")
	private String statuscode;

	@Expose
	@SerializedName("form_typ")
	private String formType;
	
	@Expose
	@SerializedName("action")
	private String action;
	
	@Expose
	@SerializedName("gt")
	private BigDecimal gt;

	@Expose
	@SerializedName("cur_gt")
	private BigDecimal cur_gt;
	
	@Expose
	@SerializedName("error_report")
	Gstr7ErrorReport gstr7ErrorReport;
	
	@Expose
	@SerializedName("req_time")
	private String reqTime;

}
