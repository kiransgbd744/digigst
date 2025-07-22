package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CashLedgerPDFDownloadDto {

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
	 private String igstTaxValue="-";
	 
	 @Expose
	 @SerializedName(value = "igstInterestValue")
	 private String igstInterestValue="-";
	 
	 @Expose
	 @SerializedName(value = "igstPenalty")
	 private String igstPenalty="-";
	 
	 @Expose
	 @SerializedName(value = "igstFees")
	 private String igstFees="-";
	 
	 @Expose
	 @SerializedName(value = "igstOther")
	 private String igstOther="-";
	 
	 @Expose
	 @SerializedName (value = "igstTotal")
     private String igstTotal="-";
	//igst end
	 
	 
	 
	
	 //cgst
	 @Expose
	 @SerializedName(value = "cgstTaxValue")
	 private String cgstTaxValue="-" ;
	 
	 @Expose
	 @SerializedName(value = "cgstInterestValue")
	 private String cgstInterestValue="-";
	 
	 @Expose
	 @SerializedName(value = "cgstPenalty")
	 private String cgstPenalty="-";
	 
	 @Expose
	 @SerializedName(value = "cgstFees")
	 private String cgstFees="-" ;
	 
	 @Expose
	 @SerializedName(value = "cgstOther")
	 private String cgstOther="-" ;
	 
	 @Expose
	 @SerializedName (value = "cgstTotal")
     private String cgstTotal="-" ;
	 	 //cgst ends
	 
	 
	 
	 //sgst
	 @Expose
	 @SerializedName(value = "sgstTaxValue")
	 private String sgstTaxValue="-" ;
	 
	 @Expose
	 @SerializedName(value = "sgstInterestValue")
	 private String sgstInterestValue="-" ;
	 
	 @Expose
	 @SerializedName(value = "sgstPenalty")
	 private String sgstPenalty="-" ;
	 
	 @Expose
	 @SerializedName(value = "sgstFees")
	 private String sgstFees="-" ;
	 
	 @Expose
	 @SerializedName(value = "sgstOther")
	 private String sgstOther="-";
	 
	 @Expose
	 @SerializedName (value = "sgstTotal")
     private String sgstTotal="-" ;

	 //sgst ends
	 
	
	 
	 //cess
	 @Expose
	 @SerializedName(value = "cessTaxValue")
	 private String cessTaxValue="-" ;
	 
	 @Expose
	 @SerializedName(value = "cessInterestValue")
	 private String cessInterestValue="-" ;
	 
	 @Expose
	 @SerializedName(value = "cessPenalty")
	 private String cessPenalty="-" ;
	 
	 @Expose
	 @SerializedName(value = "cessFees")
	 private String cessFees="-" ;
	 
	 @Expose
	 @SerializedName(value = "cessOther")
	 private String cessOther="-" ;
	 
	 @Expose
	 @SerializedName (value = "cessTotal")
     private String cessTotal="-" ;
	 //cess ends
	 
	//IGST balence
		 @Expose
		 @SerializedName(value = "igstBalTaxValue")
		 private String igstBalTaxValue ;
		 
		 @Expose
		 @SerializedName(value = "igstBalInterestValue")
		 private String igstBalInterestValue ;
		 
		 @Expose
		 @SerializedName(value = "igstBalPenalty")
		 private String igstBalPenalty ;
		 
		 @Expose
		 @SerializedName(value = "igstBalFees")
		 private String igstBalFees ;
		 
		 @Expose
		 @SerializedName(value = "igstBalOther")
		 private String igstBalOther ;
		 
		 @Expose
		 @SerializedName (value = "igstBalTotal")
	     private String igstBalTotal ;
		 
		 //IGST bal ends
		 
		 
		 //cgst Bal
		 @Expose
		 @SerializedName(value = "cgstBalTaxValue")
		 private String cgstBalTaxValue ;
		 
		 @Expose
		 @SerializedName(value = "cgstBalInterestValue")
		 private String cgstBalInterestValue ;
		 
		 @Expose
		 @SerializedName(value = "cgstBalPenalty")
		 private String cgstBalPenalty ;
		 
		 @Expose
		 @SerializedName(value = "cgstBalFees")
		 private String cgstBalFees ;
		 
		 @Expose
		 @SerializedName(value = "cgstBalOther")
		 private String cgstBalOther ;
		 
		 @Expose
		 @SerializedName (value = "cgstBalTotal")
	     private String cgstBalTotal ;
		 //cgst bal ends
		 
		  //sgst bal
		 @Expose
		 @SerializedName(value = "sgstBalTaxValue")
		 private String sgstBalTaxValue ;
		 
		 @Expose
		 @SerializedName(value = "sgstBalInterestValue")
		 private String sgstBalInterestValue ;
		 
		 @Expose
		 @SerializedName(value = "sgstBalPenalty")
		 private String sgstBalPenalty ;
		 
		 @Expose
		 @SerializedName(value = "sgstBalFees")
		 private String sgstBalFees ;
		 
		 @Expose
		 @SerializedName(value = "sgstBalOther")
		 private String sgstBalOther ;
		 
		 @Expose
		 @SerializedName (value = "sgstBalTotal")
	     private String sgstBalTotal ;
		 //sgst bal ends
		 
		 //cess bal
		 
		 @Expose
		 @SerializedName(value = "cessBalTaxValue")
		 private String cessBalTaxValue ;
		 
		 @Expose
		 @SerializedName(value = "cessBalInterestValue")
		 private String cessBalInterestValue ;
		 
		 @Expose
		 @SerializedName(value = "cessBalPenalty")
		 private String cessBalPenalty ;
		 
		 @Expose
		 @SerializedName(value = "cessBalFees")
		 private String cessBalFees ;
		 
		 @Expose
		 @SerializedName(value = "cessBalOther")
		 private String cessBalOther ;
		 
		 @Expose
		 @SerializedName (value = "cessBalTotal")
	     private String cessBalTotal ;

		 //cess bal ends
	 

}
