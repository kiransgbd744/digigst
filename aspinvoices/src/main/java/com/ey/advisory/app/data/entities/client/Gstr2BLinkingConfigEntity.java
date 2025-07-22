package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Entity
@Data
@Table(name = "TBL_2B_Linking_CONFIG")
public class Gstr2BLinkingConfigEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@Column(name = "TAX_PERIOD")
	protected String taxPeriod;

	@Expose
	@Column(name = "STATUS")
	protected String status;

	@Expose
	@Column(name = "CREATED_DATE")
	protected LocalDateTime createdDate;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@Column(name = "COMPLETED_ON")
	protected LocalDateTime completedOn;

	@Expose
	@Column(name = "IS_DELETE")
	protected Boolean isDelete;

}
