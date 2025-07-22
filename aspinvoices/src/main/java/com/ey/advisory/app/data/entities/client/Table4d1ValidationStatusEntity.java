package com.ey.advisory.app.data.entities.client;


import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author akhilesh.yadav
 *
 */
@ToString
@Setter
@Getter
@Entity
@Table(name = "TABLE_4D1_VALIDATION_STATUS")
public class Table4d1ValidationStatusEntity {
	
	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TABLE_4D1_VALIDATION_STATUS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;
	
	@Expose
	@SerializedName("Gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@SerializedName("taxPeriod")
	@Column(name = "TAX_PERIOD")
	protected String taxPeriod;
	
	@Expose
	@SerializedName("validationFlag")
	@Column(name = "VALIDATION_FLAG")
	protected Boolean validationFlag;
	
	@Expose
	@SerializedName("errorFlag")
	@Column(name = "ERROR_FLAG")
	protected Boolean errorFlag;
	
	@Expose
	@SerializedName("isActive")
	@Column(name = "IS_ACTIVE")
	protected Boolean isActive;
		
	
	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;
	
	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	protected String createdBy;
	
	@Expose
	@SerializedName("updatedOn")
	@Column(name = "UPDATED_ON")
	protected LocalDateTime updatedOn;
	
	@Expose
	@SerializedName("updatedBy")
	@Column(name = "UPDATED_BY")
	protected String updatedBy;

}
