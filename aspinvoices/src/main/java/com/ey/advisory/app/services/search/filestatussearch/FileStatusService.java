package com.ey.advisory.app.services.search.filestatussearch;

import static com.ey.advisory.common.GSTConstants.ALL;
import static com.ey.advisory.common.GSTConstants.OUTWARD;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.daos.client.FileStatusDao;
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
@Component("FileStatusService")
@Slf4j
public class FileStatusService implements SearchService {
	@Autowired
	@Qualifier("FileStatusDaoImpl")
	private FileStatusDao fileStatusDao;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("anx1OutWardErrHeaderRepository")
	private Anx1OutWardErrHeaderRepository anx1OutWardErrHeaderRepository;

	@Autowired
	@Qualifier("Anx2InwardErrHeaderRepository")
	private Anx2InwardErrHeaderRepository anx2InwardErrHeaderRepository;

	@SuppressWarnings("unchecked")
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
		List<Gstr1FileStatusEntity> response = new ArrayList<>();
		List<FileStatusResponseDto> fileStatusResponseDtos = new ArrayList<>();
		StringBuilder build = new StringBuilder();
		if (dataType != null && !dataType.equalsIgnoreCase(ALL)) {
			if (dataType.equalsIgnoreCase(OUTWARD)) {
				build.append(" AND DATA_TYPE = :dataType ");
			}
			if (dataType.equalsIgnoreCase(GSTConstants.GSTR1)) {
				build.append(" AND DATA_TYPE = :dataType ");
			} else {
				build.append(" AND DATA_TYPE = :dataType ");
			}
		}
		if (fileType != null && !fileType.equalsIgnoreCase(ALL)) {
			build.append(" AND FILE_TYPE = :fileType ");
		}
		if (dataRecvFrom != null && dataRecvTo != null) {

			build.append(" AND RECEIVED_DATE BETWEEN :dataRecvFrom "
					+ "AND :dataRecvTo ");
		}

		if (source != null && source
				.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
			build.append(" AND SOURCE = :source ");
		}

		if (source != null
				&& source.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
			build.append(" AND SOURCE IN :source ");
		}
		String buildQuery = build.toString().substring(4);
		StringBuilder build1 = new StringBuilder();

		if ((GSTConstants.INWARD.equalsIgnoreCase(dataType)
				&& GSTConstants.RAW.equalsIgnoreCase(fileType))
				|| (GSTConstants.INWARD.equalsIgnoreCase(dataType)
						&& GSTConstants.COMPREHENSIVE_INWARD_RAW
								.equalsIgnoreCase(fileType))
				|| (GSTConstants.OUTWARD.equalsIgnoreCase(dataType)
						|| GSTConstants.GSTR1.equalsIgnoreCase(dataType)
								&& !GSTConstants.EINVOICE_RECON
										.equalsIgnoreCase(fileType)

								&& (GSTConstants.RAW.equalsIgnoreCase(fileType)
										|| GSTConstants.COMPREHENSIVE_RAW
												.equalsIgnoreCase(fileType)))
				|| (GSTConstants.ITC04_FILE.equalsIgnoreCase(fileType)
						&& GSTConstants.OTHERS.equalsIgnoreCase(dataType))) {
			sqlQuery(dataType, build1, buildQuery, fileType);
			List<Object[]> list = fileStatusDao.fileStatus(build1.toString(),
					dataType.toUpperCase(), fileType.toUpperCase(),
					dataRecvFrom, dataRecvTo, source);

			/*
			 * List<Object[]> list = docRepository.fileStatuProcCall(
			 * dataType.toUpperCase(), fileType.toUpperCase(), dataRecvFrom,
			 * dataRecvTo);
			 */

			for (Object[] obj : list) {
				FileStatusResponseDto fileStatusResDto = new FileStatusResponseDto();

				Long id = null;
				if (obj[0].toString() != null) {
					String idStr = String.valueOf(obj[0]).trim();
					id = Long.valueOf(idStr);
				}
				String uploadedDate = null;
				if (obj[1] != null) {
					LocalDateTime localDateTime = ((Timestamp) obj[1])
							.toLocalDateTime();
					DateTimeFormatter formatter = DateTimeFormatter
							.ofPattern("yyyy-MM-dd");
					localDateTime = EYDateUtil
							.toISTDateTimeFromUTC(localDateTime);
					uploadedDate = formatter.format(localDateTime);
				}

				String uploadedBy = (obj[2] != null)
						? String.valueOf(obj[2]).trim() : "";
				String dataTypeRes = (obj[3] != null)
						? String.valueOf(obj[3]).trim() : "";
				
						String fileTypeRes = null;
				
				if(LOGGER.isDebugEnabled())
				{
					
					LOGGER.debug(" INSIDE block");
				}
				if(obj[4] !=null && "2BPR_IMS_RECON_RESPONSE".equalsIgnoreCase(String.valueOf(obj[4]).trim()))
				{
					fileTypeRes="Recon Response + IMS (2BvsPR)";
				}else
				{
					fileTypeRes = (obj[4] != null)
							? String.valueOf(obj[4]).trim() : "";
				}
				
				
				String fileNameRes = (obj[5] != null)
						? String.valueOf(obj[5]).trim() : "";
				String fileStausRes = (obj[6] != null)
						? String.valueOf(obj[6]).trim() : "";
				Integer totalRecordRes = 0;
				if (obj[8] != null) {
					String totalRecordStr = String.valueOf(obj[8]).trim();
					totalRecordRes = Integer.valueOf(totalRecordStr);
				}
				Integer activeProcessed = 0;
				if (obj[9] != null) {
					String activeProcessedStr = String.valueOf(obj[9]).trim();
					activeProcessed = Integer.valueOf(activeProcessedStr);
				}
				Integer inActiveProcessed = 0;
				if (obj[10] != null) {
					String inActiveProcessedStr = String.valueOf(obj[10])
							.trim();
					inActiveProcessed = Integer.valueOf(inActiveProcessedStr);
				}
				Integer structError = 0;
				if (obj[11] != null) {
					String structErrorStr = String.valueOf(obj[11]).trim();
					structError = Integer.valueOf(structErrorStr);
				}
				Integer activeError = 0;
				if (obj[12] != null) {
					String activeErrorStr = String.valueOf(obj[12]).trim();
					activeError = Integer.valueOf(activeErrorStr);
				}
				Integer inActiveError = 0;
				if (obj[13] != null) {
					String inActiveErrorStr = String.valueOf(obj[13]).trim();
					inActiveError = Integer.valueOf(inActiveErrorStr);
				}

				Integer totalStructBusError = 0;
				if (obj[14] != null) {
					String totalStructBusErrorStr = String.valueOf(obj[14])
							.trim();
					totalStructBusError = Integer
							.valueOf(totalStructBusErrorStr);
				}

				Integer activeInfo = 0;
				if (obj[15] != null) {
					String activeInfoStr = String.valueOf(obj[15]).trim();
					activeInfo = Integer.valueOf(activeInfoStr);
				}
				Integer inActiveInfo = 0;
				if (obj[16] != null) {
					String inActiveInfoStr = String.valueOf(obj[16]).trim();
					inActiveInfo = Integer.valueOf(inActiveInfoStr);
				}
				if (GSTConstants.COMPREHENSIVE_INWARD_RAW
						.equalsIgnoreCase(fileType)) {
					String errDescription = (obj[17] != null)
							? String.valueOf(obj[17]).trim() : "";
					
					if (fileStausRes.equalsIgnoreCase("Failed")) {
						Map<String, String> errorMapping = new HashMap<>();
						errorMapping.put("Headers Mismatch", "File uploaded with Unsupported format/Incorrect header");
						errorMapping.put("Incorrect File Name",
								"File name should be in \"MAP_RuleName_FileName\" format.");

						fileStatusResDto.setErrDescription(errorMapping.getOrDefault(errDescription,
								(errDescription.isEmpty()
										? "File failed due to technical reasons, please reach out to Central team"
										: errDescription)));
					}
				}
				
				String transformationStatus=(obj[18] != null)
						? String.valueOf(obj[18]).trim() : "";
				fileStatusResDto.setId(id);
				fileStatusResDto.setUploadedOn(uploadedDate);
				fileStatusResDto.setUploadedBy(uploadedBy);
				fileStatusResDto.setFileType(fileTypeRes);
				fileStatusResDto.setFileName(fileNameRes);
				fileStatusResDto.setDataType(dataTypeRes);
				fileStatusResDto.setFileStatus(fileStausRes);
				fileStatusResDto.setTotal(totalRecordRes);
				fileStatusResDto.setProcessedActive(activeProcessed);
				fileStatusResDto.setProcessedInactive(inActiveProcessed);
				fileStatusResDto.setStrucError(structError);
				fileStatusResDto.setErrorsActive(activeError);
				fileStatusResDto.setErrorsInactive(inActiveError);
				fileStatusResDto
						.setTotalStrucBusinessError(totalStructBusError);
				fileStatusResDto.setInfoActive(activeInfo);
				fileStatusResDto.setTransformationStatus(transformationStatus);
				
				fileStatusResponseDtos.add(fileStatusResDto);
			}
			return (SearchResult<R>) new SearchResult<>(fileStatusResponseDtos);
		} else {
			response = fileStatusDao.fileStatusSection("DATARECV", buildQuery,
					dataRecvFrom, dataRecvTo, fileType, dataType, source);
		}
		return (SearchResult<R>) new SearchResult<>(response);
	}

	public static void main(String[] args) {
		StringBuilder build = new StringBuilder();

		build.append(" AND DATA_TYPE = :dataType ");

		build.append(" AND FILE_TYPE = :fileType ");

		build.append(" AND RECEIVED_DATE BETWEEN :dataRecvFrom "
				+ "AND :dataRecvTo ");

		build.append(" AND SOURCE IN :source ");

		String buildQuery = build.toString().substring(4);
		StringBuilder build1 = new StringBuilder();
		sqlQuery("inward", build1, buildQuery, "COMPREHENSIVE_INWARD_RAW");
		System.out.println(build1.toString());
	}

	private static void sqlQuery(String dataType, StringBuilder build,
			String buildQuery, String fileType) {
		build.append("SELECT FS.ID, FS.CREATED_ON AS DATE_OF_UPLOAD ");
		build.append(",FS.CREATED_BY AS UPLOADED_BY,DATA_TYPE,FILE_TYPE, ");
		build.append("FILE_NAME, FILE_STATUS, SUMTBL.*, FS.ERROR_DESC, FS.TRANSFORMATION_STATUS FROM ");
		build.append("FILE_STATUS FS LEFT JOIN(");
		build.append("SELECT FILE_ID, SUM(TOTALRECORDS) AS TOTALRECORDS, ");
		build.append(
				"SUM(ACT_PROCESSED) AS ACT_PROCESSED, SUM(INACT_PROCESSED) ");
		build.append("AS INACT_PROCESSED, ");
		build.append(" SUM(STR_ERRORS) AS STR_ERRORS, ");
		build.append("SUM(ACT_ERRORS) AS ACT_ERRORS,");
		build.append("SUM(INACT_ERRORS) AS INACT_ERRORS, ");
		build.append("SUM(TOTAL_STR_BUS_ERRORS) AS TOTAL_STR_BUS_ERRORS,");
		build.append("SUM(ACT_INFORMATION) AS ACT_INFORMATION, ");
		build.append("SUM(INACT_INFORMATION) AS INACT_INFORMATION ");
		build.append("FROM(SELECT HDR.FILE_ID AS FILE_ID, ");
		build.append("count(HDR.FILE_ID) as TOTALRECORDS, ");
		build.append(
				"COUNT(case when IS_PROCESSED = TRUE AND IS_DELETE = FALSE ");
		build.append("then 1 else NULL END) AS ACT_PROCESSED, ");
		build.append(
				"COUNT(case when IS_PROCESSED = TRUE AND IS_DELETE = TRUE ");
		build.append(
				"then 1 else NULL END) AS INACT_PROCESSED,0 AS STR_ERRORS,");
		build.append("COUNT(case when IS_ERROR = TRUE AND IS_DELETE = FALSE ");
		build.append("then 1 else NULL END) AS ACT_ERRORS,");
		build.append("COUNT(case when IS_ERROR = TRUE AND IS_DELETE = TRUE ");
		build.append("then 1 else NULL END) AS INACT_ERRORS,");
		build.append("COUNT(case when IS_ERROR = TRUE AND IS_DELETE = FALSE ");
		build.append("then 1 else NULL END) AS TOTAL_STR_BUS_ERRORS, ");
		build.append(
				"COUNT(case when IS_INFORMATION = TRUE AND IS_PROCESSED = TRUE ");
		build.append(
				"AND IS_DELETE = FALSE then 1 else NULL END) AS ACT_INFORMATION, ");
		build.append(
				"COUNT(case when IS_INFORMATION = TRUE AND IS_PROCESSED = TRUE ");
		build.append(
				"AND IS_DELETE = TRUE then 1 else NULL END) AS INACT_INFORMATION ");
		if (dataType.equalsIgnoreCase("OUTWARD")
				|| dataType.equalsIgnoreCase("GSTR1")) {
			build.append("FROM ANX_OUTWARD_DOC_HEADER HDR ");
		} else if (dataType.equalsIgnoreCase("INWARD")) {
			build.append("FROM ANX_INWARD_DOC_HEADER HDR ");
		} else if (GSTConstants.ITC04_FILE.equalsIgnoreCase(fileType)
				&& GSTConstants.OTHERS.equalsIgnoreCase(dataType)) {
			build.append("FROM ITC04_HEADER HDR ");
		}
		build.append("WHERE HDR.FILE_ID IS NOT NULL ");
		build.append("GROUP BY HDR.FILE_ID ");
		build.append("UNION ALL ");
		build.append("SELECT ERR.FILE_ID AS FILE_ID, ");
		build.append("COUNT(ERR.FILE_ID) as TOTALRECORDS, ");
		build.append("0 AS ACT_PROCESSED,  ");
		build.append("0 AS INACT_PROCESSED, ");
		build.append("COUNT(ERR.FILE_ID) AS STR_ERRORS, ");
		build.append("0 AS ACT_ERRORS, ");
		build.append("0 AS INACT_ERRORS, ");
		build.append("COUNT(ERR.FILE_ID) AS TOTAL_STR_BUS_ERRORS, ");
		build.append("0 AS ACT_INFORMATION, ");
		build.append("0 AS INACT_INFORMATION ");
		if (dataType.equalsIgnoreCase("OUTWARD")
				|| dataType.equalsIgnoreCase("GSTR1")) {
			build.append("FROM ANX_OUTWARD_ERR_HEADER ERR ");
		} else if (dataType.equalsIgnoreCase("INWARD")) {
			build.append("FROM ANX_INWARD_ERROR_HEADER ERR ");
		} else if (GSTConstants.ITC04_FILE.equalsIgnoreCase(fileType)
				&& GSTConstants.OTHERS.equalsIgnoreCase(dataType)) {
			build.append("FROM ITC04_ERR_HEADER ERR ");
		}
		build.append("WHERE ERR.FILE_ID IS NOT NULL ");
		build.append("GROUP BY ERR.FILE_ID ");
		build.append(") X ");
		build.append("GROUP BY FILE_ID ");
		build.append(") SUMTBL ");
		build.append("ON FS.ID = SUMTBL.FILE_ID WHERE ");
		build.append(buildQuery);
		build.append(" ORDER BY FS.ID DESC ");

	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		return null;
	}

}
