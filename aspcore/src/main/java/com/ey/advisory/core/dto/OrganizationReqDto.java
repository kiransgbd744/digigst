package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class OrganizationReqDto {

	@Expose
	@SerializedName("groupCode")
	private String groupCode;
	
	
	@Expose
	@SerializedName("id")
	private Long id;
	
	
	@Expose
	@SerializedName("attributeName")
	private String attributeName;
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("entityName")
	private String entityName;
	
	@Expose
	@SerializedName("attCode")
	private String attCode;
	
	@Expose
	@SerializedName("attName")
	private String attName;
	
	@Expose
	@SerializedName("outword")
	private String outword;
	
	@Expose
	@SerializedName("inword")
	private String inword;

	public String getAttributeName() {
	    return attributeName;
	}

	public void setAttributeName(String attributeName) {
	    this.attributeName = attributeName;
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

	/**
	 * @return the attCode
	 */
	public String getAttCode() {
		return attCode;
	}

	/**
	 * @param attCode the attCode to set
	 */
	public void setAttCode(String attCode) {
		this.attCode = attCode;
	}

	/**
	 * @return the attName
	 */
	public String getAttName() {
		return attName;
	}

	/**
	 * @param attName the attName to set
	 */
	public void setAttName(String attName) {
		this.attName = attName;
	}

	/**
	 * @return the outword
	 */
	public String getOutword() {
		return outword;
	}

	/**
	 * @param outword the outword to set
	 */
	public void setOutword(String outword) {
		this.outword = outword;
	}

	/**
	 * @return the inword
	 */
	public String getInword() {
		return inword;
	}

	/**
	 * @param inword the inword to set
	 */
	public void setInword(String inword) {
		this.inword = inword;
	}

	
}
