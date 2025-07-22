package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetIrnDtlsRespDto {

	@Expose
	@SerializedName("docNum")
	public String docNum;

	@Expose
	@SerializedName("docDt")
	public String docDt;

	@Expose
	@SerializedName("docType")
	public String docTyp;
	
	@Expose
	@SerializedName("supplyType")
	public String suppTyp;
	
	@Expose
	@SerializedName("totInvAmt")
	public BigDecimal totInvAmt ;

	@Expose
	@SerializedName(value = "irn",alternate={"Irn"})
	public  String irn;
	
	@Expose
	@SerializedName(value = "irnStatus", alternate = {"Status"})
	public String irnSts;
	
	@Expose
	@SerializedName(value = "ackNo", alternate = {"AckNo"})
	public Long ackNo;

	@Expose
	@SerializedName(value = "ackDt",alternate={"AckDt"} )
	public String ackDt;
	
	@Expose
	@SerializedName(value = "ewbNo", alternate = {"EwbNo"})
	public Long ewbNo;

	@Expose
	@SerializedName(value = "ewbDt", alternate = {"EwbDt"})
	public String ewbDt;
	
	@Expose
	@SerializedName(value = "cnldt", alternate = {"Cnldt"})
	public String cnlDt;
	
	@Expose
	@SerializedName("EwbValidTill")
	public String ewbVald;
	
	@Expose
	@SerializedName("Remarks")
	public String rmrks;

	@Expose
	@SerializedName("CnlRsn")
	public String cnlRsn;
	
	@Expose
	@SerializedName("CnlRem")
	public String cnlRem;

}
