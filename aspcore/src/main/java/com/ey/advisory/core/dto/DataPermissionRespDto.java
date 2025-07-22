package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class DataPermissionRespDto {

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
	@SerializedName("items")
	List<DataPermItemsDetRespDto> items;
	
	/**
	 * @return the items
	 */
	public List<DataPermItemsDetRespDto> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<DataPermItemsDetRespDto> items) {
		this.items = items;
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
	 * @return the groupCode
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
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
	
	
	
}
