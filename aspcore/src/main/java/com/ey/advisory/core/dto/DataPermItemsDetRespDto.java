package com.ey.advisory.core.dto;

import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Umesha.M
 *
 */
@Setter
@Getter
@ToString
public class DataPermItemsDetRespDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("userName")
	private String userName;

	@Expose
	@SerializedName("email")
	private String email;
	
	@Expose
	@SerializedName("gstinIds")
	private List<Long> gstinId;

	@Expose
	@SerializedName("profitCenter")
	private List<Long> profitCenter;

	@Expose
	@SerializedName("profitCenter2")
	private List<Long> profitCenter2;

	@Expose
	@SerializedName("plant")
	private List<Long> plant;

	@Expose
	@SerializedName("division")
	private List<Long> division;

	@Expose
	@SerializedName("subDivision")
	private List<Long> subDivision;

	@Expose
	@SerializedName("location")
	private List<Long> location;

	@Expose
	@SerializedName("salesOrg")
	private List<Long> salesOrg;

	@Expose
	@SerializedName("purchOrg")
	private List<Long> purchOrg;

	@Expose
	@SerializedName("distChannel")
	private List<Long> distChannel;

	@Expose
	@SerializedName("userAccess1")
	private List<Long> userAccess1;

	@Expose
	@SerializedName("userAccess2")
	private List<Long> userAccess2;

	@Expose
	@SerializedName("userAccess3")
	private List<Long> userAccess3;

	@Expose
	@SerializedName("userAccess4")
	private List<Long> userAccess4;

	@Expose
	@SerializedName("userAccess5")
	private List<Long> userAccess5;

	@Expose
	@SerializedName("userAccess6")
	private List<Long> userAccess6;

	@Expose
	@SerializedName("sourceId")
	private List<Long> sourceId;

	@Expose
	@SerializedName("isDelete")
	private Boolean isDelete;

	@Expose
	@SerializedName("createdBy")
	private String createdBy;

	@Expose
	@SerializedName("createdOn")
	private LocalDate createdOn;

	@Expose
	@SerializedName("modifiedBy")
	private String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	private LocalDate modifiedOn;
}
