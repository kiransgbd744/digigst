package com.ey.advisory.app.data.services.qrvspdf;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class QRValidatorRespDto {

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
	private String docDt;

	@SerializedName("TotInvVal")
	@Expose
	private BigDecimal totInvVal;

	@SerializedName("ItemCnt")
	@Expose
	private int itemCnt;

	@SerializedName("MainHsnCode")
	@Expose
	private String mainHsnCode;

	@SerializedName("Irn")
	@Expose
	private String irn;

	@SerializedName("IrnDt")
	@Expose
	private String irnDt;

	@SerializedName("File Name")
	@Expose
	private String fileName;

	@SerializedName("Signature")
	@Expose
	private String signature;

	@SerializedName("SellerGstin Match")
	@Expose
	private String sellerGstinMatch;

	@SerializedName("BuyerGstin Match")
	@Expose
	private String buyerGstinMatch;

	@SerializedName("DocNo Match")
	@Expose
	private String docNoMatch;

	@SerializedName("DocTyp Match")
	@Expose
	private String docTypMatch;

	@SerializedName("DocDt Match")
	@Expose
	private String docDtMatch;

	@SerializedName("TotInvVal Match")
	@Expose
	private String totInvValMatch;

	@SerializedName("MainHsnCode Match")
	@Expose
	private String mainHsnCodeMatch;

	@SerializedName("Irn Match")
	@Expose
	private String irnMatch;

	@Expose
	@SerializedName("declaration")
	private String declaration;
	
	@Expose
	@SerializedName("message")
	private String message;
	
}
