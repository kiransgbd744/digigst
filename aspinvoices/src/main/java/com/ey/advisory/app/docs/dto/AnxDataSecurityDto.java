package com.ey.advisory.app.docs.dto;

import java.util.List;

/**
 * This is responsible for transfering data security data to UI
 * 
 * @author Mohana.Dasari
 *
 */
public class AnxDataSecurityDto {

	private Long entityId;
	private String entityName;
	private List<DataSecDto> gstin;
	private List<DataSecDto> profitCenter;
	private List<DataSecDto> plant;
	private List<DataSecDto> division;
	private List<DataSecDto> location;
	private List<DataSecDto> salesOrg;
	private List<DataSecDto> purchOrg;
	private List<DataSecDto> distChannel;
	private List<DataSecDto> userAccess1;
	private List<DataSecDto> userAccess2;
	private List<DataSecDto> userAccess3;
	private List<DataSecDto> userAccess4;
	private List<DataSecDto> userAccess5;
	private List<DataSecDto> userAccess6;
	private AnxDataSecAttrItemDto items;

	/**
	 * @return the entityId
	 */
	public Long getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId
	 *            the entityId to set
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
	 * @param entityName
	 *            the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return the gstin
	 */
	public List<DataSecDto> getGstin() {
		return gstin;
	}

	/**
	 * @param gstin
	 *            the gstin to set
	 */
	public void setGstin(List<DataSecDto> gstin) {
		this.gstin = gstin;
	}

	/**
	 * @return the profitCenter
	 */
	public List<DataSecDto> getProfitCenter() {
		return profitCenter;
	}

	/**
	 * @param profitCenter
	 *            the profitCenter to set
	 */
	public void setProfitCenter(List<DataSecDto> profitCenter) {
		this.profitCenter = profitCenter;
	}

	/**
	 * @return the plant
	 */
	public List<DataSecDto> getPlant() {
		return plant;
	}

	/**
	 * @param plant
	 *            the plant to set
	 */
	public void setPlant(List<DataSecDto> plant) {
		this.plant = plant;
	}

	/**
	 * @return the division
	 */
	public List<DataSecDto> getDivision() {
		return division;
	}

	/**
	 * @param division
	 *            the division to set
	 */
	public void setDivision(List<DataSecDto> division) {
		this.division = division;
	}

	/**
	 * @return the location
	 */
	public List<DataSecDto> getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(List<DataSecDto> location) {
		this.location = location;
	}

	/**
	 * @return the salesOrg
	 */
	public List<DataSecDto> getSalesOrg() {
		return salesOrg;
	}

	/**
	 * @param salesOrg
	 *            the salesOrg to set
	 */
	public void setSalesOrg(List<DataSecDto> salesOrg) {
		this.salesOrg = salesOrg;
	}

	/**
	 * @return the purchOrg
	 */
	public List<DataSecDto> getPurchOrg() {
		return purchOrg;
	}

	/**
	 * @param purchOrg
	 *            the purchOrg to set
	 */
	public void setPurchOrg(List<DataSecDto> purchOrg) {
		this.purchOrg = purchOrg;
	}

	/**
	 * @return the distChannel
	 */
	public List<DataSecDto> getDistChannel() {
		return distChannel;
	}

	/**
	 * @param distChannel
	 *            the distChannel to set
	 */
	public void setDistChannel(List<DataSecDto> distChannel) {
		this.distChannel = distChannel;
	}

	/**
	 * @return the userAccess1
	 */
	public List<DataSecDto> getUserAccess1() {
		return userAccess1;
	}

	/**
	 * @param userAccess1
	 *            the userAccess1 to set
	 */
	public void setUserAccess1(List<DataSecDto> userAccess1) {
		this.userAccess1 = userAccess1;
	}

	/**
	 * @return the userAccess2
	 */
	public List<DataSecDto> getUserAccess2() {
		return userAccess2;
	}

	/**
	 * @param userAccess2
	 *            the userAccess2 to set
	 */
	public void setUserAccess2(List<DataSecDto> userAccess2) {
		this.userAccess2 = userAccess2;
	}

	/**
	 * @return the userAccess3
	 */
	public List<DataSecDto> getUserAccess3() {
		return userAccess3;
	}

	/**
	 * @param userAccess3
	 *            the userAccess3 to set
	 */
	public void setUserAccess3(List<DataSecDto> userAccess3) {
		this.userAccess3 = userAccess3;
	}

	/**
	 * @return the userAccess4
	 */
	public List<DataSecDto> getUserAccess4() {
		return userAccess4;
	}

	/**
	 * @param userAccess4
	 *            the userAccess4 to set
	 */
	public void setUserAccess4(List<DataSecDto> userAccess4) {
		this.userAccess4 = userAccess4;
	}

	/**
	 * @return the userAccess5
	 */
	public List<DataSecDto> getUserAccess5() {
		return userAccess5;
	}

	/**
	 * @param userAccess5
	 *            the userAccess5 to set
	 */
	public void setUserAccess5(List<DataSecDto> userAccess5) {
		this.userAccess5 = userAccess5;
	}

	/**
	 * @return the userAccess6
	 */
	public List<DataSecDto> getUserAccess6() {
		return userAccess6;
	}

	/**
	 * @param userAccess6
	 *            the userAccess6 to set
	 */
	public void setUserAccess6(List<DataSecDto> userAccess6) {
		this.userAccess6 = userAccess6;
	}

	/**
	 * @return the items
	 */
	public AnxDataSecAttrItemDto getItems() {
		return items;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(AnxDataSecAttrItemDto items) {
		this.items = items;
	}

}