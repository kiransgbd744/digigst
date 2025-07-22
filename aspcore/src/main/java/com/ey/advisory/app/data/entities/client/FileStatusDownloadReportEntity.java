package com.ey.advisory.app.data.entities.client;

import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedStoredProcedureQueries;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "REPORT_DOWNLOAD_REQUEST")
@Getter
@Setter
@ToString
@NamedStoredProcedureQueries({
		@NamedStoredProcedureQuery(name = "insertChunkData", procedureName = "USP_INSERT_CHUNK_DATA", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "chunkErrorData", procedureName = "USP_CHUNK_DISP_ERROR_DATA", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "dataStatusChunkCount", procedureName = "USP_INS_CHUNK_OD_DATA_STATUS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "dataStatusChunkData", procedureName = "USP_CHUNK_DISP_OD_DATA_STATUS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "processedChunkCount", procedureName = "USP_INS_CHUNK_OD_PS_ASP_PROCESS_UPLOADED", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "processedChunkData", procedureName = "USP_CHUNK_DISP_OD_PS_ASP_PROCESS_UPLOADED", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "Gstr1CustReportData", procedureName = "USP_DY_CHUNK_DISP_OD_PS_ASP_PROCESS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_COLUMN_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "errorChunkCount", procedureName = "USP_INS_CHUNK_OD_PS_CONS_ASP_ERROR", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "errorChunkData", procedureName = "USP_CHUNK_DISP_OD_PS_CONS_ASP_ERROR", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "gstnChunkCount", procedureName = "USP_INS_CHUNK_OD_PS_GSTN_ERROR_CONSOLIDATED", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstnChunkData", procedureName = "USP_CHUNK_DISP_OD_PS_GSTN_ERROR_CONSOLIDATED", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "gstr2ChunkCount", procedureName = "USP_INS_CHUNK_IN_GSTR2_PROCESSED", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr2ChunkData", procedureName = "USP_CHUNK_DISP_IN_GSTR2_PROCESSED", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "gstr6ChunkCount", procedureName = "USP_INS_CHUNK_IN_GSTR6_PROCESSED", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr6ChunkData", procedureName = "USP_CHUNK_DISP_IN_GSTR6_PROCESSED", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "gstr6LockChunkCount", procedureName = "USP_INS_CHUNK_IN_GSTR6_PROCESSED_LOCK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr6LockChunkData", procedureName = "USP_CHUNK_DISP_IN_GSTR6_PROCESSED_LOCK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		
		
		@NamedStoredProcedureQuery(name = "inwardFileStatusChunkCount", procedureName = "USP_INS_CHUNK_IN_FILE_STATUS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "inwardchunkErrorData", procedureName = "USP_CHUNK_DISP_IN_FILE_STATUS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "dataStatusInwardChunkCount", procedureName = "USP_INS_CHUNK_IN_DATA_STATUS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "dataStatusInwardChunkData", procedureName = "USP_CHUNK_DISP_IN_DATA_STATUS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "Gstr3bOutwardChunkCount", procedureName = "USP_INS_OD_GSTR_3B_TRANSACTIONAL", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "Gstr3bOutwardChunkData", procedureName = "USP_DISP_OD_GSTR_3B_TRANSACTIONAL", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "Gstr3bTable4TransactionalChunkCount", procedureName = "USP_GSTR_3B_INS_CHUNK_TABLE_4_TRANS_RPT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_SPILIT_VAL", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "Gstr3bTable4TransactionalChunkData", procedureName = "USP_GSTR_3B_DISP_CHUNK_TABLE_4_TRANS_RPT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		
		@NamedStoredProcedureQuery(name = "Gstr3bInwardChunkCount", procedureName = "USP_INS_IN_GSTR_3B_PROCESSED", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "Gstr3bInwardChunkData", procedureName = "USP_DISP_IN_GSTR_3B_PROCESSED", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "InvoiceManagementChunkCount", procedureName = "USP_INS_CHUNK_INV_MNG_STATUS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "InvoiceManagementChunkData", procedureName = "USP_DISP_CHUNK_INV_MNG_STATUS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		
		@NamedStoredProcedureQuery(name = "InvoiceGstr1AManagementChunkCount", procedureName = "USP_INS_CHUNK_INV_MNG_STATUS_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "InvoiceGstr1AManagementChunkData", procedureName = "USP_DISP_CHUNK_INV_MNG_STATUS_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "InvOutwrdManagementChunkCount", procedureName = "USP_INS_CHUNK_INV_MNG_SHIPDTL", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_SPILIT_VAL", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "InvOutwardManagementChunkData", procedureName = "USP_DISP_CHUNK_INV_MNG_SHIPDTL", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "InvOutwrd1AManagementChunkCount", procedureName = "USP_INS_CHUNK_INV_MNG_SHIPDTL_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_SPILIT_VAL", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "InvOutward1AManagementChunkData", procedureName = "USP_DISP_CHUNK_INV_MNG_SHIPDTL_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "InInvManagementChunkCount", procedureName = "USP_INS_CHUNK_INWARD_INV_MNG_STATUS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "InInvManagementChunkData", procedureName = "USP_DISP_CHUNK_INWARD_INV_MNG_STATUS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "Gstr1EInvChunkCount", procedureName = "USP_INS_CHUNK_GET_EINV_REPORT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "Gstr1EInvChunkData", procedureName = "USP_CHUNK_DISP_GET_EINV_REPORT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "gstr2AProcessSumCount", procedureName = "USP_INSERT_CHUNK_GSTR2A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr2AProcessSumChunkData", procedureName = "USP_CHUNK_DISP_GSTR2A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "gstr2AFyProcessSumCount", procedureName = "USP_INSERT_CHUNK_GSTR2A_SCREEN_REPORT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr2AFyProcessSumChunkData", procedureName = "USP_CHUNK_DISP_GSTR2A_SCREEN_REPORT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "eInvoiceChunkCount", procedureName = "USP_INS_CHUNK_EINV_RECON_RESP", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "eInvoiceChunkData", procedureName = "USP_CHUNK_DISP_EINV_RECON_RESP", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "isdChunkCount", procedureName = "USP_ISD_DIST_INS_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "isdChunkData", procedureName = "USP_ISD_DIST_DISP_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		
		@NamedStoredProcedureQuery(name = "glDumpChunkCount", procedureName = "USP_GL_RECON_RPT_INS_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "glDumpChunkData", procedureName = "USP_GL_RECON_RPT_DISP_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		
		@NamedStoredProcedureQuery(name = "gstr6aChunkCount", procedureName = "USP_INS_CHUNK_INV_GST6A_SUMMARY", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr6aChunkData", procedureName = "USP_DISP_CHUNK_GSTR6A_SUMMARY", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "outwardFileStatusRetChunkDataCount", procedureName = "USP_INSERT_CHUNK_FILE_RETURNS_DATA", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "outwardFileStatusRetChunkData", procedureName = "USP_CHUNK_DISP_FILE_RETURNS_DATA", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "outwardFileStatusEinvChunkDataCount", procedureName = "USP_INSERT_CHUNK_DATA_EINV", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "outwardFileStatusEinvChunkData", procedureName = "USP_CHUNK_DISP_EINV_DATA", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "outwardFileStatusEwbChunkDataCount", procedureName = "USP_INSERT_CHUNK_DATA_EWB", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "outwardFileStatusEwbChunkData", procedureName = "USP_CHUNK_DISP_DATA_EWB", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "gstr2aPopUpCount", procedureName = "USP_INS_CHUNK_IN_GSTR2A_B2B", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr2aPopUpData", procedureName = "USP_CHUNK_DISP_GSTR2A_B2B", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "gstr2aB2BCount", procedureName = "USP_INS_CHUNK_GSTR2A_B2B_STATUS_REPORT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr2aB2BData", procedureName = "USP_CHUNK_DISP_GSTR2A_B2B_STATUS_REPORT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "gstr2aCDNCount", procedureName = "USP_INS_CHUNK_GSTR2A_CDN_STATUS_REPORT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr2aCDNData", procedureName = "USP_CHUNK_DISP_GSTR2A_CDN_STATUS_REPORT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "einvSummaryRespChunkCount", procedureName = "USP_INS_CHUNK_EINV_RECON_SMRY_RESP_RPT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "einvSummaryRespChunkData", procedureName = "USP_CHUNK_DISP_EINV_RECON_SMRY_RESP_RPT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_NUM", type = Integer.class) }),
		
		@NamedStoredProcedureQuery(name = "gstr1AEntitySummaryRespChunkData", procedureName = "USP_GSTR1A_ENTITY_LEVEL_SUMMARY", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "HSN_RATE", type = Boolean.class) }),

		
		@NamedStoredProcedureQuery(name = "gstr1EntitySummaryRespChunkData", procedureName = "USP_GSTR1_ENTITY_LEVEL_SUMMARY", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "HSN_RATE", type = Boolean.class) }),
		@NamedStoredProcedureQuery(name = "vendorComplianceRating", procedureName = "USP_VR_COMPLAINCE_RATING", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_GSTIN", type = String.class) }),
		@NamedStoredProcedureQuery(name = "vendorComplianceSummary", procedureName = "USP_VR_COMPLAINCE_SUMMARY", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_GSTIN", type = String.class) }),

		@NamedStoredProcedureQuery(name = "apiVendorComplianceRating", procedureName = "USP_VR_ASYNC_COMPLAINCE_RATING", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_GSTIN", type = String.class) }),
		
		@NamedStoredProcedureQuery(name = "apiVendorComplianceRatingPrevFy", procedureName = "USP_VR_COMPLAINCE_RATING_PREVIOUS", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_GSTIN", type = String.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_OLD_FY", type = String.class)}),
		
		@NamedStoredProcedureQuery(name = "gstr3b180DaysReversalResp", procedureName = "USP_GSTR_3B_180_DAYS_REV_RESP_RPT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_GSTIN", type = String.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_TAX_PERIOD", type = String.class) }),

		@NamedStoredProcedureQuery(name = "stockTransferRespChunkData", procedureName = "USP_CHUNK_DISP_OD_PS_ASP_STOCK_TRANSFER", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_COLUMN_ID", type = Long.class) }),

		@NamedStoredProcedureQuery(name = "stockTransferRespChunkCount", procedureName = "USP_INS_CHUNK_OD_PS_ASP_STOCK_TRANSFER", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),

		@NamedStoredProcedureQuery(name = "ewbChunkData", procedureName = "USP_EWB_DISP_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "ewbChunkCount", procedureName = "USP_EWB_INS_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_SPILIT_VAL", type = Integer.class) }), 
		
		@NamedStoredProcedureQuery(name = "180daysPaymentReferenceChunkCount", procedureName = "USP_RECON_180_DAYS_INS_CHUNK_DATA_ERR_PSD", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "180daysPaymentReferenceDataTotal", procedureName = "USP_RECON_180_DAYS_DISP_CHUNK_DATA_ERR_PSD", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VAL", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "180daysPaymentReferenceDataProcessed", procedureName = "USP_RECON_180_DAYS_DISP_CHUNK_DATA_PSD", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VAL", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "180daysPaymentReferenceDataError", procedureName = "USP_RECON_180_DAYS_DISP_CHUNK_DATA_ERR", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VAL", type = Integer.class) }), 
		
		@NamedStoredProcedureQuery(name = "gstr2ConslidatedErrorChunkCount", procedureName = "USP_PR_SMRY_ERR_INS_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr2ConslidatedErrorChunkData", procedureName = "USP_PR_SMRY_ERR_DISP_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		
		
		@NamedStoredProcedureQuery(name = "gstr2ReconTaggingReportChunkCount", procedureName = "USP_PR_RECON_TAG_RPT_INS_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr2ReconTaggingReportChunkData", procedureName = "USP_PR_RECON_TAG_RPT_DISP_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "gstr2ReconTaggingReportChunkOrder", procedureName = "USP_PR_RECON_TAG_RPT_UPD_CHUNK_ORDER", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr2ReconTaggingReportDataLoad", procedureName = "USP_PR_RECON_TAG_RPT_INSERT", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		
		
		
		@NamedStoredProcedureQuery(name = "gstr1aInsertChunkData", procedureName = "USP_INSERT_CHUNK_DATA_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr1aDataStatusChunkCount", procedureName = "USP_INS_CHUNK_OD_DATA_STATUS_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr1aCunkErrorData", procedureName = "USP_CHUNK_DISP_ERROR_DATA_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "dataStatusChunkData1a", procedureName = "USP_CHUNK_DISP_OD_DATA_STATUS_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		
		@NamedStoredProcedureQuery(name = "gstr1aOutwardFileStatusEinvChunkDataCount", procedureName = "USP_INSERT_CHUNK_DATA_EINV_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "gstr1aOutwardFileStatusEinvChunkData", procedureName = "USP_CHUNK_DISP_EINV_DATA_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "outwardFileStatusEwbChunkDataCountGstr1A", procedureName = "USP_INSERT_CHUNK_DATA_EWB_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "outwardFileStatusEwbChunkDataGstr1A", procedureName = "USP_CHUNK_DISP_DATA_EWB_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		@NamedStoredProcedureQuery(name = "outwardFileStatusRetChunkDataCount1a", procedureName = "USP_INSERT_CHUNK_FILE_RETURNS_DATA_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		@NamedStoredProcedureQuery(name = "outwardFileStatusRetChunkData1a", procedureName = "USP_CHUNK_DISP_FILE_RETURNS_DATA_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "Gstr1aCustReportData", procedureName = "USP_DY_CHUNK_DISP_OD_PS_ASP_PROCESS_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_COLUMN_ID", type = Long.class) }),
		
		@NamedStoredProcedureQuery(name = "processedGstr1aChunkCount", procedureName = "USP_INS_CHUNK_OD_PS_ASP_PROCESS_UPLOADED_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		
		@NamedStoredProcedureQuery(name = "gstnGstr1aChunkData", procedureName = "USP_CHUNK_DISP_OD_PS_GSTN_ERROR_CONSOLIDATED_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		
		@NamedStoredProcedureQuery(name = "gstnGstr1aChunkCount", procedureName = "USP_INS_CHUNK_OD_PS_GSTN_ERROR_CONSOLIDATED_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		
		@NamedStoredProcedureQuery(name = "errorGstr1aChunkData", procedureName = "USP_CHUNK_DISP_OD_PS_CONS_ASP_ERROR_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		
		@NamedStoredProcedureQuery(name = "errorGstr1aChunkCount", procedureName = "USP_INS_CHUNK_OD_PS_CONS_ASP_ERROR_1A", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class) }),
		
		@NamedStoredProcedureQuery(name = "imsFileStatusChunkCount", procedureName = "USP_IMS_TOT_PSD_ERR_INS_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_SPILIT_VAL", type = Integer.class) }),
		
		@NamedStoredProcedureQuery(name = "imsFileStatusPsdChunkData", procedureName = "USP_IMS_PSD_DISP_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "imsFileStatusErrorChunkData", procedureName = "USP_IMS_ERR_DISP_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),

		@NamedStoredProcedureQuery(name = "imsFileStatusTotalChunkData", procedureName = "USP_IMS_TOTAL_DISP_CHUNK", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_DOWNLOAD_ID", type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_CHUNK_VALUE", type = Integer.class) }),
		
		@NamedStoredProcedureQuery(
			    name = "gstr2ReconTaggingcalldataRemoval",
			    procedureName = "USP_PR_RECON_TAG_RPT_RMVL"
			),
		
		@NamedStoredProcedureQuery(name = "callGlReconSrPrDataProc", procedureName = "USP_GL_RECON_SRPR_PSD_DATA", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_RECON_CONFIG_ID", type = Long.class)}),
		
		})

public class FileStatusDownloadReportEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REPORT_DOWNLOAD_ID", nullable = false)
	protected Long id;

	@Expose
	@Column(name = "FILE_ID")
	protected Long fileId;

	@Expose
	@Column(name = "UPLOADED_FILE_NAME")
	protected String upldFileName;

	@Expose
	@Column(name = "REPORT_CATEG")
	protected String reportCateg;

	@Expose
	@Column(name = "REPORT_TYPE")
	protected String reportType;

	@Expose
	@Column(name = "DATA_TYPE")
	protected String dataType;

	@Expose
	@Column(name = "REPORT_STATUS")
	protected String reportStatus;

	@Expose
	@Column(name = "FILE_PATH")
	protected String filePath;

	@Expose
	@Column(name = "STATUS")
	protected String status;

	@Expose
	@Column(name = "ENTITY_ID")
	protected Long entityId;

	@Expose
	@Column(name = "GSTIN_IDS")
	protected Clob gstins;

	@Expose
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@Column(name = "SUBDIVISION")
	protected String subdivision;

	@Expose
	@Column(name = "PROFIT_CENTRE")
	protected String profitCenter;

	@Expose
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@Column(name = "PLANT_CODE")
	protected String plantCode;

	@Expose
	@Column(name = "SALES_ORGANIZATION")
	protected String salesOrg;

	@Expose
	@Column(name = "DISTRIBUTION_CHANNEL")
	protected String disChannel;

	@Expose
	@Column(name = "USERACCESS1")
	protected String usrAcs1;

	@Expose
	@Column(name = "USERACCESS2")
	protected String usrAcs2;

	@Expose
	@Column(name = "USERACCESS3")
	protected String usrAcs3;

	@Expose
	@Column(name = "USERACCESS4")
	protected String usrAcs4;

	@Expose
	@Column(name = "USERACCESS5")
	protected String usrAcs5;

	@Expose
	@Column(name = "USERACCESS6")
	protected String usrAcs6;

	@Expose
	@Column(name = "PURCHASE_ORGANIZATION")
	protected String purchaseOrg;

	@Expose
	@Column(name = "REQUEST_FROM_DATE")
	protected LocalDateTime requestFromDate;

	@Expose
	@Column(name = "REQUEST_TO_DATE")
	protected LocalDateTime requestToDate;

	@Expose
	@Column(name = "COMPLETED_ON")
	protected LocalDateTime completedOn;

	@Expose
	@Column(name = "TAX_PERIOD")
	protected String taxPeriod;

	@Expose
	@Column(name = "DERIVED_RET_PERIOD")
	protected Long derivedRetPeriod;

	@Expose
	@Column(name = "CREATED_DATE")
	protected LocalDateTime createdDate;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@Column(name = "DERIVED_RET_PERIOD_FROM")
	protected Long derivedRetPeriodFrom;

	@Expose
	@Column(name = "DERIVED_RET_PERIOD_TO")
	protected Long derivedRetPeriodFromTo;

	@Expose
	@Column(name = "DATA_COUNT")
	protected Long dataCount;

	@Expose
	@Column(name = "DOC_DATE_FROM")
	private LocalDate docDateFrom;

	@Expose
	@Column(name = "DOC_DATE_TO")
	protected LocalDate docDateTo;

	@Expose
	@Column(name = "TAX_PERIOD_FROM")
	protected String taxPeriodFrom;

	@Expose
	@Column(name = "TAX_PERIOD_TO")
	protected String taxPeriodTo;

	@Expose
	@Column(name = "TABLE_TYPE")
	protected String tableType;

	@Expose
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@Column(name = "DOC_CATEGORY")
	protected String docCategory;

	@Lob
	@Expose
	@Column(name = "REQ_PAYLOAD")
	protected String reqPayload;

	@Expose
	@Column(name = "EWB_NO_RESP")
	protected String eWbGenerated;

	@Expose
	@Column(name = "IRN_RESPONSE")
	protected String eInvGenerated;

	@Expose
	@Column(name = "DOC_NUM")
	protected String docNum;

	@Expose
	@Column(name = "COUNTER_PARTY_GSTIN")
	protected String counterPartyGstin;

	@Expose
	@Column(name = "TRANS_TYPE")
	protected String transType;

	@Expose
	@Column(name = "SHOW_GSTN_DATA")
	protected boolean showGstnData;

	@Expose
	@Column(name = "PROCESSING_STATUS")
	protected String processingStatus;

	@Expose
	@Column(name = "REF_ID")
	protected String refId;

	@Expose
	@Column(name = "DATA_ORIGIN_TYPE_CODE")
	protected String dataOriginTypeCode;

	@Expose
	@Column(name = "GST_RETURNS_STATUS")
	protected String gstReturnsStatus;

	@Expose
	@Column(name = "EWB_STATUS")
	protected String ewbStatus;

	@Expose
	@Column(name = "INV_STATUS")
	protected String invStatus;

	@Expose
	@Column(name = "EWB_ERROR_POINT")
	protected String ewbErrorPoint;

	@Expose
	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Expose
	@Column(name = "SUB_SUPPLY_TYPE")
	protected String subSupplyType;

	@Expose
	@Column(name = "POSTING_DATE")
	protected LocalDate postingDate;

	@Expose
	@Column(name = "TRANSPORTER_ID")
	protected String transporterId;

	@Expose
	@Column(name = "EWB_VALID_UPTO")
	protected LocalDateTime ewbValidUpto;

	@Expose
	@Column(name = "FI_YEAR")
	protected String fyYear;

	@Expose
	@Column(name = "MONTH")
	protected String month;

	@Expose
	@Column(name = "DATE_TYPE")
	private String criteria;

	@Expose
	@Column(name = "IRN_NUM")
	private String irnNum;

	@Expose
	@Column(name = "SUPPLIER_GSTIN")
	private String suppGstin;

	@Expose
	@Column(name = "PURCHASE_VOUCHER_NUM")
	private String accVoucherNo;

	@Expose
	@Column(name = "GST_RETURN")
	private String gstReturn;

	@Expose
	@Column(name = "USER_RESPONSE")
	private String usrResp;

	@Expose
	@Column(name = "FMRESPONSE")
	private String fmResp;
	
	@Expose
	@Column(name = "DOCUMENT_DATE_FROM")
	protected LocalDateTime documentDateFrom;

	@Expose
	@Column(name = "DOCUMENT_DATE_TO")
	protected LocalDateTime documentDateTo;
	
	@Expose
	@Column(name = "ACCOUNTING_VOUCHER_DATE_FROM")
	protected LocalDateTime accVoucherDateFrom;
	
	@Expose
	@Column(name = "ACCOUNTING_VOUCHER_DATE_TO")
	protected LocalDateTime accVoucherDateTo;
	
	@Expose
	@Column(name = "VENDOR_PAN")
	private String vendorPan;
	
	@Expose
	@Column(name = "VENDOR_GSTIN")
	private String vendorGstin;
	
	@Expose
	@Column(name = "TAXPERIOD3B_FROM")
	private Integer fromTaxPrd3B;
	
	@Expose
	@Column(name = "TAXPERIOD3B_TO")
	private Integer toTaxPrd3B;
	
	@Expose
	@Column(name = "IRN_STATUS")
	private String irnSts;
	
	@Expose
	@Column(name = "IRN_REPORT_TYPE")
	private String type;
	
	@Expose
	@Column(name = "DOC_ID")
	private String docId;
	
	@Expose
	@Column(name = "LISTING_IDS")
	protected Clob listingIds;
	
	@Expose
	@Column(name = "RECON_CRITERIA")
	private String reconCriteria;
	
	@Expose
	@Column(name = "RECON_TYPE")
	private String reconType;
	
	@Expose
	@Column(name = "RECON_REPORT_TYPE")
	private String reconReportType;
	

}