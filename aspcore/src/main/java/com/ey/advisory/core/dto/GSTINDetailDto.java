package com.ey.advisory.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class GSTINDetailDto  {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("groupCode")
	private String groupCode;
	
	@Expose
	@SerializedName("supplierGstin")
	private String supplierGstin;
	
	@Expose
	@SerializedName("registrationType")
	private String registrationType;

	@Expose
	@SerializedName("gstnUsername")
	private String gstnUsername;
	
	@Expose
	@SerializedName("effectiveDate")
	private LocalDate effectiveDate;
	
	@Expose
	@SerializedName("registeredEmail")
	private String registeredEmail;
	
	@Expose
	@SerializedName("registeredMobileNo")
	private String registeredMobileNo;
	
	@Expose
	@SerializedName("primaryAuthEmail")
	private String primaryAuthEmail;
	
	@Expose
	@SerializedName("secondaryAuthEmail")
	private String secondaryAuthEmail;
	
	@Expose
	@SerializedName("primaryContactEmail")
	private String primaryContactEmail;

	@Expose
	@SerializedName("secondaryContactEmail")
	private String secondaryContactEmail;
	
	@Expose
	@SerializedName("bankAccNo")
	private String bankAccNo;
	@Expose
	@SerializedName("turnover")
	private BigDecimal turnover;
	
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
	 * @return the supplierGstin
	 */
	public String getSupplierGstin() {
		return supplierGstin;
	}
	/**
	 * @param supplierGstin the supplierGstin to set
	 */
	public void setSupplierGstin(String supplierGstin) {
		this.supplierGstin = supplierGstin;
	}
	/**
	 * @return the registrationType
	 */
	public String getRegistrationType() {
		return registrationType;
	}
	/**
	 * @param registrationType the registrationType to set
	 */
	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}
	/**
	 * @return the gstnUsername
	 */
	public String getGstnUsername() {
		return gstnUsername;
	}
	/**
	 * @param gstnUsername the gstnUsername to set
	 */
	public void setGstnUsername(String gstnUsername) {
		this.gstnUsername = gstnUsername;
	}
	/**
	 * @return the effectiveDate
	 */
	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}
	/**
	 * @param effectiveDate the effectiveDate to set
	 */
	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	/**
	 * @return the registeredEmail
	 */
	public String getRegisteredEmail() {
		return registeredEmail;
	}
	/**
	 * @param registeredEmail the registeredEmail to set
	 */
	public void setRegisteredEmail(String registeredEmail) {
		this.registeredEmail = registeredEmail;
	}
	/**
	 * @return the registeredMobileNo
	 */
	public String getRegisteredMobileNo() {
		return registeredMobileNo;
	}
	/**
	 * @param registeredMobileNo the registeredMobileNo to set
	 */
	public void setRegisteredMobileNo(String registeredMobileNo) {
		this.registeredMobileNo = registeredMobileNo;
	}
	/**
	 * @return the primaryAuthEmail
	 */
	public String getPrimaryAuthEmail() {
		return primaryAuthEmail;
	}
	/**
	 * @param primaryAuthEmail the primaryAuthEmail to set
	 */
	public void setPrimaryAuthEmail(String primaryAuthEmail) {
		this.primaryAuthEmail = primaryAuthEmail;
	}
	/**
	 * @return the secondaryAuthEmail
	 */
	public String getSecondaryAuthEmail() {
		return secondaryAuthEmail;
	}
	/**
	 * @param secondaryAuthEmail the secondaryAuthEmail to set
	 */
	public void setSecondaryAuthEmail(String secondaryAuthEmail) {
		this.secondaryAuthEmail = secondaryAuthEmail;
	}
	/**
	 * @return the primaryContactEmail
	 */
	public String getPrimaryContactEmail() {
		return primaryContactEmail;
	}
	/**
	 * @param primaryContactEmail the primaryContactEmail to set
	 */
	public void setPrimaryContactEmail(String primaryContactEmail) {
		this.primaryContactEmail = primaryContactEmail;
	}
	/**
	 * @return the secondaryContactEmail
	 */
	public String getSecondaryContactEmail() {
		return secondaryContactEmail;
	}
	/**
	 * @param secondaryContactEmail the secondaryContactEmail to set
	 */
	public void setSecondaryContactEmail(String secondaryContactEmail) {
		this.secondaryContactEmail = secondaryContactEmail;
	}
	/**
	 * @return the bankAccNo
	 */
	public String getBankAccNo() {
		return bankAccNo;
	}
	/**
	 * @param bankAccNo the bankAccNo to set
	 */
	public void setBankAccNo(String bankAccNo) {
		this.bankAccNo = bankAccNo;
	}
	/**
	 * @return the turnover
	 */
	public BigDecimal getTurnover() {
		return turnover;
	}
	/**
	 * @param turnover the turnover to set
	 */
	public void setTurnover(BigDecimal turnover) {
		this.turnover = turnover;
	}
}
