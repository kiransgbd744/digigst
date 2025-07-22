package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CashLedgerExcelDownloadDto {

	@Expose
	@SerializedName(value = "srNo")
	private Integer srNo;
	
	@Expose
	@SerializedName(value = "gstn")
	private String gstn;
	
	@Expose
	@SerializedName(value = "dpt_dt")
	private String dptDate="-";
	
	@Expose
	@SerializedName(value  = "dpt_time")
	private String dptTime="-";

	
	@Expose
	@SerializedName(value  = "rpt_dt")
	private String rptDate="-";
	
	@Expose
	@SerializedName(value = "refNo")
	private String referenceNo="-";
	
	@Expose
	@SerializedName(value = "ret_period")
	private String retPeriod="-";
	
	@Expose
	@SerializedName(value = "desc")
    private String description="-";
	
	@Expose
	@SerializedName(value = "tr_typ")
	private String transType="-";
	
	//igst
	 @Expose
	 @SerializedName(value = "igstTaxValue")
	 private String igstTaxValue = "0";
	 
	 @Expose
	 @SerializedName(value = "igstInterestValue")
	 private String igstInterestValue = "0";
	 
	 @Expose
	 @SerializedName(value = "igstPenalty")
	 private String igstPenalty = "0";
	 
	 @Expose
	 @SerializedName(value = "igstFees")
	 private String igstFees = "0";
	 
	 @Expose
	 @SerializedName(value = "igstOther")
	 private String igstOther = "0";
	 
	 @Expose
	 @SerializedName (value = "igstTotal")
	 private String igstTotal = "0";
	//igst end
	 
	 
	 

	 //cgst
	 @Expose
	 @SerializedName(value = "cgstTaxValue")
	 private String cgstTaxValue = "0";
	 
	 @Expose
	 @SerializedName(value = "cgstInterestValue")
	 private String cgstInterestValue = "0";
	 
	 @Expose
	 @SerializedName(value = "cgstPenalty")
	 private String cgstPenalty = "0";
	 
	 @Expose
	 @SerializedName(value = "cgstFees")
	 private String cgstFees = "0";
	 
	 @Expose
	 @SerializedName(value = "cgstOther")
	 private String cgstOther = "0";
	 
	 @Expose
	 @SerializedName (value = "cgstTotal")
	 private String cgstTotal = "0";
		 //cgst ends
	 
	 
	 
	 //sgst
	 @Expose
	 @SerializedName(value = "sgstTaxValue")
	 private String sgstTaxValue = "0";
	 
	 @Expose
	 @SerializedName(value = "sgstInterestValue")
	 private String sgstInterestValue = "0";
	 
	 @Expose
	 @SerializedName(value = "sgstPenalty")
	 private String sgstPenalty = "0";
	 
	 @Expose
	 @SerializedName(value = "sgstFees")
	 private String sgstFees = "0";
	 
	 @Expose
	 @SerializedName(value = "sgstOther")
	 private String sgstOther = "0";
	 
	 @Expose
	 @SerializedName (value = "sgstTotal")
	 private String sgstTotal = "0";

	 //sgst ends
	 

	 
	 //cess
	 @Expose
	 @SerializedName(value = "cessTaxValue")
	 private String cessTaxValue = "0";
	 
	 @Expose
	 @SerializedName(value = "cessInterestValue")
	 private String cessInterestValue = "0";
	 
	 @Expose
	 @SerializedName(value = "cessPenalty")
	 private String cessPenalty = "0";
	 
	 @Expose
	 @SerializedName(value = "cessFees")
	 private String cessFees = "0";
	 
	 @Expose
	 @SerializedName(value = "cessOther")
	 private String cessOther = "0";
	 
	 @Expose
	 @SerializedName (value = "cessTotal")
	 private String cessTotal = "0";
	 //cess ends
	 
	//IGST balence
		 @Expose
		 @SerializedName(value = "igstBalTaxValue")
		 private String igstBalTaxValue = "0";
		 
		 @Expose
		 @SerializedName(value = "igstBalInterestValue")
		 private String igstBalInterestValue = "0";
		 
		 @Expose
		 @SerializedName(value = "igstBalPenalty")
		 private String igstBalPenalty = "0";
		 
		 @Expose
		 @SerializedName(value = "igstBalFees")
		 private String igstBalFees = "0";
		 
		 @Expose
		 @SerializedName(value = "igstBalOther")
		 private String igstBalOther = "0";
		 
		 @Expose
		 @SerializedName (value = "igstBalTotal")
		 private String igstBalTotal = "0";
		 
		 //IGST bal ends
		 
		 
		 //cgst Bal
		 @Expose
		 @SerializedName(value = "cgstBalTaxValue")
		 private String cgstBalTaxValue = "0";
		 
		 @Expose
		 @SerializedName(value = "cgstBalInterestValue")
		 private String cgstBalInterestValue = "0";
		 
		 @Expose
		 @SerializedName(value = "cgstBalPenalty")
		 private String cgstBalPenalty = "0";
		 
		 @Expose
		 @SerializedName(value = "cgstBalFees")
		 private String cgstBalFees = "0";
		 
		 @Expose
		 @SerializedName(value = "cgstBalOther")
		 private String cgstBalOther = "0";
		 
		 @Expose
		 @SerializedName (value = "cgstBalTotal")
		 private String cgstBalTotal = "0";
		 //cgst bal ends
		 
		  //sgst bal
		 @Expose
		 @SerializedName(value = "sgstBalTaxValue")
		 private String sgstBalTaxValue = "0";
		 
		 @Expose
		 @SerializedName(value = "sgstBalInterestValue")
		 private String sgstBalInterestValue = "0";
		 
		 @Expose
		 @SerializedName(value = "sgstBalPenalty")
		 private String sgstBalPenalty = "0";
		 
		 @Expose
		 @SerializedName(value = "sgstBalFees")
		 private String sgstBalFees = "0";
		 
		 @Expose
		 @SerializedName(value = "sgstBalOther")
		 private String sgstBalOther = "0";
		 
		 @Expose
		 @SerializedName (value = "sgstBalTotal")
		 private String sgstBalTotal = "0";
		 //sgst bal ends
		 
		 //cess bal
		 
		 @Expose
		 @SerializedName(value = "cessBalTaxValue")
		 private String cessBalTaxValue = "0";
		 
		 @Expose
		 @SerializedName(value = "cessBalInterestValue")
		 private String cessBalInterestValue = "0";
		 
		 @Expose
		 @SerializedName(value = "cessBalPenalty")
		 private String cessBalPenalty = "0";
		 
		 @Expose
		 @SerializedName(value = "cessBalFees")
		 private String cessBalFees = "0";
		 
		 @Expose
		 @SerializedName(value = "cessBalOther")
		 private String cessBalOther = "0";
		 
		 @Expose
		 @SerializedName (value = "cessBalTotal")
		 private String cessBalTotal = "0";

		 //cess bal ends

	 

}
