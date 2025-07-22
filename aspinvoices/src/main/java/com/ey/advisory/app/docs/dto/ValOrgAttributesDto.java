package com.ey.advisory.app.docs.dto;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public class ValOrgAttributesDto {

	private Long entityId;
	private Long groupId;
	private String transDocType;
	private String profitCentre;
	private String plant;
	private String division;
	private String location;
	private String salesOrg;
	private String purchaseOrg;
	private String distChnl;
	private String userDefined1;
	private String userDefined2;
	private String userDefined3;
	private String userDefined4;
	private String userDefined5;
	private String userDefined6;
	private String createdBy;
	private Map<Long, List<Pair<String, String>>> entityAtValMap;
	private Map<EntityAtConfigKey, Map<Long, String>> entityAtConfMap;

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getTransDocType() {
		return transDocType;
	}

	public void setTransDocType(String transDocType) {
		this.transDocType = transDocType;
	}

	public String getProfitCentre() {
		return profitCentre;
	}

	public void setProfitCentre(String profitCentre) {
		this.profitCentre = profitCentre;
	}

	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSalesOrg() {
		return salesOrg;
	}

	public void setSalesOrg(String salesOrg) {
		this.salesOrg = salesOrg;
	}

	public String getPurchaseOrg() {
		return purchaseOrg;
	}

	public void setPurchaseOrg(String purchaseOrg) {
		this.purchaseOrg = purchaseOrg;
	}

	public String getDistChnl() {
		return distChnl;
	}

	public void setDistChnl(String distChnl) {
		this.distChnl = distChnl;
	}

	public String getUserDefined1() {
		return userDefined1;
	}

	public void setUserDefined1(String userDefined1) {
		this.userDefined1 = userDefined1;
	}

	public String getUserDefined2() {
		return userDefined2;
	}

	public void setUserDefined2(String userDefined2) {
		this.userDefined2 = userDefined2;
	}

	public String getUserDefined3() {
		return userDefined3;
	}

	public void setUserDefined3(String userDefined3) {
		this.userDefined3 = userDefined3;
	}

	public String getUserDefined4() {
		return userDefined4;
	}

	public void setUserDefined4(String userDefined4) {
		this.userDefined4 = userDefined4;
	}

	public String getUserDefined5() {
		return userDefined5;
	}

	public void setUserDefined5(String userDefined5) {
		this.userDefined5 = userDefined5;
	}

	public String getUserDefined6() {
		return userDefined6;
	}

	public void setUserDefined6(String userDefined6) {
		this.userDefined6 = userDefined6;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Map<Long, List<Pair<String, String>>> getEntityAtValMap() {
		return entityAtValMap;
	}

	public void setEntityAtValMap(
			Map<Long, List<Pair<String, String>>> entityAtValMap) {
		this.entityAtValMap = entityAtValMap;
	}

	public Map<EntityAtConfigKey, Map<Long, String>> getEntityAtConfMap() {
		return entityAtConfMap;
	}

	public void setEntityAtConfMap(
			Map<EntityAtConfigKey, Map<Long, String>> entityAtConfMap) {
		this.entityAtConfMap = entityAtConfMap;
	}
}
