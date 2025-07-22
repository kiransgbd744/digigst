package com.ey.advisory.app.docs.dto.gstr1;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr7ProcessedRecordsRespDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("returnPerid")
	private String returnPerid;

	@Expose
	@SerializedName("totalCount")
	private int totalCountAsp;

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("notSentCount")
	private int notSentCount;
	@Expose
	@SerializedName("savedCount")
	private int savedCount;
	@Expose
	@SerializedName("notSavedCount")
	private int notSavedCount;
	@Expose
	@SerializedName("errorCount")
	private int errorCount;

	@Expose
	@SerializedName("state")
	private String state;

	@Expose
	@SerializedName("saveStatus")
	private String saveStatus;

	@Expose
	@SerializedName("saveDateTime")
	private String saveDateTime;

	@Expose
	@SerializedName("fileStatus")
	private String fileStatus;

	@Expose
	@SerializedName("fileDateTime")
	private String fileDateTime;

	@Expose
	@SerializedName("count")
	private BigInteger count;

	@Expose
	@SerializedName("igst")
	private BigDecimal igst;

	@Expose
	@SerializedName("cgst")
	private BigDecimal cgst;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgst;

	@Expose
	@SerializedName("totalAmount")
	private BigDecimal totalAmount;

	@Expose
	@SerializedName("section")
	private String section;

}
