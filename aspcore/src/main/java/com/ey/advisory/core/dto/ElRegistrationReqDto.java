package com.ey.advisory.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Umesha.M
 *
 */
@Data
public class ElRegistrationReqDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("groupCode")
	private String groupCode;

	@Expose
	@SerializedName("supplierGstin")
	private String supplierGstin;

	@Expose
	@SerializedName("regType")
	private String regType;

	@Expose
	@SerializedName("registeredName")
	private String registeredName;

	@Expose
	@SerializedName("gstnUsername")
	private String gstnUsername;

	@Expose
	@SerializedName("effectiveDate")
	private LocalDate effectiveDate;

	@Expose
	@SerializedName("regEmail")
	private String regEmail;

	@Expose
	@SerializedName("regMobile")
	private String regMobile;

	@Expose
	@SerializedName("primaryAuthEmail")
	private String primaryAuthEmail;

	@Expose
	@SerializedName("secondaryAuthEmail")
	private String secondaryAuthEmail;

	@Expose
	@SerializedName("primaryContactEmail")
	private String primaryContactEmail;

	@Expose
	@SerializedName("secondaryContactEmail")
	private String secondaryContactEmail;

	@Expose
	@SerializedName("bankAccNo")
	private String bankAccNo;

	@Expose
	@SerializedName("turnover")
	private BigDecimal turnover;

	@Expose
	@SerializedName("isDelete")
	private Integer isDelete;

	@Expose
	@SerializedName("address1")
	private String address1;

	@Expose
	@SerializedName("address2")
	private String address2;

	@Expose
	@SerializedName("address3")
	private String address3;
}
