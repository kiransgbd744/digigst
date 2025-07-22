package com.ey.advisory.app.data.entities.client.asprecon;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * @author Arun.KA
 *
 */

@Entity
@Table(name = "RECON_REPORT_CONFIG")
@Setter
@Getter
@ToString
@NamedStoredProcedureQueries({
		@NamedStoredProcedureQuery(name = "initiateReconcile",
				procedureName = "USP_RECON_MASTER", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN,
						name = "P_RECON_REPORT_CONFIG_ID",
						type = Long.class) }),
		@NamedStoredProcedureQuery(name = "generateRecords",
		procedureName = "USP_RECON_REPORT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN,
						name = "P_RECON_REPORT_CONFIG_ID",
						type = Long.class) }) })

public class ReconConfigEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "RECON_REPORT_CONFIG_ID")
	protected Long configId;
	
	@Expose
	@Column(name="ENTITY_ID")
	protected Long entityId;

	@Expose
	@Column(name = "TYPE")
	protected String type;

	@Expose
	@Column(name = "CREATED_DATE")
	protected Date createdDate;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@Column(name = "COMPLETED_ON")
	protected Date completedOn;

	@Expose
	@Column(name = "TAX_PERIOD")
	protected String taxPeriod;

	@Expose
	@Column(name = "FILE_PATH")
	protected String filePath;

	@Expose
	@Column(name = "STATUS")
	protected String status;
	
	@Expose
	@Column(name = "INFORMATION_REPORT_ID")
	protected String infoReportId;
	

}
