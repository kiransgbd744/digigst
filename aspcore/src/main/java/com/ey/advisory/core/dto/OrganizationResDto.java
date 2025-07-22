package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class OrganizationResDto {

	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("entityName")
	private String entityName;
	
	@SerializedName("items")
	List<OrgaItemDetailsResDto> orgaResItemDetailsDto;

	/**
	 * @return the entityId
	 */
	public Long getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return the orgaResItemDetailsDto
	 */
	public List<OrgaItemDetailsResDto> getOrgaResItemDetailsDto() {
		return orgaResItemDetailsDto;
	}

	/**
	 * @param orgaResItemDetailsDto the orgaResItemDetailsDto to set
	 */
	public void setOrgaResItemDetailsDto(
			List<OrgaItemDetailsResDto> orgaResItemDetailsDto) {
		this.orgaResItemDetailsDto = orgaResItemDetailsDto;
	}
	
}
