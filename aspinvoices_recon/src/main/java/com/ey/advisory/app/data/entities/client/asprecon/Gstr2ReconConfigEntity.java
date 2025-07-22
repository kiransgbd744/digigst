package com.ey.advisory.app.data.entities.client.asprecon;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Entity
@Table(name = "TBL_RECON_REPORT_CONFIG")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
/*
 * @NamedStoredProcedureQueries({
 * 
 * @NamedStoredProcedureQuery(name = "gstr2InitiateReconcile", procedureName =
 * "USP_2APR_RECON_MASTER", parameters = {
 * 
 * @StoredProcedureParameter(mode = ParameterMode.IN, name =
 * "P_RECON_REPORT_CONFIG_ID", type = Long.class) })
 * 
 * })
 */
public class Gstr2ReconConfigEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "RECON_REPORT_CONFIG_ID")
	protected Long configId;

	@Expose
	@Column(name = "ENTITY_ID")
	protected Long entityId;

	@Expose
	@Column(name = "RECON_TYPE")
	protected String type;

	@Expose
	@Column(name = "PAN")
	protected String pan;

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
	@Column(name = "TO_RET_PERIOD")
	protected Integer toTaxPeriodPR;

	@Expose
	@Column(name = "FROM_RET_PERIOD")
	protected Integer fromTaxPeriodPR;

	@Expose
	@Column(name = "A2_TO_RET_PERIOD")
	protected Integer toTaxPeriod2A;

	@Expose
	@Column(name = "A2_FROM_RET_PERIOD")
	protected Integer fromTaxPeriod2A;

	@Expose
	@Column(name = "TO_DOC_DATE")
	protected LocalDate toDocDate;

	@Expose
	@Column(name = "FROM_DOC_DATE")
	protected LocalDate fromDocDate;

	@Expose
	@Column(name = "FILE_PATH")
	protected String filePath;

	@Expose
	@Column(name = "STATUS")
	protected String status;

	@Expose
	@Column(name = "INFORMATION_REPORT_ID")
	protected String inforRId;

	@Expose
	@Column(name = "REQUEST_TYPE")
	protected String requestType;

	@Expose
	@Column(name = "IS_IMPG_RECON")
	protected Boolean impgRecon;

	@Expose
	@Column(name = "AUTO_RECON_DATE")
	protected LocalDate autoReconDate;
	
	//for 2BPR chunk 
	@Expose
	@Column(name = "REPORT_CHUNK_SIZE")
	protected Integer reportChunkSize;
	
	@Expose
	@Column(name = "IS_MANDATORY")
	protected Boolean isMandatory;
	
	@Expose
	@Column(name = "IS_ISD_RECON")
	protected Boolean isdRecon;
	
	@Expose
	@Column(name = "ENTITY_NAME")
	protected String entityName;
	
	@Expose
	@Column(name = "IS_ITC_REJ_OPTED")
	protected String isItcRejOpted;
	
	
}
