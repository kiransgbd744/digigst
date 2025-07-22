package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Sri Bhavya
 *
 */
@Data
public class SaveGstr6 {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("fp")
	private String taxperiod;	

	@Expose
	@SerializedName("b2b")
	private List<Gstr6B2bDto> b2bInvoice;

	@Expose
	@SerializedName("b2ba")
	private List<Gstr6B2baDto> b2baInvoice;

	@Expose
	@SerializedName("cdn")
	private List<Gstr6CdnDto> cdnInvoice;

	@Expose
	@SerializedName("cdna")
	private List<Gstr6CdnaDto> cdnaInvoice;

	@Expose
	@SerializedName("isd")
	private Gstr6IsdDetailsDto isdInvoice;

	@Expose
	@SerializedName("isda")
	private List<Gstr6IsdDetailsDto> isdaInvoice;
	
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
	Gstr6ErrorReport gstr6ErrorReport;
	
}
