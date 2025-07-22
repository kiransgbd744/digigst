package com.ey.advisory.core.dto;

public class OutwardInwardQueAnsReqDto {

    private String groupCode;
    private Long entityId;
    private String type;
    
    public String getGroupCode() {
        return groupCode;
    }
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
    public Long getEntityId() {
        return entityId;
    }
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    @Override
    public String toString() {
	return "OutwardInwardQueAnsRequest [groupCode=" + groupCode
		+ ", entityId=" + entityId + ", type=" + type + "]";
    }
}
