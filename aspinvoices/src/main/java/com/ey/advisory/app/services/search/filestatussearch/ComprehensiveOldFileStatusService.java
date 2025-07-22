package com.ey.advisory.app.services.search.filestatussearch;

import static com.ey.advisory.common.GSTConstants.ALL;
import static com.ey.advisory.common.GSTConstants.OUTWARD;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.daos.client.ComprehensiveStatusDaoImpl;
import com.ey.advisory.app.data.repositories.client.Anx1OutWardErrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Anx2InwardErrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.FileStatusReqDto;
import com.ey.advisory.app.docs.dto.FileStatusResponseDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("ComprehensiveOldFileStatusService")
@Slf4j
public class ComprehensiveOldFileStatusService implements SearchService {
	@Autowired
	@Qualifier("ComprehensiveStatusDaoImpl")
	private ComprehensiveStatusDaoImpl comprehensiveStatusDaoImpl;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("anx1OutWardErrHeaderRepository")
	private Anx1OutWardErrHeaderRepository anx1OutWardErrHeaderRepository;

	@Autowired
	@Qualifier("Anx2InwardErrHeaderRepository")
	private Anx2InwardErrHeaderRepository anx2InwardErrHeaderRepository;

	@SuppressWarnings({ "unchecked" })
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("fileStatus methods{}");
		}
		FileStatusReqDto req = (FileStatusReqDto) criteria;

		// these two variables define the date of document received
		LocalDate dataRecvFrom = req.getDataRecvFrom();
		LocalDate dataRecvTo = req.getDataRecvTo();
		// these variable define the type of file
		String fileType = req.getFileType();
		String dataType = req.getDataType();
		String source = req.getSource();
		/*
		 * List<String> gstin = req.getFileStatusDataSecReqDto().getGstn();
		 * List<String> plant = req.getFileStatusDataSecReqDto().getPlant();
		 * List<String> pc = req.getFileStatusDataSecReqDto().getPc();
		 * List<String> d = req.getFileStatusDataSecReqDto().getD();
		 * List<String> l = req.getFileStatusDataSecReqDto().getL();
		 * List<String> dc = req.getFileStatusDataSecReqDto().getDc();
		 * List<String> so = req.getFileStatusDataSecReqDto().getSo();
		 * List<String> user1 = req.getFileStatusDataSecReqDto().getUd1();
		 * List<String> user2 = req.getFileStatusDataSecReqDto().getUd2();
		 * 
		 * List<String> user3 = req.getFileStatusDataSecReqDto().getUd3();
		 * List<String> user4 = req.getFileStatusDataSecReqDto().getUd4();
		 * 
		 * List<String> user5 = req.getFileStatusDataSecReqDto().getUd5();
		 * List<String> user6 = req.getFileStatusDataSecReqDto().getUd6();
		 */
		List<Gstr1FileStatusEntity> response = new ArrayList<>();
		List<FileStatusResponseDto> fileStatusResponseDtos = new ArrayList<>();

		StringBuilder build = new StringBuilder();
		if (dataType != null && !dataType.equalsIgnoreCase(ALL)) {
			if (dataType.equalsIgnoreCase(OUTWARD)) {
				build.append(" AND FIL.DATA_TYPE = :dataType ");
			}

		}

		if (fileType != null && !fileType.equalsIgnoreCase(ALL)) {
			build.append(" AND FIL.FILE_TYPE = :fileType ");
		}
		if (dataRecvFrom != null && dataRecvTo != null) {

			build.append(" AND FIL.RECEIVED_DATE BETWEEN :dataRecvFrom "
					+ "AND :dataRecvTo ");
		}
		if (source != null && source
				.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
			build.append(" AND FIL.SOURCE = :source ");
		}
		if (source != null
				&& source.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
			build.append(" AND FIL.SOURCE = :source ");
		}

		String buildQuery = build.toString().substring(4);
		StringBuilder build1 = new StringBuilder();

		if ("COMPREHENSIVE_RAW".equalsIgnoreCase(fileType)) {
			sqlQuery(dataType, build1, buildQuery);
			List<Object[]> list = comprehensiveStatusDaoImpl.fileStatus(
					build1.toString(), dataType.toUpperCase(),
					fileType.toUpperCase(), dataRecvFrom, dataRecvTo, source);

			for (Object[] obj : list) {
				FileStatusResponseDto fileStatusResDto = new FileStatusResponseDto();

				String uploadedBy = (obj[1] != null)
						? String.valueOf(obj[1]).trim() : "";

				String uploadedDate = null;
				if (obj[2] != null) {
					LocalDateTime localDateTime = ((Timestamp) obj[2])
							.toLocalDateTime();
					DateTimeFormatter formatter = DateTimeFormatter
							.ofPattern("yyyy-MM-dd");
					localDateTime = EYDateUtil
							.toISTDateTimeFromUTC(localDateTime);
					uploadedDate = formatter.format(localDateTime);
				}
				Long fileId = null;
				if (obj[3].toString() != null) {
					String idStr = String.valueOf(obj[3]).trim();
					fileId = Long.valueOf(idStr);
				}
				String fileNameRes = (obj[4] != null)
						? String.valueOf(obj[4]).trim() : "";
				String fileStausRes = (obj[5] != null)
						? String.valueOf(obj[5]).trim() : "";

				Integer activeProcessed = 0;
				if (obj[6] != null) {
					String activeProcessedStr = String.valueOf(obj[6]).trim();
					activeProcessed = Integer.valueOf(activeProcessedStr);
				}
				Integer totalRecordRes = 0;
				if (obj[7] != null) {
					String totalRecordStr = String.valueOf(obj[7]).trim();
					totalRecordRes = Integer.valueOf(totalRecordStr);
				}

				Integer inActiveProcessed = 0;
				if (obj[8] != null) {
					String inActiveProcessedStr = String.valueOf(obj[8]).trim();
					inActiveProcessed = Integer.valueOf(inActiveProcessedStr);
				}

				Integer activeError = 0;
				if (obj[9] != null) {
					String activeErrorStr = String.valueOf(obj[9]).trim();
					activeError = Integer.valueOf(activeErrorStr);
				}
				Integer inActiveError = 0;
				if (obj[10] != null) {
					String inActiveErrorStr = String.valueOf(obj[10]).trim();
					inActiveError = Integer.valueOf(inActiveErrorStr);
				}

				Integer eInvNa = 0;
				if (obj[11] != null) {
					String eInvNaStr = String.valueOf(obj[11]).trim();
					eInvNa = Integer.valueOf(eInvNaStr);
				}

				Integer einvErrorGigiGST = 0;
				if (obj[12] != null) {
					String einvErrorGigiGSTStr = String.valueOf(obj[12]).trim();
					einvErrorGigiGST = Integer.valueOf(einvErrorGigiGSTStr);
				}
				Integer einvASPProcessed = 0;
				if (obj[13] != null) {
					String einvASPProcessedStr = String.valueOf(obj[13]).trim();
					einvASPProcessed = Integer.valueOf(einvASPProcessedStr);
				}
				Integer einvINRInitiated = 0;
				if (obj[14] != null) {
					String einvINRInitiatedStr = String.valueOf(obj[14]).trim();
					einvINRInitiated = Integer.valueOf(einvINRInitiatedStr);
				}
				Integer einvError = 0;
				if (obj[15] != null) {
					String einvErrorStr = String.valueOf(obj[15]).trim();
					einvError = Integer.valueOf(einvErrorStr);
				}
				Integer einvGenerated = 0;
				if (obj[16] != null) {
					String einvGeneratedStr = String.valueOf(obj[16]).trim();
					einvGenerated = Integer.valueOf(einvGeneratedStr);
				}
				Integer einvCancelled = 0;
				if (obj[17] != null) {
					String einvCancelledStr = String.valueOf(obj[17]).trim();
					einvCancelled = Integer.valueOf(einvCancelledStr);
				}

				Integer ewbvNa = 0;
				if (obj[18] != null) {
					String ewbStr = String.valueOf(obj[18]).trim();
					ewbvNa = Integer.valueOf(ewbStr);
				}

				Integer ewbErrorGigiGST = 0;
				if (obj[19] != null) {
					String ewbErrorGigiGSTStr = String.valueOf(obj[19]).trim();
					ewbErrorGigiGST = Integer.valueOf(ewbErrorGigiGSTStr);
				}
				Integer ewbASPProcessed = 0;
				if (obj[20] != null) {
					String ewbASPProcessedStr = String.valueOf(obj[20]).trim();
					ewbASPProcessed = Integer.valueOf(ewbASPProcessedStr);
				}
				Integer ewbINRInitiated = 0;
				if (obj[21] != null) {
					String ewbINRInitiatedStr = String.valueOf(obj[21]).trim();
					ewbINRInitiated = Integer.valueOf(ewbINRInitiatedStr);
				}

				Integer ewbError = 0;
				if (obj[22] != null) {
					String ewbErrorStr = String.valueOf(obj[22]).trim();
					ewbError = Integer.valueOf(ewbErrorStr);
				}

				Integer ewbGenerated = 0;
				if (obj[23] != null) {
					String ewbErrorStr = String.valueOf(obj[23]).trim();
					ewbGenerated = Integer.valueOf(ewbErrorStr);
				}

				Integer ewbCancelled = 0;
				if (obj[24] != null) {
					String ewbCancelledStr = String.valueOf(obj[24]).trim();
					ewbCancelled = Integer.valueOf(ewbCancelledStr);
				}

				Integer gstnNa = 0;
				if (obj[25] != null) {
					String gstnStr = String.valueOf(obj[25]).trim();
					gstnNa = Integer.valueOf(gstnStr);
				}

				Integer gstnErrorGigiGST = 0;
				if (obj[26] != null) {
					String gstnErrorGigiGSTStr = String.valueOf(obj[26]).trim();
					gstnErrorGigiGST = Integer.valueOf(gstnErrorGigiGSTStr);
				}
				Integer gstnASPProcessed = 0;
				if (obj[27] != null) {
					String gstnASPProcessedStr = String.valueOf(obj[27]).trim();
					gstnASPProcessed = Integer.valueOf(gstnASPProcessedStr);
				}
				Integer gSTNSaveInitiated = 0;
				if (obj[28] != null) {
					String gstnINRInitiatedStr = String.valueOf(obj[28]).trim();
					gSTNSaveInitiated = Integer.valueOf(gstnINRInitiatedStr);
				}

				Integer pushedToGstn = 0;
				if (obj[29] != null) {
					String gstnSavedStr = String.valueOf(obj[29]).trim();
					pushedToGstn = Integer.valueOf(gstnSavedStr);
				}

				Integer gstnGenerated = 0;
				if (obj[31] != null) {
					String gstnGeneratedStr = String.valueOf(obj[31]).trim();
					gstnGenerated = Integer.valueOf(gstnGeneratedStr);
				}
				Integer gstnError = 0;
				if (obj[32] != null) {
					String gstnErrorStr = String.valueOf(obj[32]).trim();
					gstnError = Integer.valueOf(gstnErrorStr);
				}
				Integer gstnCancelled = 0;
				if (obj[33] != null) {
					String gstnErrorStr = String.valueOf(obj[33]).trim();
					gstnCancelled = Integer.valueOf(gstnErrorStr);
				}

				String fileTypeRes = (obj[34] != null)
						? String.valueOf(obj[34]).trim() : "";
				String dataTypeRes = (obj[35] != null)
						? String.valueOf(obj[35]).trim() : "";

				Integer strucError = 0;
				if (obj[36] != null) {
					String strucErrorStr = String.valueOf(obj[36]).trim();
					strucError = Integer.valueOf(strucErrorStr);
				}

				Integer totalStrucBusinessError = 0;
				if (obj[37] != null) {
					String totalStrucBusinessErrorStr = String.valueOf(obj[37])
							.trim();
					totalStrucBusinessError = Integer
							.valueOf(totalStrucBusinessErrorStr);
				}
				Integer infoActive = 0;
				if (obj[38] != null) {
					String infoActiveStr = String.valueOf(obj[38]).trim();
					infoActive = Integer.valueOf(infoActiveStr);
				}
				Integer infoInactive = 0;
				if (obj[39] != null) {
					String infoInactiveStr = String.valueOf(obj[39]).trim();
					infoInactive = Integer.valueOf(infoInactiveStr);
				}

				fileStatusResDto.setId(fileId);
				fileStatusResDto.setUploadedOn(uploadedDate);
				fileStatusResDto.setUploadedBy(uploadedBy);
				fileStatusResDto.setFileType(fileTypeRes);
				fileStatusResDto.setFileName(fileNameRes);
				fileStatusResDto.setDataType(dataTypeRes);
				fileStatusResDto.setFileStatus(fileStausRes);
				fileStatusResDto.setTotal(totalRecordRes);
				fileStatusResDto.setProcessedActive(activeProcessed);
				fileStatusResDto.setProcessedInactive(inActiveProcessed);
				fileStatusResDto.setStrucError(strucError);
				fileStatusResDto.setErrorsActive(activeError);
				fileStatusResDto.setErrorsInactive(inActiveError);
				fileStatusResDto
						.setTotalStrucBusinessError(totalStrucBusinessError);
				fileStatusResDto.setInfoActive(infoActive);
				// fileStatusResDto.setInfoInactive(infoActive);
				fileStatusResDto.setEnivNA(eInvNa);
				fileStatusResDto.setEinvErrorGigiGST(einvErrorGigiGST);
				// fileStatusResDto.setEinvASPProcessed(einvASPProcessed);
				fileStatusResDto.setEinvINRInitiated(einvINRInitiated);
				fileStatusResDto.setEinvGenerated(einvGenerated);
				fileStatusResDto.setEinvError(einvError);

				fileStatusResDto.setEwbNA(ewbvNa);
				fileStatusResDto.setEWBErrorGigiGST(ewbErrorGigiGST);
				// fileStatusResDto.setEWBASPProcessed(ewbASPProcessed);
				fileStatusResDto.setEWBInitiated(ewbINRInitiated);
				fileStatusResDto.setEWBGenerated(ewbGenerated);
				fileStatusResDto.setEWBError(ewbError);

				/*
				 * fileStatusResDto.setGstnNA(gstnNa);
				 * fileStatusResDto.setGSTNErrorGigiGST(gstnErrorGigiGST);
				 * fileStatusResDto.setGSTNASPProcessed(gstnASPProcessed);
				 * fileStatusResDto.setGstnSaveINRInitiated(gSTNSaveInitiated);
				 * fileStatusResDto.setGSTNGenerated(gstnGenerated);
				 * fileStatusResDto.setGstnError(gstnError);
				 */

				fileStatusResponseDtos.add(fileStatusResDto);

			}
			return (SearchResult<R>) new SearchResult<>(fileStatusResponseDtos);
		} else {

			if (dataType != null && !dataType.equalsIgnoreCase(ALL)) {
				if (dataType.equalsIgnoreCase(OUTWARD)) {
					build.append(" AND FIL.DATA_TYPE = :dataType ");
				}
				/*
				 * if (dataType.equalsIgnoreCase(INWARD)) {
				 * build.append(" AND DATA_TYPE = :dataType"); } if
				 * (dataType.equalsIgnoreCase(GSTConstants.RET)) {
				 * build.append(" AND DATA_TYPE = :dataType"); }
				 */
			}

			if (fileType != null && !fileType.equalsIgnoreCase(ALL)) {
				build.append(" AND FIL.FILE_TYPE = :fileType ");
			}
			if (dataRecvFrom != null && dataRecvTo != null) {

				build.append(" AND FIL.RECEIVED_DATE BETWEEN :dataRecvFrom "
						+ "AND :dataRecvTo ORDER BY ID DESC ");
			}

			// String buildQuery = build.toString().substring(4);

			if ((dataRecvFrom != null && dataRecvFrom.lengthOfYear() > 0)
					&& (dataRecvTo != null && dataRecvTo.lengthOfYear() > 0)) {
				response = comprehensiveStatusDaoImpl.fileStatusSection(
						"DATARECV", buildQuery, dataRecvFrom, dataRecvTo,
						fileType, dataType, source);
			}

			return (SearchResult<R>) new SearchResult<>(response);
		}

	}

	private void sqlQuery(String dataType, StringBuilder build,
			String buildQuery) {
		build.append("SELECT RECEIVED_DATE,CREATED_BY,CREATED_ON,ID ,");
		build.append(
				"FILE_NAME,FILE_STATUS,SUM(ASP_ACTIVE_PROCESSED) AS ASP_ACTIVE_PROCESED,");
		build.append("SUM(TTL) AS TOTAL_CNT,  ");
		build.append("SUM(ASP_INACTIVE_PROCESSED) AS ASP_INACTIVE__PROCESSED,");
		build.append("SUM(ASP_ACTIVE_ERROR) AS ASP__ACTIVE_ERROR,  ");
		build.append("SUM(ASP_INACTIVE_ERROR) AS ASP__INACTIVE_ERROR,  ");
		build.append("SUM(INV_NOT_APPLICABLE) AS INV_NOT__APPLICABLE,   ");
		build.append("SUM(INV_ASP_ERROR) AS INV_ASP_ERR,  ");
		build.append("SUM(INV_ASP_PROCESSED) AS INV_ASP__PROCESSED,   ");
		build.append("SUM(INV_IRN_IN_PROGRESS) AS INV_IRN_INPROGRESS,  ");
		build.append("SUM(INV_IRN_ERROR) AS INV_IRNERROR, ");
		build.append("SUM(INV_IRN_PROCESSED) AS INV_IRNPROCESSED, ");
		build.append("SUM(INV_IRN_CANCELLED) AS INV_IRNCANCELLED,   ");
		build.append("SUM(EWB_NOT_APPLICABLE) AS EWB_NOTAPPLICABLE, ");
		build.append("SUM(EWB_ASP_ERROR) AS EWB_ASPERROR,  ");
		build.append("SUM(EWB_ASP_PROCESSED) AS EWB_ASPPROCESSED,   ");
		build.append("SUM(EWB_IRN_IN_PROGRESS) AS EWB_IRN_INPROGRESS,  ");
		build.append("SUM(EWB_IRN_ERROR) AS EWB_IRN__ERROR,   ");
		build.append("SUM(EWB_IRN_PROCESSED) AS EWBIRN_PROCESSED,   ");
		build.append("SUM(IRN_CANCELLED) AS IRN__CANCELLED,  ");
		build.append("SUM(GSTN_NOT_APPLICABLE) AS GSTN_NOTAPPLICABLE,  ");
		build.append("SUM(GSTN_ASP_ERROR) AS GSTN_ASPERROR,  ");
		build.append("SUM(GSTN_ASP_PROCESSED) AS GSTN_ASPPROCESSED,   ");
		build.append("SUM(NOT_PUSHED_TO_GSTN) AS NOT_PUSHED_TOGSTN,  ");
		build.append("SUM(PUSHED_TO_GSTN) AS PUSHED_TOGSTN, ");
		build.append("SUM(PUSHED_TO_GSTN) AS PUSHED_TO_GSTN,  ");
		build.append("SUM(SAVED_TO_GSTN) AS SAVED_TO_GSTN,  ");
		build.append("SUM(ERRORS_GSTN) AS ERRORS_GSTN, ");
		build.append("SUM(GSTN_IRN_CANCELLED) AS GSTN_IRNCANCELLED,  ");
		build.append("FILE_TYPE,DATA_TYPE,SUM(SV_ERROR) AS SV_ERROR,  ");
		build.append("SUM(ASP_ACTIVE_ERROR + SV_ERROR) AS TOTAL_SV_BVERROR,  ");
		build.append("SUM(INFO_ACTIVE) AS INFO__ACTIVE, ");
		build.append("SUM(INFO_INACTIVE) AS INFO__INACTIVE FROM  ");
		build.append(
				"(SELECT FIL.RECEIVED_DATE,FIL.CREATED_BY,FIL.CREATED_ON,FIL.ID,  ");
		build.append("FILE_NAME,FILE_STATUS, COUNT(*) AS TOTAL_COUNT, ");
		build.append(
				"COUNT(CASE WHEN IS_PROCESSED = TRUE AND IS_DELETE =  FALSE THEN 1 ELSE NULL END ) ASP_ACTIVE_PROCESSED,  ");
		build.append(
				"COUNT(CASE WHEN IS_PROCESSED = TRUE AND IS_DELETE =  TRUE THEN 1 ELSE NULL END ) ASP_INACTIVE_PROCESSED, ");
		build.append(
				"COUNT(CASE WHEN IS_ERROR = TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END  ) ASP_ACTIVE_ERROR, ");
		build.append(
				"COUNT(CASE WHEN IS_ERROR = TRUE AND IS_DELETE = TRUE THEN 1 ELSE NULL END  ) ASP_INACTIVE_ERROR,  ");
		build.append(
				"COUNT(CASE WHEN EINV_STATUS = 1 THEN 1 ELSE NULL END  ) INV_NOT_APPLICABLE,  ");
		build.append(
				"COUNT(CASE WHEN EINV_STATUS = 2 THEN 1 ELSE NULL END  ) INV_ASP_ERROR,  ");
		build.append(
				"COUNT(CASE WHEN EINV_STATUS = 3 THEN 1 ELSE NULL END  ) INV_ASP_PROCESSED, ");
		build.append(
				"COUNT(CASE WHEN EINV_STATUS = 4 THEN 1 ELSE NULL END  ) INV_IRN_IN_PROGRESS,  ");
		build.append(
				"COUNT(CASE WHEN EINV_STATUS = 5 THEN 1 ELSE NULL END  ) INV_IRN_ERROR, ");
		build.append(
				"COUNT(CASE WHEN EINV_STATUS = 6 THEN 1 ELSE NULL END  ) INV_IRN_PROCESSED,  ");
		build.append(
				"COUNT(CASE WHEN EINV_STATUS = 7 THEN 1 ELSE NULL END  ) INV_IRN_CANCELLED,  ");
		build.append(
				"COUNT(CASE WHEN EWB_STATUS = 1 THEN 1 ELSE NULL END  ) EWB_NOT_APPLICABLE,  ");
		build.append(
				"COUNT(CASE WHEN EWB_STATUS = 2 THEN 1 ELSE NULL END  ) EWB_ASP_ERROR,  ");
		build.append(
				"COUNT(CASE WHEN EWB_STATUS = 3 THEN 1 ELSE NULL END  ) EWB_ASP_PROCESSED, ");
		build.append(
				"COUNT(CASE WHEN EWB_STATUS = 4 THEN 1 ELSE NULL END  ) EWB_IRN_IN_PROGRESS,  ");
		build.append(
				"COUNT(CASE WHEN EWB_STATUS = 5 THEN 1 ELSE NULL END  ) EWB_IRN_ERROR,  ");
		build.append(
				"COUNT(CASE WHEN EWB_STATUS = 6 THEN 1 ELSE NULL END  ) EWB_IRN_PROCESSED,  ");
		build.append(
				"COUNT(CASE WHEN EWB_STATUS = 7 THEN 1 ELSE NULL END  ) IRN_CANCELLED,  ");
		build.append("0 GSTN_NOT_APPLICABLE, ");
		build.append(
				"COUNT(CASE WHEN IS_ERROR = TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END  ) GSTN_ASP_ERROR,  ");
		build.append(
				"COUNT(CASE WHEN IS_PROCESSED = TRUE AND IS_DELETE =  FALSE THEN 1 ELSE NULL END ) GSTN_ASP_PROCESSED,  ");
		build.append(
				"COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE AND IS_DELETE =  FALSE THEN 1 ELSE NULL END ) NOT_PUSHED_TO_GSTN,  ");
		build.append(
				"COUNT(CASE WHEN IS_SENT_TO_GSTN = TRUE AND IS_DELETE =  FALSE THEN 1 ELSE NULL END ) PUSHED_TO_GSTN, ");
		build.append(
				"COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND IS_DELETE =  FALSE THEN 1 ELSE NULL END )  SAVED_TO_GSTN,  ");
		build.append(
				"COUNT(CASE WHEN GSTN_ERROR = TRUE AND IS_DELETE =  FALSE THEN 1 ELSE NULL END )  ERRORS_GSTN,  ");
		build.append("0 GSTN_IRN_CANCELLED,FILE_TYPE,DATA_TYPE,  ");
		build.append("0 AS  SV_ERROR,0 AS TOTAL_SV_BV_ERROR,  ");
		build.append(
				"COUNT(CASE WHEN IS_PROCESSED = TRUE AND HDR.IS_INFORMATION = TRUE AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) as INFO_ACTIVE, ");
		build.append(
				"COUNT(CASE WHEN IS_PROCESSED = TRUE AND HDR.IS_INFORMATION = TRUE AND HDR.IS_DELETE = TRUE THEN 1 ELSE NULL END) as INFO_INACTIVE  , ");
		build.append(
				"COUNT(CASE WHEN IS_PROCESSED = TRUE OR HDR.IS_ERROR  = TRUE THEN 1 ELSE NULL END) AS TTL ");
		build.append("FROM  FILE_STATUS FIL  ");
		build.append(
				"LEFT OUTER JOIN ANX_OUTWARD_DOC_HEADER HDR ON HDR.FILE_ID = FIL.ID  WHERE ");
		build.append(buildQuery);
		build.append(
				"GROUP BY  FIL.RECEIVED_DATE,FIL.CREATED_BY,FIL.CREATED_ON,FILE_NAME,FILE_STATUS,FILE_TYPE,DATA_TYPE,FIL.ID  ");
		build.append("UNION ALL  ");
		build.append(
				"SELECT FIL.RECEIVED_DATE,FIL.CREATED_BY,FIL.CREATED_ON,FIL.ID,FILE_NAME,FILE_STATUS, COUNT(*) AS TOTAL_COUNT,  ");
		build.append(
				"0 AS ASP_ACTIVE_PROCESSED, 0 AS ASP_INACTIVE_PROCESSED,0 AS ASP_ACTIVE_ERROR,0 AS ASP_INACTIVE_ERROR,  ");
		build.append(
				"0 AS INV_NOT_APPLICABLE,0 AS INV_ASP_ERROR,0 AS INV_ASP_PROCESSED,0 AS INV_IRN_IN_PROGRESS,0 AS INV_IRN_ERROR,  ");
		build.append(
				"0 AS INV_IRN_PROCESSED,0 AS INV_IRN_CANCELLED,0 AS EWB_NOT_APPLICABLE,  ");
		build.append(
				"0 AS EWB_ASP_ERROR,0 AS EWB_ASP_PROCESSED,0 AS EWB_IRN_IN_PROGRESS,");
		build.append(
				"0 AS EWB_IRN_ERROR,0 AS EWB_IRN_PROCESSED,0 AS IRN_CANCELLED,0 GSTN_NOT_APPLICABLE,0 AS GSTN_ASP_ERROR, ");
		build.append(
				"0 AS GSTN_ASP_PROCESSED,0 AS NOT_PUSHED_TO_GSTN,0 AS PUSHED_TO_GSTN,0 AS  SAVED_TO_GSTN,0 AS  ERRORS_GSTN,  ");
		build.append(
				"0 GSTN_IRN_CANCELLED,FILE_TYPE,DATA_TYPE,COUNT(CASE WHEN IS_ERROR = 'true' THEN 1 ELSE NULL END  ) SV_ERROR,  ");
		build.append(
				"0 AS TOTAL_SV_BV_ERROR,0 as INFO_ACTIVE,0 AS  INFO_INACTIVE,  ");
		build.append("COUNT(HDR.ID) AS TTL ");
		build.append(
				"FROM FILE_STATUS FIL LEFT OUTER JOIN  ANX_OUTWARD_ERR_HEADER HDR  ON HDR.FILE_ID = FIL.ID  WHERE ");
		build.append(buildQuery);
		build.append(
				"GROUP BY  FIL.RECEIVED_DATE,FIL.CREATED_BY,FIL.CREATED_ON,FILE_NAME,FILE_STATUS,FILE_TYPE,DATA_TYPE,FIL.ID  ");
		build.append(
				")GROUP BY  RECEIVED_DATE,CREATED_BY,CREATED_ON,FILE_NAME,FILE_STATUS,FILE_TYPE,DATA_TYPE , ID  ORDER BY ID DESC  ");
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		return null;
	}

}
