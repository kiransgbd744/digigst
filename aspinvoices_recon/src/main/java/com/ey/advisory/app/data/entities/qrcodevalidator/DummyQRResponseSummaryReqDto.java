package com.ey.advisory.app.data.entities.qrcodevalidator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
public class DummyQRResponseSummaryReqDto {

	@Expose
	@SerializedName("SellerGstin")
	private String sellerGstin;

	@Expose
	@SerializedName("BuyerGstin")
	private String buyerGstin;

	@Expose
	@SerializedName("DocNo")
	private String docNo;

	@Expose
	@SerializedName("DocDt")
	private String docDt;

	@Expose
	@SerializedName("DocTyp")
	private String docType;

	@Expose
	@SerializedName("TotInvVal")
	private String totInvVal;

	@Expose
	@SerializedName("ItemCnt")
	private int itemCnt;

	@Expose
	@SerializedName("File Name")
	private String fileName;

	@Expose
	@SerializedName("MainHsnCode")
	private String mainHsnCode;

	@Expose
	@SerializedName("Irn")
	private String irn;

	@Expose
	@SerializedName("IrnDt")
	private String irnDate;

	@Expose
	@SerializedName("Signature")
	private String signature;

	@Expose
	@SerializedName("SellerGstin Match")
	private String sellerGstinMatch;

	@Expose
	@SerializedName("BuyerGstin Match")
	private String buyerGstinMatch;

	@Expose
	@SerializedName("DocNo Match")
	private String docNoMatch;

	@Expose
	@SerializedName("DocTyp Match")
	private String docTypeMatch;

	@Expose
	@SerializedName("DocDt Match")
	private String docDtMatch;

	@Expose
	@SerializedName("TotInvVal Match")
	private String totInvValMatch;

	@Expose
	@SerializedName("MainHsnCode Match")
	private String mainHsnCodeMatch;

	@Expose
	@SerializedName("Irn Match")
	private String irnMatch;

	@Expose
	@SerializedName("declaration")
	private String declaration;
}
