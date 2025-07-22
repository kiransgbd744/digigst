package com.ey.advisory.app.common;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Saif.S
 *
 */
@Getter
@Setter
@ToString
public class GstrEmailDetailsDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("returnType")
	@Expose
	private String returnType;

	@SerializedName("fromTaxPeriod")
	@Expose
	private String fromTaxPeriod;

	@SerializedName("toTaxPeriod")
	@Expose
	private String toTaxPeriod;

	@SerializedName("fy")
	@Expose
	private String fy;

	@SerializedName("entityName")
	@Expose
	private String entityName;

	@SerializedName("primaryEmail")
	@Expose
	private String primaryEmail;

	@SerializedName("secondaryEmails")
	@Expose
	private List<String> secondaryEmail;

	@SerializedName("notfnCode")
	@Expose
	private String notfnCode;

	@SerializedName("gstins")
	@Expose
	private List<GstinWiseEmailDto> gstins;
	
	private boolean isSecondEmailEligible;
	
	private String activeGstins;
	
	private Long entityId;
}