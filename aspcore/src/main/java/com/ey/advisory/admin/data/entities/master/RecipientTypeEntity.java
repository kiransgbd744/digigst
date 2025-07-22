package com.ey.advisory.admin.data.entities.master;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Siva.Nandam
 *
 */
@Entity
@Table(name = "MASTER_RECIPIENT_TYPE")
public class RecipientTypeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "RECIPIENT_TYPE")
	private String recipientType;

	@Expose
	@Column(name = "RECIPIENT_TYPE_DESC")
	private String recipientdesc;
	
	@Expose
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Expose
	@Column(name = "CREATED_ON")
	private LocalDate createdOn;

	@Expose
	@Column(name = "MODIFIED_BY")
	private LocalDate modifiedBy;

	@Expose
	@Column(name = "MODIFIED_ON")
	private String modifiedOn;

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
	 * @return the recipientType
	 */
	public String getRecipientType() {
		return recipientType;
	}

	/**
	 * @param recipientType the recipientType to set
	 */
	public void setRecipientType(String recipientType) {
		this.recipientType = recipientType;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdOn
	 */
	public LocalDate getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(LocalDate createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the modifiedBy
	 */
	public LocalDate getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(LocalDate modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedOn
	 */
	public String getModifiedOn() {
		return modifiedOn;
	}

	/**
	 * @param modifiedOn the modifiedOn to set
	 */
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	/**
	 * @return the recipientdesc
	 */
	public String getRecipientdesc() {
		return recipientdesc;
	}

	/**
	 * @param recipientdesc the recipientdesc to set
	 */
	public void setRecipientdesc(String recipientdesc) {
		this.recipientdesc = recipientdesc;
	}
	

}