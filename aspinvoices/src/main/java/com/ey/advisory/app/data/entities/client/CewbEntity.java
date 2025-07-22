package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "CEWB_PROCESSED")
@Setter
@Getter
@ToString
public class CewbEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("sNo")
	@Column(name = "SL_NO")
	protected Long sNo;

	@Expose
	@SerializedName("ewbNo")
	@Column(name = "EWB_NO")
	protected String ewbNo;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@SerializedName("fromState")
	@Column(name = "FROM_STATE")
	protected String fromState;

	@Expose
	@SerializedName("fromPlace")
	@Column(name = "FROM_PLACE")
	protected String fromPlace;

	@Expose
	@SerializedName("vehicleNo")
	@Column(name = "VEHICLE_NO")
	protected String vehicleNo;

	@Expose
	@SerializedName("transMode")
	@Column(name = "TRANS_MODE")
	protected String transMode;

	@Expose
	@SerializedName("transDocNo")
	@Column(name = "TRANS_DOC_NUM")
	protected String transDocNo;

	@Expose
	@SerializedName("transDocDate")
	@Column(name = "TRANS_DOC_DATE")
	protected LocalDate transDocDate;

	@Expose
	@SerializedName("cewbInvKey")
	@Column(name = "DOC_KEY")
	protected String cewbInvKey;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@SerializedName("modifiedBy")
	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Expose
	@SerializedName("asEnterTableId")
	@Column(name = "AS_ENTERED_ID")
	protected Long asEnterTableId;
	
	@Expose
	@SerializedName("errorCode")
	@Column(name = "ERROR_CODE")
	protected String errorCode;
	
	@Expose
	@SerializedName("errorDesc")
	@Column(name = "ERROR_DESC")
	protected String errorDesc;
	
	@Expose
	@SerializedName("consolidatedEwbNum")
	@Column(name = "CONSOLIDATED_EWB_NUM")
	private String consolidatedEwbNum;
	
	@Expose
	@SerializedName("consolidatedEwbDate")
	@Column(name = "CONSOLIDATED_EWB_DATE")
	private LocalDateTime consolidatedEwbDate;
}