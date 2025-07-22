package com.ey.advisory.app.docs.dto.gstr9;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *This Dto is used for Gstr9 to Serialize and DeSerialize the Data
 */

@Data
public class GetDetailsForGstr9ReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("fp")
	private String fp;

	@Expose
	@SerializedName("table4")
	private Gstr9Table4ReqDto table4ReqDto;

	@Expose
	@SerializedName("table5")
	private Gstr9Table5ReqDto table5ReqDto;

	@Expose
	@SerializedName("table6")
	private Gstr9Table6ReqDto table6ReqDto;
	
	@Expose
	@SerializedName("table7")
	private Gstr9Table7ReqDto table7ReqDto;

	@Expose
	@SerializedName("table8")
	private Gstr9Table8ReqDto table8ReqDto;

	@Expose
	@SerializedName("table9")
	private Gstr9Table9ReqDto table9ReqDto;
	
	@Expose
	@SerializedName("table10")
	private Gstr9Table10ReqDto table10ReqDto;

	@Expose
	@SerializedName("table14")
	private Gstr9Table14ReqDto table14ReqDto;

	@Expose
	@SerializedName("table15")
	private Gstr9Table15ReqDto table15ReqDto;
	
	@Expose
	@SerializedName("table16")
	private Gstr9Table16ReqDto table16ReqDto;

	@Expose
	@SerializedName("table17")
	private Gstr9Table17ReqDto table17ReqDto;

	@Expose
	@SerializedName("table18")
	private Gstr9Table18ReqDto table18ReqDto;
	
/*	@Expose
	@SerializedName("tax_pay")
	private List<Gstr9TaxPayReqDto> taxpayReqDto;

	@Expose
	@SerializedName("tax_paid")
	private Gstr9TaxPaidReqDto taxPaidReqDto;*/
}
