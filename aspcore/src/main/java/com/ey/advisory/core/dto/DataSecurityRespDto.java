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
public class DataSecurityRespDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("groupCode")
	private String groupCode;

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("entityName")
	private String entityName;

	@Expose
	@SerializedName("gstin")
	private List<GSTINDetailDto> gstiNDetailDto;

	@Expose
	@SerializedName("profitCenter")
	private List<ProfitCenterDto> profitCenter;
	
	@Expose
	@SerializedName("profitCenter2")
	private List<ProfitCenter2Dto> profitCenter2;

	@Expose
	@SerializedName("plant")
	private List<PlantDto> plant;

	@Expose
	@SerializedName("division")
	private List<DivisionDto> division;

	@Expose
	@SerializedName("subDivision")
	private List<SubDivisionDto> subDivision;

	@Expose
	@SerializedName("location")
	private List<LocationDto> location;

	@Expose
	@SerializedName("salesOrg")
	private List<SalesOrgDto> salesOrg;

	@Expose
	@SerializedName("purchOrg")
	private List<PurchOrgDto> purchOrg;

	@Expose
	@SerializedName("distChannel")
	private List<DistChannelDto> distChannel;

	@Expose
	@SerializedName("userAccess1")
	private List<UserAccess1Dto> userAccess1;

	@Expose
	@SerializedName("userAccess2")
	private List<UserAccess2Dto> userAccess2;

	@Expose
	@SerializedName("userAccess3")
	private List<UserAccess3Dto> userAccess3;

	@Expose
	@SerializedName("userAccess4")
	private List<UserAccess4Dto> userAccess4;

	@Expose
	@SerializedName("userAccess5")
	private List<UserAccess5Dto> userAccess5;

	@Expose
	@SerializedName("userAccess6")
	private List<UserAccess6Dto> userAccess6;

	@Expose
	@SerializedName("sourceId")
	private List<SourceIdDto> sourceId;

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

	@Expose
	@SerializedName("items")
	private ItemsDto items;

}
