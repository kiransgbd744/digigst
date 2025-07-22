package com.ey.advisory.app.services.gen;

import java.util.List;

import com.ey.advisory.gstr2.userdetails.GstinDto;

/**
 * EntityDto is responsible for transfering entity data to UI
 * @author Mohana.Dasari
 *
 */
public class EntityDto {

	private Long entityId;
	private String entityName;
	private List<GstinDto> gstins;
	
	public List<GstinDto> getGstin() {
		return gstins;
	}
	public void setGstin(List<GstinDto> gstins) {
		this.gstins = gstins;
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
