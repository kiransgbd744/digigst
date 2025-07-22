package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class ELExtractRegDto {


	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("entityName")
	private String entityName;

	@Expose
	@SerializedName("items")
	List<ELExtractRegItemsDetailsDto> list;
	
	/**
	 * @return the list
	 */
	public List<ELExtractRegItemsDetailsDto> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<ELExtractRegItemsDetailsDto> list) {
		this.list = list;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ELExtractRegDto [id=" + id + ", entityId=" + entityId
				+ ", entityName=" + entityName + ", list=" + list + "]";
	}
	
}
