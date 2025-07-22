package com.ey.advisory.app.data.entities.client.asprecon;

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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "VENDOR_COMM_REQUEST")
@Getter
@Setter
@ToString
@NamedStoredProcedureQueries({
		@NamedStoredProcedureQuery(name = "vendorCommReportDataNonAP", procedureName = "USP_AUTO_2APR_NON_AP_VENDOR_EMAIL", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REQUEST_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VENDOR_GSTIN", type = String.class) }),
		@NamedStoredProcedureQuery(name = "vendorCommReportDataAPOpted", procedureName = "USP_AUTO_2APR_AUTO_AP_VENDOR_EMAIL", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REQUEST_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VENDOR_GSTIN", type = String.class) }),
		@NamedStoredProcedureQuery(name = "vendorCommSummReportDataNonAP", procedureName = "USP_AUTO_2APR_NON_AP_VENDOR_EMAIL_SMRY", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REQUEST_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VENDOR_GSTIN", type = String.class) }),
		@NamedStoredProcedureQuery(name = "vendorCommSummRGstinDataNonAp", procedureName = "USP_AUTO_2APR_NON_AP_VENDOR_EMAIL_SMRY_RGSTIN", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REQUEST_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VENDOR_GSTIN", type = String.class) }),
		@NamedStoredProcedureQuery(name = "vendorCommSummReportDataAPOpted", procedureName = "USP_AUTO_2APR_AUTO_AP_VENDOR_EMAIL_SMRY", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REQUEST_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VENDOR_GSTIN", type = String.class) }),
		@NamedStoredProcedureQuery(name = "vendorCommSummRGstinDataAPOpted", procedureName = "USP_AUTO_2APR_AUTO_AP_VENDOR_EMAIL_SMRY_RGSTIN", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REQUEST_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VENDOR_GSTIN", type = String.class) }),
		@NamedStoredProcedureQuery(name = "vendorComm2BPRReportData", procedureName = "USP_2BPR_VENDOR_EMAIL", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REQUEST_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VENDOR_GSTIN", type = String.class) }),
		@NamedStoredProcedureQuery(name = "vendorComm2BPRSummaryReport", procedureName = "USP_2BPR_VENDOR_EMAIL_SMRY", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REQUEST_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VENDOR_GSTIN", type = String.class) }),
		@NamedStoredProcedureQuery(name = "vendorComm2BPRRgstinReport", procedureName = "USP_2BPR_VENDOR_EMAIL_SMRY_RGSTIN", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REQUEST_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VENDOR_GSTIN", type = String.class) })

})

public class VendorCommRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "NO_OF_RECIPIENT_GSTINS")
	private Long noOfRecipientGstins;

	@Column(name = "NO_OF_VENDOR_GSTINS")
	private Long noOfVendorGstins;

	@Column(name = "NO_OF_REPORT_TYPES")
	private Long noOfReportTypes;

	@Column(name = "REPORT_TYPES")
	private String reportTypes;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "FROM_TAX_PERIOD")
	private String fromTaxPeriod;

	@Column(name = "TO_TAX_PERIOD")
	private String toTaxPeriod;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "FILEPATH")
	private String filePath;

	@Column(name = "RECON_TYPE")
	private String reconType;
	
	@Column(name = "DATA_PREP_STATUS")
	private String dataPrepStatus;
	
	@Column(name = "DOC_ID")
	private String docId;

}
