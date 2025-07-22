package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class LedgerItcReclaimBalanceAmts {
	
	 @Expose
	 @SerializedName(value = "igst")
	 private BigDecimal igst = BigDecimal.ZERO;
	 
	 @Expose
	 @SerializedName(value = "cgst")
	 private BigDecimal cgst =  BigDecimal.ZERO;
	 
	 @Expose
	 @SerializedName(value = "sgst")
	 private BigDecimal sgst =  BigDecimal.ZERO;
	 
	 @Expose
	 @SerializedName(value = "cess")
	 private BigDecimal cess =  BigDecimal.ZERO;
	 
	 @Expose
	 @SerializedName(value = "desc")
	 private String desc;
	 
	 @Expose
	 @SerializedName(value = "trancd")
	 private BigInteger trancd;

}
