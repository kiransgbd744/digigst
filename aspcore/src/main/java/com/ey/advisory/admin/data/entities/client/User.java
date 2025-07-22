package com.ey.advisory.admin.data.entities.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Quartet;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class User {

	// The list of applicable configurations for each entity
	private Map<Long, List<Quartet<String, String, String, String>>> attrConfigMap;

	// The Map of map containing the list of attribute values applicable
	// for each entity. Does not contain GSTINs.
	private Map<Long, Map<String, List<Pair<Long, String>>>> attrValueMap;
	
	private String userPrincipalName;
	
	private String sacDashboardUrl;
	
	public User() {}

	public User(Map<Long, List<Quartet<String, String,String, String>>> entityMap,
			Map<Long, Map<String, List<Pair<Long, String>>>> attributeMap) {
		super();
		this.attrConfigMap = entityMap;
		this.attrValueMap = attributeMap;
	}

	// Get the list of attributes applicable for the entity. Returns the
	// list of pair of attr config code and attr config name. For example,
	// [("PC", "Profit Center"), ("SO", "Sales Organization"),
	// ("UD1", "SomeName")] is one possible return value. The order
	// in which these attribues appear in this list is important. These
	// attributes will be mapped to Attr1, Attr2, Attr3 etc.. in the DB.
	// Note that GSTINs will not be available in this list.
	public List<Quartet<String, String,String, String>> getApplicableAttrs(Long entityId) {
		return attrConfigMap.get(entityId);
	}

	// Get all entities applicable to the user. Returns a list of
	// pair object containing the entityId and entity name. This can be
	// used to populate the drop downs for entity list for a particular user.
	public List<Pair<Long, String>> getApplicableEntities() {
		return null;

	}

	// Get the list of applicable GSTINs for the user. GSTINs should
	// always be used in queries as IN clauses.
	public List<String> getApplicableGstins() {
		return null;
	}

	// Get the list of applicable GSTINs for the user, for the specified
	// entity. GSTINs should always be used in the queries as IN clauses.
	public List<String> getApplicableGstins(Long entityId) {
		return null;
	}

	// Get all attr values applicable to the user for the specified entity id
	// and attr code... For example, using this method, the caller can request
	// for all Profit centers accessible by the user belonging the
	// specified entity [(1,PlantCode1),(2, PlantCodew2),(3,PlantCodew3)]
	public List<Pair<Long, String>> getAttrValuesForAttrCode(Long entityId,
			String attrCode) {

		Map<String, List<Pair<Long, String>>> map = attrValueMap.get(entityId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("map : {} for entityId: {}", map,entityId);
			}
		
		return (map == null) ? new ArrayList<>() : map.get(attrCode);

	}

	public Map<Long, List<Quartet<String, String, String, String>>> getEntityMap() {
		return attrConfigMap;
	}

	public void setEntityMap(Map<Long, List<Quartet<String, String, String, String>>> entityMap) {
		this.attrConfigMap = entityMap;
	}

	public Map<Long, Map<String, List<Pair<Long, String>>>> getAttributeMap() {
		return attrValueMap;
	}

	public void setAttributeMap(
			Map<Long, Map<String, List<Pair<Long, String>>>> attributeMap) {
		this.attrValueMap = attributeMap;
	}

	public String getUserPrincipalName() {
		return userPrincipalName;
	}

	public void setUserPrincipalName(String userPrincipalName) {
		this.userPrincipalName = userPrincipalName;
	}

	public String getSacDashboardUrl() {
		return sacDashboardUrl;
	}

	public void setSacDashboardUrl(String sacDashboardUrl) {
		this.sacDashboardUrl = sacDashboardUrl;
	}

	@Override
	public String toString() {
		return "User [entityMap=" + attrConfigMap + ", attributeMap="
				+ attrValueMap + "]";
	}

}
