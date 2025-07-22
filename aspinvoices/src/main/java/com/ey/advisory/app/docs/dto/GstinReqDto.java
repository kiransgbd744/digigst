package com.ey.advisory.app.docs.dto;

import java.util.List;

public class GstinReqDto {
	
	private List<Long> entityId;

	/**
	 * @return the entityId
	 */
	public List<Long> getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}
}
