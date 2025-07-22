package com.ey.advisory.app.data.entities.client;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@MappedSuperclass
@Data
public class GetGstr1SummaryEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@SerializedName("ret_period")
	@Column(name = "RET_PERIOD")
	protected String retPeriod;
	
	@Column(name = "DERIVED_RET_PERIOD")
	protected int dervRetPeriod;

	@Expose
	@SerializedName("chksum")
	@Column(name = "RET_PERIOD_CHKSUM")
	protected String chkSum;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

}
