package com.ey.advisory.admin.services.onboarding;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public class EntityAtConfigKey {

	private Long entityId;
	private String atCode;

	public EntityAtConfigKey(Long entityId, String atCode) {
		this.entityId = entityId;
		this.atCode = atCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof EntityAtConfigKey) {
			EntityAtConfigKey key = (EntityAtConfigKey) obj;
			return entityId.equals(key.entityId) && atCode.equals(key.atCode);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (entityId + atCode).hashCode();
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getAtCode() {
		return atCode;
	}

	public void setAtCode(String atCode) {
		this.atCode = atCode;
	}

	@Override
	public String toString() {
		return String.format("[entityId=%s, atCode=%s]",
				entityId, atCode);
	}
	
}
