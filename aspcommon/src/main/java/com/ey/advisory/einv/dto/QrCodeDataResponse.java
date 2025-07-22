/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Khalid1.Khan
 *
 */
@Setter
@Getter
public class QrCodeDataResponse {

	@SerializedName("SellerGstin")
	@Expose
	private String sellerGstin;
	@SerializedName("BuyerGstin")
	@Expose
	private String buyerGstin;
	@SerializedName("DocNo")
	@Expose
	private String docNo;
	@SerializedName("DocTyp")
	@Expose
	private String docTyp;
	
	@SerializedName("DocDt")
	@Expose
	private LocalDate docDt;
	
	@SerializedName("TotInvVal")
	@Expose
	private BigDecimal totInvVal;
	@SerializedName("ItemCnt")
	@Expose
	private Integer itemCnt;
	@SerializedName("MainHsnCode")
	@Expose
	private String mainHsnCode;
	@SerializedName("Irn")
	@Expose
	private String irn;

}
