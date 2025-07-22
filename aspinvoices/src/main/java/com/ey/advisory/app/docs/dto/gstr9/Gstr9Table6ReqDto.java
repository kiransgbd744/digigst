package com.ey.advisory.app.docs.dto.gstr9;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table6ReqDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("itc_3b")
	private Gstr9Table6Itc3bReqDto table6Itc3bReqDto;

	@Expose
	@SerializedName("supp_non_rchrg")
	private List<Gstr9Table6SuppNonRchrgReqDto> table6SuppNonRchrgReqDto;
	
	@Expose
	@SerializedName("supp_rchrg_unreg")
	private List<Gstr9Table6SuppRchrgUnRegReqDto> table6SuppRchrgUnRegReqDto;
	
	@Expose
	@SerializedName("supp_rchrg_reg")
	private List<Gstr9Table6SuppRchrgRegReqDto> table6SuppRchrgRegReqDto;
	
	@Expose
	@SerializedName("iog")
	private List<Gstr9Table6IogReqDto> table6IogReqDto;
	
	@Expose
	@SerializedName("ios")
	private Gstr9Table6IosReqDto table6IosReqDto;
	
	@Expose
	@SerializedName("isd")
	private Gstr9Table6IsdReqDto table6IsdReqDto;
	
	@Expose
	@SerializedName("itc_clmd")
	private Gstr9Table6ItcClmdReqDto table6ItcClmdReqDto;
	
	@Expose
	@SerializedName("tran1")
	private Gstr9Table6Trans1ReqDto table6Trans1ReqDto;
	
	@Expose
	@SerializedName("tran2")
	private Gstr9Table6Trans2ReqDto table6Trans2ReqDto;
	
	@Expose
	@SerializedName("other")
	private Gstr9Table6OtherReqDto table6OtherReqDto;
	
	@Expose
	@SerializedName("sub_totalBH")
	private Gstr9Table6SubTotalBhReqDto table6SubTotalBhReqDto;
	
	@Expose
	@SerializedName("sub_totalKM")
	private Gstr9Table6SubTotalKmReqDto table6SubTotalKmReqDto;
	
	@Expose
	@SerializedName("difference")
	private Gstr9Table6DifferenceReqDto table6DifferenceReqDto;
	
	@Expose
	@SerializedName("total_itc_availed")
	private Gstr9Table6TotalItcAvailedReqDto totalItcAvailedReqDto;

}
