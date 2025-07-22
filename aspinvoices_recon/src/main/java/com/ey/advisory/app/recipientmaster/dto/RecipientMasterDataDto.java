package com.ey.advisory.app.recipientmaster.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Rajesh N K
 *
 */
@Data
public class RecipientMasterDataDto {

	@Expose
	@SerializedName("invoiceKey")
	private String invoiceKey;

	@Expose
	@SerializedName("recipientPAN")
	private String recipientPAN;

	@Expose
	@SerializedName("recipientGstin")
	private String recipientGstin;

	@Expose
	@SerializedName("recipientPrimEmailId")
	private String recipientPrimEmailId;

	@Expose
	@SerializedName("recipientEmailId2")
	private String recipientEmailId2;

	@Expose
	@SerializedName("recipientEmailId3")
	private String recipientEmailId3;

	@Expose
	@SerializedName("recipientEmailId4")
	private String recipientEmailId4;

	@Expose
	@SerializedName("recipientEmailId5")
	private String recipientEmailId5;

	@Expose
	@SerializedName("isGetGstr2AEmail")
	private String isGetGstr2AEmail;

	@Expose
	@SerializedName("isGetGstr2BEmail")
	private String isGetGstr2BEmail;

	@Expose
	@SerializedName("isRetCompStatusEmail")
	private String isRetCompStatusEmail;

	@Expose
	@SerializedName("cceEmailId1")
	private String cceEmailId1;
	
	@Expose
	@SerializedName("cceEmailId2")
	private String cceEmailId2;
	
	@Expose
	@SerializedName("cceEmailId3")
	private String cceEmailId3;
	
	@Expose
	@SerializedName("cceEmailId4")
	private String cceEmailId4;
	
	@Expose
	@SerializedName("cceEmailId5")
	private String cceEmailId5;
	
	@Expose
	@SerializedName("isDrc01b")
	private String isDrc01b;
	
	@Expose
	@SerializedName("isDrc01c")
	private String isDrc01c;
}
