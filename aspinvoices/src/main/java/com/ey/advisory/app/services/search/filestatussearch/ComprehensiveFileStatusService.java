package com.ey.advisory.app.services.search.filestatussearch;

import static com.ey.advisory.common.GSTConstants.ALL;
import static com.ey.advisory.common.GSTConstants.OUTWARD;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
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
import com.ey.advisory.common.GSTConstants;
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
@Component("ComprehensiveFileStatusService")
@Slf4j
public class ComprehensiveFileStatusService implements SearchService {
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
		List<Gstr1FileStatusEntity> response = new ArrayList<>();
		try {
			FileStatusReqDto req = (FileStatusReqDto) criteria;

			// these two variables define the date of document received
			LocalDate dataRecvFrom = req.getDataRecvFrom();
			LocalDate dataRecvTo = req.getDataRecvTo();
			// these variable define the type of file
			String fileType = req.getFileType();
			String dataType = req.getDataType();
			String source = req.getSource();

			List<FileStatusResponseDto> fileStatusResponseDtos = new ArrayList<>();
			StringBuilder build = new StringBuilder();
			StringBuilder build1 = new StringBuilder();

			if (dataType != null && !dataType.equalsIgnoreCase(ALL)) {
				if (dataType.equalsIgnoreCase(OUTWARD)) {
					build.append(" AND DATA_TYPE = :dataType ");
					build1.append("AND FIL.DATA_TYPE = :dataType ");
				}
				if (dataType.equalsIgnoreCase(GSTConstants.GSTR1)) {
					build.append(" AND DATA_TYPE = :dataType ");
					build1.append(" AND FIL.DATA_TYPE = :dataType ");

				}
				
				if (dataType.equalsIgnoreCase("outward_1A")) {
					build.append(" AND DATA_TYPE = :dataType ");
					build1.append("AND FIL.DATA_TYPE = :dataType ");
				}
			}
			if (fileType != null && !fileType.equalsIgnoreCase(ALL)) {
				build.append(" AND FILE_TYPE = :fileType ");
				build1.append(" AND FIL.FILE_TYPE = :fileType ");

			}
			if (dataRecvFrom != null && dataRecvTo != null) {

				build.append(" AND RECEIVED_DATE BETWEEN :dataRecvFrom "
						+ "AND :dataRecvTo ");

				build1.append(" AND FIL.RECEIVED_DATE BETWEEN :dataRecvFrom "
						+ "AND :dataRecvTo ");

			}

			if (source != null && source
					.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
				build.append(" AND SOURCE = :source ");
				build1.append(" AND FIL.SOURCE = :source ");

			}

			if (source != null
					&& source.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
				build.append(" AND SOURCE = :source ");
				build1.append(" AND FIL.SOURCE = :source ");

			}

			String buildQuery = build.toString().substring(4);
			String buildQuery1 = build1.toString().substring(4);
			String sql = null;

			if (GSTConstants.COMPREHENSIVE_RAW.equalsIgnoreCase(fileType)
					|| GSTConstants.RAW.equalsIgnoreCase(fileType)
					|| GSTConstants.COMPREHENSIVE_RAW_1A
							.equalsIgnoreCase(fileType)) {

				if (GSTConstants.COMPREHENSIVE_RAW.equalsIgnoreCase(fileType))
					sql = sqlQuery(buildQuery);
				else
					sql = sqlGstr1AQuery(buildQuery);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("sql1 -> " + sql);
				}
				List<Object[]> list = comprehensiveStatusDaoImpl.fileStatus(sql,
						dataType.toUpperCase(), fileType.toUpperCase(),
						dataRecvFrom, dataRecvTo, source);

				Map<String, List<FileStatusResponseDto>> mapfileStatusEwbDto = mapEWBStatus(
						dataRecvFrom, dataRecvTo, fileType, dataType, source,
						buildQuery1);

				for (Object[] obj : list) {

					FileStatusResponseDto fileStatusResDto = new FileStatusResponseDto();

					String uploadedBy = (obj[1] != null)
							? String.valueOf(obj[1]).trim() : "";

					String uploadedDate = null;
					if (obj[0] != null) {
						String dateTime = String.valueOf(obj[0]);
						/*DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter
								.ofPattern("dd.MM.yyyy");
						uploadedDate = (LocalDate.parse(dateTime, DATE_FORMAT1))
								.toString();
								
								
*/
						uploadedDate = dateTime;
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
						String activeProcessedStr = String.valueOf(obj[6])
								.trim();
						activeProcessed = Integer.valueOf(activeProcessedStr);
					}
					Integer totalRecordRes = 0;
					if (obj[7] != null) {
						String totalRecordStr = String.valueOf(obj[7]).trim();
						totalRecordRes = Integer.valueOf(totalRecordStr);
					}

					Integer inActiveProcessed = 0;
					if (obj[8] != null) {
						String inActiveProcessedStr = String.valueOf(obj[8])
								.trim();
						inActiveProcessed = Integer
								.valueOf(inActiveProcessedStr);
					}

					Integer activeError = 0;
					if (obj[9] != null) {
						String activeErrorStr = String.valueOf(obj[9]).trim();
						activeError = Integer.valueOf(activeErrorStr);
					}
					Integer inActiveError = 0;
					if (obj[10] != null) {
						String inActiveErrorStr = String.valueOf(obj[10])
								.trim();
						inActiveError = Integer.valueOf(inActiveErrorStr);
					}

					Integer eInvNa = 0;
					if (obj[11] != null) {
						String eInvNaStr = String.valueOf(obj[11]).trim();
						eInvNa = Integer.valueOf(eInvNaStr);
					}

					Integer eInvA = 0;
					if (obj[12] != null) {
						String eInvAStr = String.valueOf(obj[12]).trim();
						eInvA = Integer.valueOf(eInvAStr);
					}

					Integer einvGenerated = 0;
					if (obj[13] != null) {
						String einvGeneratedStr = String.valueOf(obj[13])
								.trim();
						einvGenerated = Integer.valueOf(einvGeneratedStr);
					}

					Integer einvINRInitiated = 0;
					if (obj[14] != null) {
						String einvINRInitiatedStr = String.valueOf(obj[14])
								.trim();
						einvINRInitiated = Integer.valueOf(einvINRInitiatedStr);
					}

					Integer einvCancelled = 0;
					if (obj[15] != null) {
						String einvCancelledStr = String.valueOf(obj[15])
								.trim();
						einvCancelled = Integer.valueOf(einvCancelledStr);
					}

					Integer einvErrorGigiGST = 0;
					if (obj[16] != null) {
						String einvErrorGigiGSTStr = String.valueOf(obj[16])
								.trim();
						einvErrorGigiGST = Integer.valueOf(einvErrorGigiGSTStr);
					}

					Integer einvError = 0;
					if (obj[17] != null) {
						String einvErrorStr = String.valueOf(obj[17]).trim();
						einvError = Integer.valueOf(einvErrorStr);
					}

					Integer ewbvNa = 0;
					if (obj[18] != null) {
						String ewbvNaStr = String.valueOf(obj[18]).trim();
						ewbvNa = Integer.valueOf(ewbvNaStr);
					}

					Integer ewbvA = 0;
					if (obj[19] != null) {
						String ewbvAStr = String.valueOf(obj[19]).trim();
						ewbvA = Integer.valueOf(ewbvAStr);
					}

					Integer ewbGenerated = 0;
					if (obj[20] != null) {
						String ewbGeneratedStr = String.valueOf(obj[20]).trim();
						ewbGenerated = Integer.valueOf(ewbGeneratedStr);
					}

					Integer ewbINRInitiated = 0;
					if (obj[21] != null) {
						String ewbINRInitiatedStr = String.valueOf(obj[21])
								.trim();
						ewbINRInitiated = Integer.valueOf(ewbINRInitiatedStr);
					}
					Integer ewbCancelled = 0;
					if (obj[22] != null) {
						String ewbCancelledStr = String.valueOf(obj[22]).trim();
						ewbCancelled = Integer.valueOf(ewbCancelledStr);
					}
					Integer ewbErrorGigiGST = 0;
					if (obj[23] != null) {
						String ewbErrorGigiGSTStr = String.valueOf(obj[23])
								.trim();
						ewbErrorGigiGST = Integer.valueOf(ewbErrorGigiGSTStr);
					}

					Integer ewbError = 0;
					if (obj[24] != null) {
						String ewbErrorStr = String.valueOf(obj[24]).trim();
						ewbError = Integer.valueOf(ewbErrorStr);
					}

					Integer gstnNa = 0;
					if (obj[25] != null) {
						String gstnStr = String.valueOf(obj[25]).trim();
						gstnNa = Integer.valueOf(gstnStr);
					}

					Integer gstnErrorGigiGST = 0;
					if (obj[26] != null) {
						String gstnErrorGigiGSTStr = String.valueOf(obj[26])
								.trim();
						gstnErrorGigiGST = Integer.valueOf(gstnErrorGigiGSTStr);
					}
					Integer gstnASPProcessed = 0;
					if (obj[27] != null) {
						String gstnASPProcessedStr = String.valueOf(obj[27])
								.trim();
						gstnASPProcessed = Integer.valueOf(gstnASPProcessedStr);
					}
					Integer notPushedToGstin = 0;
					if (obj[28] != null) {
						String notPushedToGstinStr = String.valueOf(obj[28])
								.trim();
						notPushedToGstin = Integer.valueOf(notPushedToGstinStr);
					}

					Integer pushedToGstn = 0;
					if (obj[29] != null) {
						String gstnSavedStr = String.valueOf(obj[29]).trim();
						pushedToGstn = Integer.valueOf(gstnSavedStr);
					}

					Integer gstnGenerated = 0;
					if (obj[30] != null) {
						String gstnGeneratedStr = String.valueOf(obj[30])
								.trim();
						gstnGenerated = Integer.valueOf(gstnGeneratedStr);
					}
					Integer gstnError = 0;
					if (obj[31] != null) {
						String gstnErrorStr = String.valueOf(obj[31]).trim();
						gstnError = Integer.valueOf(gstnErrorStr);
					}
					Integer gstnCancelled = 0;
					if (obj[32] != null) {
						String gstnErrorStr = String.valueOf(obj[32]).trim();
						gstnCancelled = Integer.valueOf(gstnErrorStr);
					}

					String fileTypeRes = (obj[33] != null)
							? String.valueOf(obj[33]).trim() : "";
					String dataTypeRes = (obj[34] != null)
							? String.valueOf(obj[34]).trim() : "";

					Integer strucError = 0;
					if (obj[35] != null) {
						String strucErrorStr = String.valueOf(obj[35]).trim();
						strucError = Integer.valueOf(strucErrorStr);
					}

					Integer totalStrucBusinessError = 0;
					if (obj[36] != null) {
						String totalStrucBusinessErrorStr = String
								.valueOf(obj[36]).trim();
						totalStrucBusinessError = Integer
								.valueOf(totalStrucBusinessErrorStr);
					}
					Integer infoActive = 0;
					if (obj[37] != null) {
						String infoActiveStr = String.valueOf(obj[37]).trim();
						infoActive = Integer.valueOf(infoActiveStr);
					}
					Integer infoInactive = 0;
					if (obj[38] != null) {
						String infoInactiveStr = String.valueOf(obj[38]).trim();
						infoInactive = Integer.valueOf(infoInactiveStr);
					}

					Integer ewbGeneratedOnErp = 0;
					if (obj[39] != null) {
						String ewbGeneratedOnErpStr = String.valueOf(obj[39])
								.trim();
						ewbGeneratedOnErp = Integer
								.valueOf(ewbGeneratedOnErpStr);
					}

					Integer ewbNotGeneratedOnErp = 0;
					if (obj[40] != null) {
						String ewbNotGeneratedOnErpStr = String.valueOf(obj[40])
								.trim();
						ewbNotGeneratedOnErp = Integer
								.valueOf(ewbNotGeneratedOnErpStr);
					}

					Integer aspNA = 0;
					if (obj[41] != null) {
						String aspNAStr = String.valueOf(obj[41]).trim();
						aspNA = Integer.valueOf(aspNAStr);
					}

					Integer aspError = 0;
					if (obj[42] != null) {
						String aspErrorStr = String.valueOf(obj[42]).trim();
						aspError = Integer.valueOf(aspErrorStr);
					}

					Integer aspProcess = 0;
					if (obj[43] != null) {
						String aspProcessStr = String.valueOf(obj[43]).trim();
						aspProcess = Integer.valueOf(aspProcessStr);
					}

					Integer aspSaveInitiated = 0;
					if (obj[44] != null) {
						String aspSaveInitiatedStr = String.valueOf(obj[44])
								.trim();
						aspSaveInitiated = Integer.valueOf(aspSaveInitiatedStr);
					}

					Integer aspSavedGstin = 0;
					if (obj[45] != null) {
						String aspSavedGstinStr = String.valueOf(obj[45])
								.trim();
						aspSavedGstin = Integer.valueOf(aspSavedGstinStr);
					}

					Integer aspErrorsGstin = 0;
					if (obj[46] != null) {
						String aspErrorsGstinStr = String.valueOf(obj[46])
								.trim();
						aspErrorsGstin = Integer.valueOf(aspErrorsGstinStr);
					}

					Integer ewbNotOpted = 0;
					if (obj[47] != null) {
						String ewbNotOptedStr = String.valueOf(obj[47]).trim();
						ewbNotOpted = Integer.valueOf(ewbNotOptedStr);
					}

					Integer einvInfoError = 0;
					if (obj[48] != null) {
						String einvInfoErrorStr = String.valueOf(obj[48])
								.trim();
						einvInfoError = Integer.valueOf(einvInfoErrorStr);
					}

					Integer einvNotOpted = 0;
					if (obj[49] != null) {
						String einvNotOptedStr = String.valueOf(obj[49]).trim();
						einvNotOpted = Integer.valueOf(einvNotOptedStr);
					}

					Integer gstnApplicable = 0;
					if (obj[50] != null) {
						String gstnApplicableStr = String.valueOf(obj[50])
								.trim();
						gstnApplicable = Integer.valueOf(gstnApplicableStr);
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
					fileStatusResDto.setTotalStrucBusinessError(
							totalStrucBusinessError);

					fileStatusResDto.setEnivNA(eInvNa);
					fileStatusResDto.setEnivAplicable(eInvA);
					fileStatusResDto.setEinvErrorGigiGST(einvErrorGigiGST);
					fileStatusResDto.setEinvGenerated(einvGenerated);
					fileStatusResDto.setEinvError(einvError);
					fileStatusResDto.setEinvCancelled(einvCancelled);

					fileStatusResDto.setEwbNA(ewbvNa);
					fileStatusResDto.setEwbApplicable(ewbvA);
					fileStatusResDto.setEWBGenerated(ewbGenerated);
					fileStatusResDto.setEwbCancelled(ewbCancelled);
					fileStatusResDto.setEWBErrorGigiGST(ewbErrorGigiGST);
					fileStatusResDto.setEWBError(ewbError);

					fileStatusResDto.setAspNA(aspNA);
					fileStatusResDto.setGstnApplicable(gstnApplicable);
					fileStatusResDto.setAspProcess(aspProcess);
					fileStatusResDto.setInfoActive(infoActive);
					fileStatusResDto.setAspError(aspError);
					
					String transformationStatus = "";
					if (GSTConstants.COMPREHENSIVE_RAW.equalsIgnoreCase(fileType)) {
						transformationStatus = (obj[52] != null) ? String.valueOf(obj[52]).trim() : "";
						fileStatusResDto.setTransformationStatus(transformationStatus);
					} else {
						transformationStatus = (obj[51] != null) ? String.valueOf(obj[51]).trim() : "";
					}
					fileStatusResDto.setTransformationStatus(transformationStatus);
					
					if (fileStausRes.equalsIgnoreCase("Failed")
							&& GSTConstants.COMPREHENSIVE_RAW.equalsIgnoreCase(fileType)) {
						String errDescription = (obj[51] != null) ? String.valueOf(obj[51]).trim() : "";

						Map<String, String> errorMapping = new HashMap<>();
						errorMapping.put("Headers Mismatch", "File uploaded with Unsupported format/Incorrect header");
						errorMapping.put("Incorrect File Name",
								"File name should be in \"MAP_RuleName_FileName\" format.");

						fileStatusResDto.setErrDescription(errorMapping.getOrDefault(errDescription,
								(errDescription.isEmpty()
										? "File failed due to technical reasons, please reach out to Central team"
										: errDescription)) 
						);

					}
							
					
					// This is for getting EWB Id and EINV Id
					StringBuilder ewbStatusFileId = new StringBuilder();
					ewbStatusFileId.append(fileId);
					List<FileStatusResponseDto> fileStatusForEwbs = mapfileStatusEwbDto
							.get(ewbStatusFileId.toString());
					if (fileStatusForEwbs != null
							&& !fileStatusForEwbs.isEmpty()) {
						fileStatusForEwbs.forEach(fileStatusForEwb -> {
							fileStatusResDto
									.setEwbId(fileStatusForEwb.getEwbId());
							fileStatusResDto
									.setEinvId(fileStatusForEwb.getEinvId());
						});
					}
					fileStatusResponseDtos.add(fileStatusResDto);
				}
				return (SearchResult<R>) new SearchResult<>(
						fileStatusResponseDtos);
			} else {

				if (dataType != null && !dataType.equalsIgnoreCase(ALL)) {
					if (dataType.equalsIgnoreCase(OUTWARD)) {
						build.append(" AND DATA_TYPE = :dataType ");
					}
					if (dataType.equalsIgnoreCase(GSTConstants.GSTR1)) {
						build.append(" AND DATA_TYPE = :dataType ");
					}
				}

				if (fileType != null && !fileType.equalsIgnoreCase(ALL)) {
					build.append(" AND FILE_TYPE = :fileType ");
				}
				if (dataRecvFrom != null && dataRecvTo != null) {

					build.append(" AND RECEIVED_DATE BETWEEN :dataRecvFrom "
							+ "AND :dataRecvTo ORDER BY ID DESC ");
				}

				// String buildQuery = build.toString().substring(4);

				if ((dataRecvFrom != null && dataRecvFrom.lengthOfYear() > 0)
						&& (dataRecvTo != null
								&& dataRecvTo.lengthOfYear() > 0)) {
					response = comprehensiveStatusDaoImpl.fileStatusSection(
							"DATARECV", buildQuery, dataRecvFrom, dataRecvTo,
							fileType, dataType, source);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occred:", e);
		}
		return (SearchResult<R>) new SearchResult<>(response);

	}

	private Map<String, List<FileStatusResponseDto>> mapEWBStatus(
			LocalDate dataRecvFrom, LocalDate dataRecvTo, String fileType,
			String dataType, String source, String buildQuery) {
		Map<String, List<FileStatusResponseDto>> mapfileStatusEwbDto = new HashMap<>();
		List<FileStatusResponseDto> fileStatusEwbDtos = new ArrayList<>();
		String sql2 = sqlQuery2(buildQuery);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("sql2 -> " + sql2);
		}
		List<Object[]> listObj2 = comprehensiveStatusDaoImpl.fileStatus(sql2,
				dataType.toUpperCase(), fileType.toUpperCase(), dataRecvFrom,
				dataRecvTo, source);
		listObj2.forEach(obj -> {
			FileStatusResponseDto fileStatusResDto = new FileStatusResponseDto();
			Integer ewbId = 0;
			Integer eInvId = 0;

			if (obj[0] != null) {
				String ewbIdStr = String.valueOf(obj[0]).trim();
				ewbId = Integer.valueOf(ewbIdStr);
			}

			if (obj[1] != null) {
				String eInvIdStr = String.valueOf(obj[1]).trim();
				eInvId = Integer.valueOf(eInvIdStr);
			}
			String uploadedDate = null;
			if (obj[4] != null) {
				LocalDateTime localDateTime = ((Timestamp) obj[4])
						.toLocalDateTime();
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("yyyy-MM-dd");
				localDateTime = EYDateUtil.toISTDateTimeFromUTC(localDateTime);
				uploadedDate = formatter.format(localDateTime);
			}
			Long fileId = null;
			if (obj[5].toString() != null) {
				String idStr = String.valueOf(obj[5]).trim();
				fileId = Long.valueOf(idStr);
			}
			fileStatusResDto.setId(fileId);
			fileStatusResDto.setUploadedOn(uploadedDate);
			fileStatusResDto.setEwbId(ewbId);
			fileStatusResDto.setEinvId(eInvId);
			fileStatusEwbDtos.add(fileStatusResDto);
		});
		mapfileStatusEwbDto = mapfileStatusEwbDto(fileStatusEwbDtos);
		return mapfileStatusEwbDto;
	}

	private Map<String, List<FileStatusResponseDto>> mapfileStatusEwbDto(
			List<FileStatusResponseDto> fileStatusEwbDtos) {
		Map<String, List<FileStatusResponseDto>> mapFileStaRespDto = new HashMap<>();
		fileStatusEwbDtos.forEach(fileStatusEwbDto -> {
			StringBuilder key = new StringBuilder();
			key.append(fileStatusEwbDto.getId());
			if (mapFileStaRespDto.containsKey(key.toString())) {
				List<FileStatusResponseDto> respDtos = mapFileStaRespDto
						.get(key.toString());
				respDtos.add(fileStatusEwbDto);
				mapFileStaRespDto.put(key.toString(), respDtos);
			} else {
				List<FileStatusResponseDto> respDtos = new ArrayList<>();
				respDtos.add(fileStatusEwbDto);
				mapFileStaRespDto.put(key.toString(), respDtos);
			}
		});
		return mapFileStaRespDto;
	}

	private String sqlQuery(String buildQuery) {

		StringBuilder builder = new StringBuilder();
		builder.append(
				"WITH FIL AS ( SELECT RECEIVED_DATE, CREATED_BY, CREATED_ON, ID, FILE_NAME, FILE_STATUS, "
						+ " FILE_TYPE, DATA_TYPE, source, ERROR_DESC,TRANSFORMATION_STATUS FROM FILE_STATUS WHERE "
						+ buildQuery + "  ) SELECT  "
						+ " RECEIVED_DATE, CREATED_BY, CREATED_ON, ID, FILE_NAME, "
						+ " FILE_STATUS, SUM(ASP_ACTIVE_PROCESSED) AS ASP_ACTIVE_PROCESED, SUM(TTL) AS TOTAL_CNT,  "
						+ " SUM(ASP_INACTIVE_PROCESSED) AS ASP_INACTIVE__PROCESSED, "
						+ "  SUM(ASP_ACTIVE_ERROR) AS ASP__ACTIVE_ERROR,  "
						+ " SUM(ASP_INACTIVE_ERROR) AS ASP__INACTIVE_ERROR,  "
						+ " SUM(INV_NOT_APPLICABLE) AS INV_NOT__APPLICABLE,  "
						+ " SUM(INV_APP) AS INV_APPLICABLE,  "
						+ " SUM(INV_GEN) AS INV_GENERATED,  "
						+ " SUM(INV_IRN_IN_PROGRESS) AS INV_IRN_INITIATED,  "
						+ " SUM(INV_CAN) AS INV_CANCELLED,  "
						+ " SUM(INV_ERR_DIG_IRP) AS INV_ERROR_DIGIGST_IRP,  "
						+ " SUM(ERROR_IRP) AS INV_ERROR_IRP,  "
						+ "  SUM(EWB_NOT_APPLICABLE) AS EWB_NOT_APPLICABLE, "
						+ " SUM(EWB_APP) AS EWB_APPLICABLE,  "
						+ " SUM(EWB_GEN) AS EWB_GENERATED,  "
						+ "  SUM(EWB_IRN_IN_PROGRESS) AS EWB_INTIATED, "
						+ " SUM(EWB_CAN) AS EWB_CANCELLED,  "
						+ " SUM(EWB_ERR_NIC) AS EWB_ERROR_DIGIGST_NIC,  "
						+ " SUM(EBW_ERR_DUP_CAN) AS EWB_ERROR_NIC,  "
						+ " SUM(GSTN_NOT_APPLICABLE) AS GSTN_NOTAPPLICABLE,  "
						+ "  SUM(GSTN_ASP_ERROR) AS GSTN_ASPERROR,  "
						+ " SUM(GSTN_ASP_PROCESSED) AS GSTN_ASPPROCESSED,  "
						+ " SUM(NOT_PUSHED_TO_GSTN) AS NOT_PUSHED_TOGSTN,  "
						+ " SUM(PUSHED_TO_GSTN) AS PUSHED_TOGSTN,  "
						+ " SUM(SAVED_TO_GSTN) AS SAVED_TO_GSTN,  "
						+ " SUM(ERRORS_GSTN) AS ERRORS_GSTN,  "
						+ " SUM(GSTN_IRN_CANCELLED) AS GSTN_IRNCANCELLED, FILE_TYPE,  "
						+ " DATA_TYPE, SUM(SV_ERROR) AS SV_ERROR,  "
						+ " SUM(ASP_ACTIVE_ERROR + SV_ERROR) AS TOTAL_SV_BVERROR,  "
						+ " SUM(INFO_ACTIVE) AS INFO__ACTIVE,  "
						+ " SUM(INFO_INACTIVE) AS INFO__INACTIVE,  "
						+ " SUM(EWB_GENERATED_ON_ERP) AS GENERATED_ON_ERP_EWB,  "
						+ " SUM(EWB_NOT_GENERATED_ON_ERP) AS NOT_GENERATED_ON_ERP_EWB,  "
						+ "   SUM(ASP_NOT_APPLICABLE) AS ASP_NOT_APPLICABLE,  "
						+ " SUM(ASP_ERROR) AS ASP_ERROR,  "
						+ " SUM(ASP_PROCESS) AS ASP_PROCESS,  "
						+ " SUM(ASP_SAVE_INITIATED) AS ASP_SAVE_INITIATED,  "
						+ "  SUM(ASP_SAVED_GSTIN) AS ASP_SAVED_GSTIN,  "
						+ " SUM(ASP_ERRORS_GSTN) AS ASP_ERRORS_GSTN,   "
						+ " SUM(EWB_NOT_OPTED) AS NOT_OPTED_EWB,  "
						+ " SUM(EINV_INFO_ERROR) AS INFO_ERROR_EINV,  "
						+ " SUM(EINV_NOT_OPTED) AS NOT_OPTED_EIN,  "
						+ " SUM(ASP_APPLICABLE) AS ASP_APPLICABLE  "
						+ " ,ERROR_DESC ,TRANSFORMATION_STATUS"
						+ " FROM ( SELECT FIL.RECEIVED_DATE, FIL.CREATED_BY, FIL.CREATED_ON, "
						+ "  FIL.ID,FILE_NAME, FILE_STATUS, COUNT(*) AS TOTAL_COUNT,  COUNT( "
						+ " CASE WHEN ASP_INVOICE_STATUS = 2  "
						+ " AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END "
						+ " ) ASP_ACTIVE_PROCESSED, COUNT( CASE WHEN ASP_INVOICE_STATUS = 2  "
						+ " AND HDR.IS_DELETE = TRUE THEN 1 ELSE NULL END "
						+ " ) ASP_INACTIVE_PROCESSED, COUNT( CASE WHEN ASP_INVOICE_STATUS = 1 "
						+ " AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) "
						+ " ASP_ACTIVE_ERROR,     COUNT(   CASE WHEN ASP_INVOICE_STATUS = 1 "
						+ " AND HDR.IS_DELETE = TRUE THEN 1 ELSE NULL END ) ASP_INACTIVE_ERROR, "
						+ " COUNT(   CASE WHEN EINV_STATUS IN (1, 2) THEN 1 ELSE NULL END ) "
						+ " INV_NOT_APPLICABLE,     "
						+ " COUNT(   CASE WHEN EINV_STATUS IN (4, 5, 6, 7, 8, 9, 10, 13, 14) "
						+ " THEN 1 ELSE NULL END ) INV_APP,    "
						+ "  COUNT(   CASE WHEN EINV_STATUS IN (10, 11) THEN 1 ELSE NULL END ) INV_GEN,   "
						+ "   0 as INV_IRN_IN_PROGRESS,     COUNT(   CASE WHEN EINV_STATUS = 9 "
						+ " THEN 1 ELSE NULL END ) INV_CAN,     COUNT(   "
						+ " CASE WHEN EINV_STATUS IN (4, 5, 6, 7, 8)       "
						+ " AND ASP_INVOICE_STATUS = 1        THEN 1 ELSE NULL END ) "
						+ " INV_ERR_DIG_IRP,     COUNT(   CASE WHEN EINV_STATUS IN (13, 14) "
						+ " THEN 1 ELSE NULL END ) ERROR_IRP,     "
						+ " COUNT(   CASE WHEN EWB_PROCESSING_STATUS IN (1, 25) THEN 1 ELSE NULL END )"
						+ "  EWB_NOT_APPLICABLE,     COUNT(   CASE WHEN EWB_PROCESSING_STATUS IN "
						+ " ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 26, 27 ) "
						+ " THEN 1 ELSE NULL END ) EWB_APP,     "
						+ " COUNT(   CASE WHEN EWB_PROCESSING_STATUS IN (6, 13, 23) THEN 1 ELSE NULL END ) "
						+ " EWB_GEN,     0 as EWB_IRN_IN_PROGRESS,    "
						+ "  COUNT(   CASE WHEN EWB_PROCESSING_STATUS = 10 THEN 1 ELSE NULL END ) EWB_CAN,     "
						+ " COUNT(   CASE WHEN EWB_PROCESSING_STATUS IN (2, 3, 4, 5)      "
						+ "  AND ASP_INVOICE_STATUS = 1 THEN 1 ELSE NULL END ) EWB_ERR_NIC,     "
						+ " COUNT(   CASE WHEN EWB_PROCESSING_STATUS = 9 THEN 1 ELSE NULL END ) EBW_ERR_DUP_CAN, "
						+ "     0 GSTN_NOT_APPLICABLE,     COUNT(   CASE WHEN ASP_INVOICE_STATUS = 1   "
						+ "     AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) GSTN_ASP_ERROR,  "
						+ "   COUNT(   CASE WHEN ASP_INVOICE_STATUS = 2   "
						+ "    AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) GSTN_ASP_PROCESSED,  "
						+ "   COUNT(   CASE WHEN IS_SENT_TO_GSTN = FALSE     "
						+ "  AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) NOT_PUSHED_TO_GSTN,   "
						+ "  COUNT(   CASE WHEN IS_SENT_TO_GSTN = TRUE     "
						+ "  AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) PUSHED_TO_GSTN,    "
						+ " COUNT(   CASE WHEN IS_SAVED_TO_GSTN = TRUE     "
						+ "  AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) SAVED_TO_GSTN,  "
						+ "   COUNT(   CASE WHEN GSTN_ERROR = TRUE     "
						+ "  AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) ERRORS_GSTN,    "
						+ " 0 GSTN_IRN_CANCELLED,     FILE_TYPE,     DATA_TYPE,     0 AS SV_ERROR,    "
						+ " 0 AS TOTAL_SV_BV_ERROR,     COUNT(   CASE WHEN ASP_INVOICE_STATUS = 2  "
						+ "     AND HDR.IS_INFORMATION = TRUE      "
						+ " AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) as INFO_ACTIVE,  "
						+ "   COUNT(   CASE WHEN ASP_INVOICE_STATUS = 2       AND HDR.IS_INFORMATION = TRUE  "
						+ "     AND HDR.IS_DELETE = TRUE THEN 1 ELSE NULL END ) as INFO_INACTIVE,   "
						+ "  COUNT(   CASE WHEN ASP_INVOICE_STATUS = 2      "
						+ " OR + HDR.ASP_INVOICE_STATUS = 1 THEN 1 ELSE NULL END ) AS TTL,    "
						+ " SUM(   CASE WHEN EWB_PROCESSING_STATUS = 23 THEN 1 ELSE 0 END ) "
						+ " EWB_GENERATED_ON_ERP,  "
						+ "   SUM(   CASE WHEN EWB_PROCESSING_STATUS = 24 THEN 1 ELSE 0 END ) "
						+ "EWB_NOT_GENERATED_ON_ERP,     COUNT(   CASE WHEN COMPLIANCE_APPLICABLE = FALSE   "
						+ "    AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) ASP_NOT_APPLICABLE,   "
						+ "  COUNT(   CASE WHEN COMPLIANCE_APPLICABLE = TRUE       AND ASP_INVOICE_STATUS = 1    "
						+ "   AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) ASP_ERROR,  "
						+ "   COUNT(   CASE WHEN COMPLIANCE_APPLICABLE = TRUE      "
						+ " AND ASP_INVOICE_STATUS = 2       AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) "
						+ "ASP_PROCESS,     COUNT(   CASE WHEN IS_SENT_TO_GSTN = TRUE     "
						+ "  AND BATCH_ID IS NOT NULL       AND IS_SAVED_TO_GSTN = FALSE     "
						+ "  AND GSTN_ERROR = FALSE THEN 1 ELSE NULL END ) ASP_SAVE_INITIATED,   "
						+ "  COUNT(   CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 1 ELSE NULL END ) ASP_SAVED_GSTIN,"
						+ "     COUNT(   CASE WHEN GSTN_ERROR = TRUE THEN 1 ELSE NULL END ) ASP_ERRORS_GSTN,   "
						+ "  COUNT(   CASE WHEN EWB_PROCESSING_STATUS = 25 THEN 1 ELSE NULL END ) EWB_NOT_OPTED, "
						+ "    COUNT(   CASE WHEN INFO_ERROR_CODE IS NULL THEN 1 ELSE NULL END ) EINV_INFO_ERROR, "
						+ "    COUNT(   CASE WHEN EINV_STATUS = 1 THEN 1 ELSE NULL END ) EINV_NOT_OPTED,    "
						+ " COUNT(   CASE WHEN COMPLIANCE_APPLICABLE = TRUE    "
						+ "   AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) ASP_APPLICABLE,FIL.ERROR_DESC,FIL.TRANSFORMATION_STATUS   FROM   "
						+ "   FIL     INNER JOIN  ANX_OUTWARD_DOC_HEADER HDR ON HDR.FILE_ID = FIL.ID  "
						+ " GROUP BY FIL.RECEIVED_DATE, FIL.CREATED_BY, FIL.CREATED_ON, FILE_NAME,"
						+ "  FILE_STATUS, FILE_TYPE, DATA_TYPE, FIL.ID,FIL.ERROR_DESC,FIL.TRANSFORMATION_STATUS  UNION ALL SELECT FIL.RECEIVED_DATE,"
						+ "  FIL.CREATED_BY, FIL.CREATED_ON, FIL.ID, FILE_NAME, FILE_STATUS,"
						+ " COUNT(*) AS TOTAL_COUNT, 0 AS ASP_ACTIVE_PROCESSED, 0 AS ASP_INACTIVE_PROCESSED,"
						+ " 0 AS ASP_ACTIVE_ERROR, 0 AS ASP_INACTIVE_ERROR, 0 AS INV_NOT_APPLICABLE, "
						+ " 0 AS INV_APP, 0 AS INV_GEN, 0 AS INV_IRN_IN_PROGRESS, "
						+ " 0 AS INV_CAN, 0 AS INV_ERR_DIG_IRP, 0 AS ERROR_IRP, "
						+ " 0 AS EWB_NOT_APPLICABLE, 0 AS EWB_APP, 0 AS EWB_GEN, "
						+ " 0 AS EWB_IRN_IN_PROGRESS, 0 AS EWB_CAN, 0 AS EWB_ERR_NIC,"
						+ "  0 AS EBW_ERR_DUP_CAN, 0 GSTN_NOT_APPLICABLE, "
						+ " 0 AS GSTN_ASP_ERROR, 0 AS GSTN_ASP_PROCESSED, "
						+ " 0 AS NOT_PUSHED_TO_GSTN, 0 AS PUSHED_TO_GSTN, 0 AS SAVED_TO_GSTN, "
						+ " 0 AS ERRORS_GSTN, 0 GSTN_IRN_CANCELLED, FILE_TYPE, DATA_TYPE, COUNT( "
						+ " CASE WHEN IS_ERROR = 'true' THEN 1 ELSE NULL END ) SV_ERROR, 0 AS TOTAL_SV_BV_ERROR, "
						+ " 0 as INFO_ACTIVE, 0 AS INFO_INACTIVE, COUNT(HDR.ID) AS TTL, "
						+ " 0 AS EWB_GENERATED_ON_ERP, 0 AS EWB_NOT_GENERATED_ON_ERP, "
						+ " 0 AS ASP_NOT_APPLICABLE, NULL AS ASP_ERROR, 0 AS ASP_PROCESS, "
						+ " 0 AS ASP_SAVE_INITIATED, 0 AS ASP_SAVED_GSTIN, "
						+ " 0 AS ASP_ERRORS_GSTN, 0 AS EWB_NOT_OPTED, "
						+ " 0 AS EINV_INFO_ERROR, 0 AS EINV_NOT_OPTED, 0 AS ASP_APPLICABLE,FIL.ERROR_DESC,FIL.TRANSFORMATION_STATUS FROM  FIL INNER JOIN "
						+ " ANX_OUTWARD_ERR_HEADER HDR ON HDR.FILE_ID = FIL.ID "
						+ " GROUP BY FIL.RECEIVED_DATE, FIL.CREATED_BY, FIL.CREATED_ON, "
						+ " FILE_NAME, FILE_STATUS, FILE_TYPE, DATA_TYPE, FIL.ID, FIL.ERROR_DESC,FIL.TRANSFORMATION_STATUS UNION ALL  "
						+ " SELECT FIL.RECEIVED_DATE, FIL.CREATED_BY, FIL.CREATED_ON, FIL.ID, "
						+ " FILE_NAME, FILE_STATUS, COUNT(*) AS TOTAL_COUNT, 0 AS ASP_ACTIVE_PROCESSED, "
						+ " 0 AS ASP_INACTIVE_PROCESSED, 0 AS ASP_ACTIVE_ERROR, "
						+ " 0 AS ASP_INACTIVE_ERROR, 0 AS INV_NOT_APPLICABLE, "
						+ " 0 AS INV_APP, 0 AS INV_GEN, 0 AS INV_IRN_IN_PROGRESS, "
						+ " 0 AS INV_CAN, 0 AS INV_ERR_DIG_IRP, 0 AS ERROR_IRP, "
						+ " 0 AS EWB_NOT_APPLICABLE, 0 AS EWB_APP, 0 AS EWB_GEN, "
						+ " 0 AS EWB_IRN_IN_PROGRESS, 0 AS EWB_CAN, 0 AS EWB_ERR_NIC, "
						+ " 0 AS EBW_ERR_DUP_CAN, 0 GSTN_NOT_APPLICABLE, 0 AS GSTN_ASP_ERROR, "
						+ " 0 AS GSTN_ASP_PROCESSED, 0 AS NOT_PUSHED_TO_GSTN, 0 AS PUSHED_TO_GSTN, "
						+ " 0 AS SAVED_TO_GSTN, 0 AS ERRORS_GSTN, 0 GSTN_IRN_CANCELLED, FILE_TYPE, "
						+ " DATA_TYPE, 0 as SV_ERROR, 0 AS TOTAL_SV_BV_ERROR, 0 as INFO_ACTIVE, "
						+ " 0 AS INFO_INACTIVE, 0 AS TTL, 0 AS EWB_GENERATED_ON_ERP, "
						+ " 0 AS EWB_NOT_GENERATED_ON_ERP, 0 AS ASP_NOT_APPLICABLE, NULL AS ASP_ERROR, "
						+ " 0 AS ASP_PROCESS, 0 AS ASP_SAVE_INITIATED, 0 AS ASP_SAVED_GSTIN, "
						+ " 0 AS ASP_ERRORS_GSTN, 0 AS EWB_NOT_OPTED, 1 AS EINV_INFO_ERROR, "
						+ " 0 AS EINV_NOT_OPTED, 0 AS ASP_APPLICABLE,FIL.ERROR_DESC, FIL.TRANSFORMATION_STATUS FROM  "
						+ " FIL WHERE FILE_STATUS in ('Failed','InProgress','Uploaded') "
						+ " GROUP BY FIL.RECEIVED_DATE, FIL.CREATED_BY, FIL.CREATED_ON, "
						+ " FILE_NAME, FILE_STATUS, FILE_TYPE, DATA_TYPE, FIL.ID,FIL.ERROR_DESC,FIL.TRANSFORMATION_STATUS)  "
						+ " GROUP BY RECEIVED_DATE, CREATED_BY, CREATED_ON, FILE_NAME, "
						+ " FILE_STATUS, FILE_TYPE, DATA_TYPE, ID,ERROR_DESC,TRANSFORMATION_STATUS ORDER BY ID DESC ");
		return builder.toString();
	}

	private String sqlQuery2(String buildQuery) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT IFNULL(SUM(EWB_ID),0),IFNULL(SUM(EINV_ID),0), ");
		sql.append(
				"RECEIVED_DATE ,CREATED_BY,CREATED_ON,ID,FILE_NAME,FILE_STATUS ");
		sql.append("FROM ( ");
		sql.append(
				"select count(HDR.ID) AS EWB_ID, NULL AS EINV_ID,FIL.RECEIVED_DATE ,FIL.CREATED_BY, ");
		sql.append("FIL.CREATED_ON,FIL.ID,FIL.FILE_NAME, FIL.FILE_STATUS ");
		sql.append("FROM  FILE_STATUS FIL ");
		sql.append("LEFT OUTER JOIN ");
		sql.append("ANX_OUTWARD_DOC_HEADER HDR ON HDR.FILE_ID = FIL.ID ");
		sql.append("WHERE SUPPLIER_GSTIN IN ( ");
		sql.append(
				"SELECT GSTIN FROM ENTITY_SOURCE_INFO WHERE EWB_JOB = 1 OR EWB_JOB = 2) ");
		sql.append("AND  EWB_PROCESSING_STATUS = 4 AND ");
		sql.append(buildQuery);
		sql.append(
				"GROUP BY fil.RECEIVED_DATE,FIL.CREATED_BY,FIL.CREATED_ON,FIL.ID,FIL.FILE_NAME, ");
		sql.append("FIL.FILE_STATUS ");
		sql.append("UNION all ");
		sql.append(
				"select NULL AS EWB_ID,count(HDR.ID) AS EINV_ID,FIL.RECEIVED_DATE ,FIL.CREATED_BY, ");
		sql.append("FIL.CREATED_ON,FIL.ID,FIL.FILE_NAME, FIL.FILE_STATUS ");
		sql.append("FROM FILE_STATUS FIL ");
		sql.append("LEFT OUTER JOIN ");
		sql.append(
				"ANX_OUTWARD_DOC_HEADER HDR ON HDR.FILE_ID = FIL.ID WHERE SUPPLIER_GSTIN IN ( ");
		sql.append(
				"SELECT GSTIN FROM ENTITY_SOURCE_INFO WHERE E_INVOICE_JOB = 5 OR E_INVOICE_JOB = 7) ");
		sql.append("AND  EINV_STATUS = 4  AND ");
		sql.append(buildQuery);
		sql.append("GROUP BY FIL.RECEIVED_DATE,FIL.CREATED_BY, ");
		sql.append("FIL.CREATED_ON,FIL.ID,FIL.FILE_NAME, FIL.FILE_STATUS ");
		sql.append(")  ");
		sql.append("GROUP BY RECEIVED_DATE,CREATED_BY, ");
		sql.append("CREATED_ON,ID,FILE_NAME, FILE_STATUS ");
		return sql.toString();
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		return null;
	}

	public static String formatDateToString(Date date, String format,
			String timeZone) {
		// null check
		if (date == null)
			return null;
		// create SimpleDateFormat object with input format
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		// default system timezone if passed null or empty
		if (timeZone == null || "".equalsIgnoreCase(timeZone.trim())) {
			timeZone = Calendar.getInstance().getTimeZone().getID();
		}
		// set timezone to SimpleDateFormat
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		// return Date in required format with timezone as String
		return sdf.format(date);
	}

	//gstr1a query
	private String sqlGstr1AQuery(String buildQuery) {

		StringBuilder builder = new StringBuilder();
		builder.append(
				"WITH FIL AS ( SELECT RECEIVED_DATE, CREATED_BY, CREATED_ON, ID, FILE_NAME, FILE_STATUS, "
						+ " FILE_TYPE, DATA_TYPE, source, TRANSFORMATION_STATUS FROM FILE_STATUS WHERE "
						+ buildQuery + "  ) SELECT  "
						+ " RECEIVED_DATE, CREATED_BY, CREATED_ON, ID, FILE_NAME, "
						+ " FILE_STATUS, SUM(ASP_ACTIVE_PROCESSED) AS ASP_ACTIVE_PROCESED, SUM(TTL) AS TOTAL_CNT,  "
						+ " SUM(ASP_INACTIVE_PROCESSED) AS ASP_INACTIVE__PROCESSED, "
						+ "  SUM(ASP_ACTIVE_ERROR) AS ASP__ACTIVE_ERROR,  "
						+ " SUM(ASP_INACTIVE_ERROR) AS ASP__INACTIVE_ERROR,  "
						+ " SUM(INV_NOT_APPLICABLE) AS INV_NOT__APPLICABLE,  "
						+ " SUM(INV_APP) AS INV_APPLICABLE,  "
						+ " SUM(INV_GEN) AS INV_GENERATED,  "
						+ " SUM(INV_IRN_IN_PROGRESS) AS INV_IRN_INITIATED,  "
						+ " SUM(INV_CAN) AS INV_CANCELLED,  "
						+ " SUM(INV_ERR_DIG_IRP) AS INV_ERROR_DIGIGST_IRP,  "
						+ " SUM(ERROR_IRP) AS INV_ERROR_IRP,  "
						+ "  SUM(EWB_NOT_APPLICABLE) AS EWB_NOT_APPLICABLE, "
						+ " SUM(EWB_APP) AS EWB_APPLICABLE,  "
						+ " SUM(EWB_GEN) AS EWB_GENERATED,  "
						+ "  SUM(EWB_IRN_IN_PROGRESS) AS EWB_INTIATED, "
						+ " SUM(EWB_CAN) AS EWB_CANCELLED,  "
						+ " SUM(EWB_ERR_NIC) AS EWB_ERROR_DIGIGST_NIC,  "
						+ " SUM(EBW_ERR_DUP_CAN) AS EWB_ERROR_NIC,  "
						+ " SUM(GSTN_NOT_APPLICABLE) AS GSTN_NOTAPPLICABLE,  "
						+ "  SUM(GSTN_ASP_ERROR) AS GSTN_ASPERROR,  "
						+ " SUM(GSTN_ASP_PROCESSED) AS GSTN_ASPPROCESSED,  "
						+ " SUM(NOT_PUSHED_TO_GSTN) AS NOT_PUSHED_TOGSTN,  "
						+ " SUM(PUSHED_TO_GSTN) AS PUSHED_TOGSTN,  "
						+ " SUM(SAVED_TO_GSTN) AS SAVED_TO_GSTN,  "
						+ " SUM(ERRORS_GSTN) AS ERRORS_GSTN,  "
						+ " SUM(GSTN_IRN_CANCELLED) AS GSTN_IRNCANCELLED, FILE_TYPE,  "
						+ " DATA_TYPE, SUM(SV_ERROR) AS SV_ERROR,  "
						+ " SUM(ASP_ACTIVE_ERROR + SV_ERROR) AS TOTAL_SV_BVERROR,  "
						+ " SUM(INFO_ACTIVE) AS INFO__ACTIVE,  "
						+ " SUM(INFO_INACTIVE) AS INFO__INACTIVE,  "
						+ " SUM(EWB_GENERATED_ON_ERP) AS GENERATED_ON_ERP_EWB,  "
						+ " SUM(EWB_NOT_GENERATED_ON_ERP) AS NOT_GENERATED_ON_ERP_EWB,  "
						+ "   SUM(ASP_NOT_APPLICABLE) AS ASP_NOT_APPLICABLE,  "
						+ " SUM(ASP_ERROR) AS ASP_ERROR,  "
						+ " SUM(ASP_PROCESS) AS ASP_PROCESS,  "
						+ " SUM(ASP_SAVE_INITIATED) AS ASP_SAVE_INITIATED,  "
						+ "  SUM(ASP_SAVED_GSTIN) AS ASP_SAVED_GSTIN,  "
						+ " SUM(ASP_ERRORS_GSTN) AS ASP_ERRORS_GSTN,   "
						+ " SUM(EWB_NOT_OPTED) AS NOT_OPTED_EWB,  "
						+ " SUM(EINV_INFO_ERROR) AS INFO_ERROR_EINV,  "
						+ " SUM(EINV_NOT_OPTED) AS NOT_OPTED_EIN,  "
						+ " SUM(ASP_APPLICABLE) AS ASP_APPLICABLE , "
						+ " TRANSFORMATION_STATUS "
						+ " FROM ( SELECT FIL.RECEIVED_DATE, FIL.CREATED_BY, FIL.CREATED_ON, "
						+ "  FIL.ID,FILE_NAME, FILE_STATUS, COUNT(*) AS TOTAL_COUNT,  COUNT( "
						+ " CASE WHEN ASP_INVOICE_STATUS = 2  "
						+ " AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END "
						+ " ) ASP_ACTIVE_PROCESSED, COUNT( CASE WHEN ASP_INVOICE_STATUS = 2  "
						+ " AND HDR.IS_DELETE = TRUE THEN 1 ELSE NULL END "
						+ " ) ASP_INACTIVE_PROCESSED, COUNT( CASE WHEN ASP_INVOICE_STATUS = 1 "
						+ " AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) "
						+ " ASP_ACTIVE_ERROR,     COUNT(   CASE WHEN ASP_INVOICE_STATUS = 1 "
						+ " AND HDR.IS_DELETE = TRUE THEN 1 ELSE NULL END ) ASP_INACTIVE_ERROR, "
						+ " COUNT(   CASE WHEN EINV_STATUS IN (1, 2) THEN 1 ELSE NULL END ) "
						+ " INV_NOT_APPLICABLE,     "
						+ " COUNT(   CASE WHEN EINV_STATUS IN (4, 5, 6, 7, 8, 9, 10, 13, 14) "
						+ " THEN 1 ELSE NULL END ) INV_APP,    "
						+ "  COUNT(   CASE WHEN EINV_STATUS IN (10, 11) THEN 1 ELSE NULL END ) INV_GEN,   "
						+ "   0 as INV_IRN_IN_PROGRESS,     COUNT(   CASE WHEN EINV_STATUS = 9 "
						+ " THEN 1 ELSE NULL END ) INV_CAN,     COUNT(   "
						+ " CASE WHEN EINV_STATUS IN (4, 5, 6, 7, 8)       "
						+ " AND ASP_INVOICE_STATUS = 1        THEN 1 ELSE NULL END ) "
						+ " INV_ERR_DIG_IRP,     COUNT(   CASE WHEN EINV_STATUS IN (13, 14) "
						+ " THEN 1 ELSE NULL END ) ERROR_IRP,     "
						+ " COUNT(   CASE WHEN EWB_PROCESSING_STATUS IN (1, 25) THEN 1 ELSE NULL END )"
						+ "  EWB_NOT_APPLICABLE,     COUNT(   CASE WHEN EWB_PROCESSING_STATUS IN "
						+ " ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 26, 27 ) "
						+ " THEN 1 ELSE NULL END ) EWB_APP,     "
						+ " COUNT(   CASE WHEN EWB_PROCESSING_STATUS IN (6, 13, 23) THEN 1 ELSE NULL END ) "
						+ " EWB_GEN,     0 as EWB_IRN_IN_PROGRESS,    "
						+ "  COUNT(   CASE WHEN EWB_PROCESSING_STATUS = 10 THEN 1 ELSE NULL END ) EWB_CAN,     "
						+ " COUNT(   CASE WHEN EWB_PROCESSING_STATUS IN (2, 3, 4, 5)      "
						+ "  AND ASP_INVOICE_STATUS = 1 THEN 1 ELSE NULL END ) EWB_ERR_NIC,     "
						+ " COUNT(   CASE WHEN EWB_PROCESSING_STATUS = 9 THEN 1 ELSE NULL END ) EBW_ERR_DUP_CAN, "
						+ "     0 GSTN_NOT_APPLICABLE,     COUNT(   CASE WHEN ASP_INVOICE_STATUS = 1   "
						+ "     AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) GSTN_ASP_ERROR,  "
						+ "   COUNT(   CASE WHEN ASP_INVOICE_STATUS = 2   "
						+ "    AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) GSTN_ASP_PROCESSED,  "
						+ "   COUNT(   CASE WHEN IS_SENT_TO_GSTN = FALSE     "
						+ "  AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) NOT_PUSHED_TO_GSTN,   "
						+ "  COUNT(   CASE WHEN IS_SENT_TO_GSTN = TRUE     "
						+ "  AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) PUSHED_TO_GSTN,    "
						+ " COUNT(   CASE WHEN IS_SAVED_TO_GSTN = TRUE     "
						+ "  AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) SAVED_TO_GSTN,  "
						+ "   COUNT(   CASE WHEN GSTN_ERROR = TRUE     "
						+ "  AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) ERRORS_GSTN,    "
						+ " 0 GSTN_IRN_CANCELLED,     FILE_TYPE,     DATA_TYPE,     0 AS SV_ERROR,    "
						+ " 0 AS TOTAL_SV_BV_ERROR,     COUNT(   CASE WHEN ASP_INVOICE_STATUS = 2  "
						+ "     AND HDR.IS_INFORMATION = TRUE      "
						+ " AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) as INFO_ACTIVE,  "
						+ "   COUNT(   CASE WHEN ASP_INVOICE_STATUS = 2       AND HDR.IS_INFORMATION = TRUE  "
						+ "     AND HDR.IS_DELETE = TRUE THEN 1 ELSE NULL END ) as INFO_INACTIVE,   "
						+ "  COUNT(   CASE WHEN ASP_INVOICE_STATUS = 2      "
						+ " OR + HDR.ASP_INVOICE_STATUS = 1 THEN 1 ELSE NULL END ) AS TTL,    "
						+ " SUM(   CASE WHEN EWB_PROCESSING_STATUS = 23 THEN 1 ELSE 0 END ) "
						+ " EWB_GENERATED_ON_ERP,  "
						+ "   SUM(   CASE WHEN EWB_PROCESSING_STATUS = 24 THEN 1 ELSE 0 END ) "
						+ "EWB_NOT_GENERATED_ON_ERP,     COUNT(   CASE WHEN COMPLIANCE_APPLICABLE = FALSE   "
						+ "    AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) ASP_NOT_APPLICABLE,   "
						+ "  COUNT(   CASE WHEN COMPLIANCE_APPLICABLE = TRUE       AND ASP_INVOICE_STATUS = 1    "
						+ "   AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) ASP_ERROR,  "
						+ "   COUNT(   CASE WHEN COMPLIANCE_APPLICABLE = TRUE      "
						+ " AND ASP_INVOICE_STATUS = 2       AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) "
						+ "ASP_PROCESS,     COUNT(   CASE WHEN IS_SENT_TO_GSTN = TRUE     "
						+ "  AND BATCH_ID IS NOT NULL       AND IS_SAVED_TO_GSTN = FALSE     "
						+ "  AND GSTN_ERROR = FALSE THEN 1 ELSE NULL END ) ASP_SAVE_INITIATED,   "
						+ "  COUNT(   CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 1 ELSE NULL END ) ASP_SAVED_GSTIN,"
						+ "     COUNT(   CASE WHEN GSTN_ERROR = TRUE THEN 1 ELSE NULL END ) ASP_ERRORS_GSTN,   "
						+ "  COUNT(   CASE WHEN EWB_PROCESSING_STATUS = 25 THEN 1 ELSE NULL END ) EWB_NOT_OPTED, "
						+ "    COUNT(   CASE WHEN INFO_ERROR_CODE IS NULL THEN 1 ELSE NULL END ) EINV_INFO_ERROR, "
						+ "    COUNT(   CASE WHEN EINV_STATUS = 1 THEN 1 ELSE NULL END ) EINV_NOT_OPTED,    "
						+ " COUNT(   CASE WHEN COMPLIANCE_APPLICABLE = TRUE    "
						+ "   AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END ) ASP_APPLICABLE, FIL.TRANSFORMATION_STATUS   FROM   "
						+ "   FIL     INNER JOIN  ANX_OUTWARD_DOC_HEADER_1A HDR ON HDR.FILE_ID = FIL.ID  "
						+ " and hdr.dataorigintypecode='E' "
						+ " GROUP BY FIL.RECEIVED_DATE, FIL.CREATED_BY, FIL.CREATED_ON, FILE_NAME,"
						+ "  FILE_STATUS, FILE_TYPE, DATA_TYPE, FIL.ID ,FIL.TRANSFORMATION_STATUS UNION ALL SELECT FIL.RECEIVED_DATE,"
						+ "  FIL.CREATED_BY, FIL.CREATED_ON, FIL.ID, FILE_NAME, FILE_STATUS,"
						+ " COUNT(*) AS TOTAL_COUNT, 0 AS ASP_ACTIVE_PROCESSED, 0 AS ASP_INACTIVE_PROCESSED,"
						+ " 0 AS ASP_ACTIVE_ERROR, 0 AS ASP_INACTIVE_ERROR, 0 AS INV_NOT_APPLICABLE, "
						+ " 0 AS INV_APP, 0 AS INV_GEN, 0 AS INV_IRN_IN_PROGRESS, "
						+ " 0 AS INV_CAN, 0 AS INV_ERR_DIG_IRP, 0 AS ERROR_IRP, "
						+ " 0 AS EWB_NOT_APPLICABLE, 0 AS EWB_APP, 0 AS EWB_GEN, "
						+ " 0 AS EWB_IRN_IN_PROGRESS, 0 AS EWB_CAN, 0 AS EWB_ERR_NIC,"
						+ "  0 AS EBW_ERR_DUP_CAN, 0 GSTN_NOT_APPLICABLE, "
						+ " 0 AS GSTN_ASP_ERROR, 0 AS GSTN_ASP_PROCESSED, "
						+ " 0 AS NOT_PUSHED_TO_GSTN, 0 AS PUSHED_TO_GSTN, 0 AS SAVED_TO_GSTN, "
						+ " 0 AS ERRORS_GSTN, 0 GSTN_IRN_CANCELLED, FILE_TYPE, DATA_TYPE, COUNT( "
						+ " CASE WHEN IS_ERROR = 'true' THEN 1 ELSE NULL END ) SV_ERROR, 0 AS TOTAL_SV_BV_ERROR, "
						+ " 0 as INFO_ACTIVE, 0 AS INFO_INACTIVE, COUNT(HDR.ID) AS TTL, "
						+ " 0 AS EWB_GENERATED_ON_ERP, 0 AS EWB_NOT_GENERATED_ON_ERP, "
						+ " 0 AS ASP_NOT_APPLICABLE, NULL AS ASP_ERROR, 0 AS ASP_PROCESS, "
						+ " 0 AS ASP_SAVE_INITIATED, 0 AS ASP_SAVED_GSTIN, "
						+ " 0 AS ASP_ERRORS_GSTN, 0 AS EWB_NOT_OPTED, "
						+ " 0 AS EINV_INFO_ERROR, 0 AS EINV_NOT_OPTED, 0 AS ASP_APPLICABLE,FIL.TRANSFORMATION_STATUS FROM  FIL INNER JOIN "
						+ " ANX_OUTWARD_ERR_HEADER_1A HDR ON HDR.FILE_ID = FIL.ID  and FIL.SOURCE ='WEB_UPLOAD' "
						+ " GROUP BY FIL.RECEIVED_DATE, FIL.CREATED_BY, FIL.CREATED_ON, "
						+ " FILE_NAME, FILE_STATUS, FILE_TYPE, DATA_TYPE, FIL.ID ,FIL.TRANSFORMATION_STATUS UNION ALL  "
						+ " SELECT FIL.RECEIVED_DATE, FIL.CREATED_BY, FIL.CREATED_ON, FIL.ID, "
						+ " FILE_NAME, FILE_STATUS, COUNT(*) AS TOTAL_COUNT, 0 AS ASP_ACTIVE_PROCESSED, "
						+ " 0 AS ASP_INACTIVE_PROCESSED, 0 AS ASP_ACTIVE_ERROR, "
						+ " 0 AS ASP_INACTIVE_ERROR, 0 AS INV_NOT_APPLICABLE, "
						+ " 0 AS INV_APP, 0 AS INV_GEN, 0 AS INV_IRN_IN_PROGRESS, "
						+ " 0 AS INV_CAN, 0 AS INV_ERR_DIG_IRP, 0 AS ERROR_IRP, "
						+ " 0 AS EWB_NOT_APPLICABLE, 0 AS EWB_APP, 0 AS EWB_GEN, "
						+ " 0 AS EWB_IRN_IN_PROGRESS, 0 AS EWB_CAN, 0 AS EWB_ERR_NIC, "
						+ " 0 AS EBW_ERR_DUP_CAN, 0 GSTN_NOT_APPLICABLE, 0 AS GSTN_ASP_ERROR, "
						+ " 0 AS GSTN_ASP_PROCESSED, 0 AS NOT_PUSHED_TO_GSTN, 0 AS PUSHED_TO_GSTN, "
						+ " 0 AS SAVED_TO_GSTN, 0 AS ERRORS_GSTN, 0 GSTN_IRN_CANCELLED, FILE_TYPE, "
						+ " DATA_TYPE, 0 as SV_ERROR, 0 AS TOTAL_SV_BV_ERROR, 0 as INFO_ACTIVE, "
						+ " 0 AS INFO_INACTIVE, 0 AS TTL, 0 AS EWB_GENERATED_ON_ERP, "
						+ " 0 AS EWB_NOT_GENERATED_ON_ERP, 0 AS ASP_NOT_APPLICABLE, NULL AS ASP_ERROR, "
						+ " 0 AS ASP_PROCESS, 0 AS ASP_SAVE_INITIATED, 0 AS ASP_SAVED_GSTIN, "
						+ " 0 AS ASP_ERRORS_GSTN, 0 AS EWB_NOT_OPTED, 1 AS EINV_INFO_ERROR, "
						+ " 0 AS EINV_NOT_OPTED, 0 AS ASP_APPLICABLE,TRANSFORMATION_STATUS FROM  "
						+ " FIL WHERE FILE_STATUS in ('Failed','InProgress','Uploaded') "
						+ " GROUP BY FIL.RECEIVED_DATE, FIL.CREATED_BY, FIL.CREATED_ON, "
						+ " FILE_NAME, FILE_STATUS, FILE_TYPE, DATA_TYPE, FIL.ID,FIL.TRANSFORMATION_STATUS)  "
						+ " GROUP BY RECEIVED_DATE, CREATED_BY, CREATED_ON, FILE_NAME, "
						+ " FILE_STATUS, FILE_TYPE, DATA_TYPE, ID,TRANSFORMATION_STATUS ORDER BY ID DESC ");
		return builder.toString();
	}
	
	public static void main(String[] args) {
		StringBuilder build = new StringBuilder();
		StringBuilder build1 = new StringBuilder();

		if (true) {
			build.append(" AND SOURCE = :source ");
			build1.append(" AND FIL.SOURCE = :source ");
		}

		if (true) {
			if (true) {
				build.append(" AND DATA_TYPE = :dataType ");
				build1.append("AND FIL.DATA_TYPE = :dataType ");
			}
			if (true) {
				build.append(" AND DATA_TYPE = :dataType ");
				build1.append(" AND FIL.DATA_TYPE = :dataType ");

			}
			
			if (true) {
				build.append(" AND DATA_TYPE = :dataType ");
				build1.append("AND FIL.DATA_TYPE = :dataType ");
			}
		}
		if (true) {
			build.append(" AND FILE_TYPE = :fileType ");
			build1.append(" AND FIL.FILE_TYPE = :fileType ");

		}
		if (true) {

			build.append(" AND RECEIVED_DATE BETWEEN :dataRecvFrom "
					+ "AND :dataRecvTo ");

			build1.append(" AND FIL.RECEIVED_DATE BETWEEN :dataRecvFrom "
					+ "AND :dataRecvTo ");

		}

		String buildQuery = build.toString();
		String buildQueryHdr = build1.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		ComprehensiveFileStatusService c=new ComprehensiveFileStatusService();
		String queryStr = c.sqlQuery(buildQuery);

		System.out.println(queryStr);
	}
}
