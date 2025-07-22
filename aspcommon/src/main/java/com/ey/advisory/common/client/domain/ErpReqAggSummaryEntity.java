package com.ey.advisory.common.client.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQueries;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * *
 * 
 * @author Siva Reddy
 *
 */
@Entity
@Table(name = "ERP_REQ_AGG_SUMMARY")
@Setter
@Getter
@ToString
@NamedStoredProcedureQueries({
		@NamedStoredProcedureQuery(name = "insertERPReqAggSummaryData", 
				procedureName = "USP_ERP_REQ_AGG_SUMMARY_DATA_LOAD") })
public class ErpReqAggSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	protected Long id;

	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@SerializedName("sGstin")
	@Column(name = "SGSTIN")
	protected String sGstin;

	@Expose
	@SerializedName("summaryDate")
	@Column(name = "SUMMARY_DATE")
	private LocalDate summaryDate;

	@Expose
	@SerializedName("totalReq")
	@Column(name = "TOTAL_REQ")
	protected Long totalRequest;

	@Expose
	@SerializedName("genCount")
	@Column(name = "GEN_COUNT")
	protected Long genCount;

	@Expose
	@SerializedName("canCount")
	@Column(name = "CAN_COUNT")
	protected Long canCount;

	@Expose
	@SerializedName("dupCount")
	@Column(name = "DUP_COUNT")
	protected Long dupCount;

	@Expose
	@SerializedName("errorCount")
	@Column(name = "ERROR_COUNT")
	protected Long errorCount;

	@Expose
	@SerializedName("providerName")
	@Column(name = "PROVIDER_NAME")
	private String providerName;

	@Expose
	@SerializedName("isPushedtoCloud")
	@Column(name = "IS_PUSHED_TO_CLOUD")
	private boolean isPushedtoCloud;

	@Expose
	@SerializedName("isActive")
	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("region")
	@Column(name = "REGION")
	private String region;
}
