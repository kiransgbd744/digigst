package com.ey.advisory.core.dto;

import java.time.LocalDate;

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
public class DataSecurityAttriUserRespDto {

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
	@SerializedName("dataSecurity")
	private DataSecurityDto dataSecurity;
	
	@Expose
	@SerializedName("appPermission")
	private AppPermissionDto appPermissionDto;
	
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
