/**
 * 
 */
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

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "ITC04_STOCKTRACK_COMPUTE_DETAILS")
public class Itc04StockTrackingComputeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	@Expose
	@SerializedName("id")
	private Long id;

	@Column(name = "GSTIN")
	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Column(name = "REQUEST_TYPE")
	@Expose
	@SerializedName("requestType")
	private String requestType;

	@Column(name = "FY")
	@Expose
	@SerializedName("fy")
	private String fy;

	@Column(name = "CHALLAN_FROM_DATE")
	@Expose
	@SerializedName("challanFromDate")
	private LocalDate challanFromDate;

	@Column(name = "CHALLAN_TO_DATE")
	@Expose
	@SerializedName("challanToDate")
	private LocalDate challanToDate;

	@Column(name = "FROM_RET_PERIOD")
	@Expose
	@SerializedName("fromRetPeriod")
	private String fromRetPeriod;

	@Column(name = "TO_RET_PERIOD")
	@Expose
	@SerializedName("toRetPeriod")
	private String toRetPeriod;

	@Column(name = "IS_ACTIVE")
	@Expose
	@SerializedName("isActive")
	private Boolean isActive;

	@Column(name = "REPORT_STATUS")
	@Expose
	@SerializedName("reportStatus")
	private String reportStatus;

	@Column(name = "OPEN_CHALLAN_GR_365_IG")
	@Expose
	@SerializedName("openChallanGr365IG")
	private Integer openChallanGr365IG;

	@Column(name = "OPEN_CHALLAN_LS_365_IG")
	@Expose
	@SerializedName("openChallanLs365IG")
	private Integer openChallanLs365IG;

	@Column(name = "OPEN_CHALLAN_GR_1095_CG")
	@Expose
	@SerializedName("openChallanGr1095CG")
	private Integer openChallanGr1095CG;

	@Column(name = "OPEN_CHALLAN_LS_1095_CG")
	@Expose
	@SerializedName("openChallanLs1095CG")
	private Integer openChallanLs1095CG;

	@Column(name = "CREATED_BY")
	@Expose
	@SerializedName("createdBy")
	private String createdBy;

	@Column(name = "CREATED_ON")
	@Expose
	@SerializedName("createdOn")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	@Expose
	@SerializedName("modifiedBy")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	@Expose
	@SerializedName("modifiedOn")
	private LocalDate modifiedOn;
}
