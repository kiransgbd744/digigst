package com.ey.advisory.core.dto;

import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Umesha.M
 *
 */
@Data
public class BankDetailsReqDto {

	@Expose
	@SerializedName("groupCode")
	private String groupCode;

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("groupId")
	private Long groupId;

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("gstinId")
	private List<Long> gstinId;

	@Expose
	@SerializedName("bankAcc")
	private String bankAcc;

	@Expose
	@SerializedName("ifscCode")
	private String ifscCode;

	@Expose
	@SerializedName("beneficiary")
	private String beneficiary;

	@Expose
	@SerializedName("paymentDueDate")
	private LocalDate paymentDueDate;

	@Expose
	@SerializedName("paymentTerms")
	private String paymentTerms;

	@Expose
	@SerializedName("paymentInstruction")
	private String paymentInstruction;
	
	@Expose
	@SerializedName("bankName")
	private String bankName;
	
	@Expose
	@SerializedName("bankAddrs")
	private String bankAddress;

}
