/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Arun.KA
 *
 */

@Getter
@Setter
@ToString
public class GetCounterPartyResDto {
	
	@SerializedName("ewbNo")
	@Expose
	private Long ewbNo;
	@SerializedName("ewayBillDate")
	@Expose
	private String ewayBillDate;
	@SerializedName("genMode")
	@Expose
	private String genMode;
	@SerializedName("genGstin")
	@Expose
	private String genGstin;
	@SerializedName("docNo")
	@Expose
	private String docNo;
	@SerializedName("docDate")
	@Expose
	private String docDate;
	@SerializedName("fromgstin")
	@Expose
	private String fromgstin;
	@SerializedName("fromTradename")
	@Expose
	private String fromTradename;
	@SerializedName("togstin")
	@Expose
	private String togstin;
	@SerializedName("toTradename")
	@Expose
	private String toTradename;
	@SerializedName("totInvValue")
	@Expose
	private BigDecimal totInvValue;
	@SerializedName("hsndesc")
	@Expose
	private String hsndesc;
	@SerializedName("status")
	@Expose
	private String status;
	@SerializedName("rejectStatus")
	@Expose
	private String rejectStatus;
	@SerializedName("fromGstin")
	@Expose
	private String fromGstin;
	@SerializedName("fromTradeName")
	@Expose
	private String fromTradeName;
	@SerializedName("toGstin")
	@Expose
	private String toGstin;
	@SerializedName("toTradeName")
	@Expose
	private String toTradeName;
	@SerializedName("hsnCode")
	@Expose
	private Integer hsnCode;
	@SerializedName("hsnDesc")
	@Expose
	private String hsnDesc;

}
