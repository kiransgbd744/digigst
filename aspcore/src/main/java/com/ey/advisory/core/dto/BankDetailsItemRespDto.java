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
public class BankDetailsItemRespDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("gstinId")
	private List<Long> gstinIds;

	private Long entityId;

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
	private String bankAddrs;

}
