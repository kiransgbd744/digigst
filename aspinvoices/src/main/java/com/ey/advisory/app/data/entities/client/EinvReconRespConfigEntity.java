package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQueries;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Siva Reddy
 *
 */
@Setter
@Getter
@ToString
@Entity
@Table(name = "TBL_EINV_RECON_RESPONSE_CONFIG")
@NamedStoredProcedureQueries({
		@NamedStoredProcedureQuery(name = "insertEinvRecSumChunkData", procedureName = "USP_INS_CHUNK_EINV_RECON_SMRY_RESP", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_RECON_RESP_CONFIG_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_SPILIT_VAL", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "eInvRecSummResp", procedureName = "USP_EINV_RECON_SUMMARY_RESPONSE", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_RECON_RESP_CONFIG_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_DATA", type = Integer.class) }), })

public class EinvReconRespConfigEntity {

	public EinvReconRespConfigEntity() {
		super();
	}

	@Expose
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RECON_RESP_CONFIG_ID")
	private Long reconRespConfigId;

	@Expose
	@Column(name = "RECON_RESP_STATUS")
	private String reconRespStatus;

	@Expose
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdDate;

	@Expose
	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;

}
