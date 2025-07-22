package com.ey.advisory.app.data.daos.client;

import static com.ey.advisory.common.GSTConstants.INWARD;
import static com.ey.advisory.common.GSTConstants.OUTWARD;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Component("FileStatusDaoImpl")
@Slf4j
public class FileStatusDaoImpl implements FileStatusDao {

	private static final Map<String, String> SECTION_TABLE__FILESTATUS_MAP = new HashMap<>();
	private static final String GSTR1 = "GSTR1";
	private static final String B2CS = "B2CS";

	static {
		// Initialize the static array.

		String DATARECV = "FILE_STATUS";

		SECTION_TABLE__FILESTATUS_MAP.put("DATARECV", DATARECV);
	}
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr1FileStatusEntity> fileStatusSection(String sectionType,
			String buildQuery, LocalDate dataRecvFrom, LocalDate dataRecvTo,
			String fileType, String dataType, String source) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("fileStatusSection ");
		}
		StringBuilder build = new StringBuilder();
		String capitalType = null;
		String dataTypeCapital = null;
		if (dataType != null) {
			if (dataType.equalsIgnoreCase(INWARD)) {
				dataTypeCapital = INWARD;
			}
			if (dataType.equalsIgnoreCase(OUTWARD)) {
				dataTypeCapital = OUTWARD;
			}
			if (dataType.equalsIgnoreCase(GSTConstants.RET)) {
				dataTypeCapital = GSTConstants.RET;
			}
			if (dataType.equalsIgnoreCase(GSTR1)) {
				dataTypeCapital = GSTR1;
			}
			if (dataType.equalsIgnoreCase(GSTConstants.GSTR3B)) {
				dataTypeCapital = GSTConstants.GSTR3B;
			}
			if (dataType.equalsIgnoreCase(GSTConstants.OTHERS)) {
				dataTypeCapital = GSTConstants.OTHERS;
			} else {
				dataTypeCapital = dataType.toUpperCase();
			}
		}
		if (source != null && source
				.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
			source = JobStatusConstants.SFTP_WEB_UPLOAD;
		}
		if (source != null && source.equalsIgnoreCase(JobStatusConstants.ERP)) {
			source = JobStatusConstants.ERP;
		}

		if (source != null
				&& source.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
			source = JobStatusConstants.WEB_UPLOAD;
		}
		if (fileType != null) {
			if (fileType.equalsIgnoreCase("table4")) {
				capitalType = "TABLE4";
			}
			if (fileType.equalsIgnoreCase("table3h/3i")) {
				capitalType = "TABLE3H3I";
			}
			if (fileType.equalsIgnoreCase("b2cs")) {
				capitalType = B2CS;
			} else if (fileType.equalsIgnoreCase("at")) {
				capitalType = "ADVANCE RECEIVED";
			} else if (fileType.equalsIgnoreCase("txpd")) {
				capitalType = "ADVANCE ADJUSTMENT";
			} else if (fileType.equalsIgnoreCase("inv")) {
				capitalType = "INVOICE";
			} else if (fileType.equalsIgnoreCase("nil")) {
				capitalType = "NILNONEXMPT";
			} else if (fileType.equalsIgnoreCase("ret1and1a")) {
				capitalType = GSTConstants.RET1AND1A;
			} else if (fileType.equalsIgnoreCase("refunds")) {
				capitalType = GSTConstants.REFUNDS;
			} else if (fileType.equalsIgnoreCase("setoffandutil")) {
				capitalType = GSTConstants.SETOFFANDUTIL;
			} else if (fileType.equalsIgnoreCase("gstr3bItc")) {
				capitalType = GSTConstants.GSTR3B_ITC_4243;
			} else if (fileType.equalsIgnoreCase("interest")) {
				capitalType = GSTConstants.INTEREST;
			} else if (fileType.equalsIgnoreCase(GSTConstants.DISTRIBUTION)) {
				capitalType = GSTConstants.DISTRIBUTION;
			} else if (fileType.equalsIgnoreCase(GSTConstants.GSTR7_TDS)) {
				capitalType = GSTConstants.GSTR7_TDS;
			} else if (fileType.equalsIgnoreCase(APIConstants.TCSANDTDS)) {
				capitalType = APIConstants.TCSANDTDS.toUpperCase();
			} else if (fileType.equalsIgnoreCase("einvoice_recon")) {
				capitalType = GSTConstants.EINVOICE_RECON;
			} else if (fileType.equalsIgnoreCase("delete_gstn")) {
				capitalType = GSTConstants.DELETE_GSTN;
			} else if (fileType.equalsIgnoreCase("exclusive_save_file")) {
				capitalType = GSTConstants.EXCLUSIVE_SAVE_FILE;
			} else if (fileType.equalsIgnoreCase("180_days_reversal")) {
				capitalType = GSTConstants.R180_DAYS_REVERSAL;
			} else if (fileType.equalsIgnoreCase(GSTConstants.HSNUPLOAD)) {
				capitalType = GSTConstants.HSNUPLOAD;
			} else {
				capitalType = fileType.toUpperCase();
			}
			
		}
		createQueryString(sectionType, buildQuery, build);
		Query q = entityManager.createNativeQuery(build.toString());
		if (sectionType != null && "DATARECV".equals(sectionType)) {
			q.setParameter("dataRecvFrom", dataRecvFrom);
			q.setParameter("dataRecvTo", dataRecvTo);
		}
		if (fileType != null && !fileType.equalsIgnoreCase("all")) {
			q.setParameter("fileType", capitalType);
		}
		if (dataType != null && !dataType.equalsIgnoreCase("all")) {
			q.setParameter("dataType", dataTypeCapital);
		}
		if (source != null && source
				.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
			q.setParameter("source", source);
		}
		if (source != null
				&& source.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
			List<String> list = new ArrayList<>();
			list.add(JobStatusConstants.WEB_UPLOAD);
			list.add(JobStatusConstants.ERP);
			q.setParameter("source", list);
		}
		if (source != null && source.equalsIgnoreCase(JobStatusConstants.ERP)) {
			q.setParameter("source", source);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		return list.parallelStream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1FileStatusEntity convert(Object[] o) {

		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();
		fileStatus.setId(Long.parseLong(o[0].toString()));
		fileStatus.setFileName((String) o[1]);
		fileStatus.setFileType((String) o[2]);
		fileStatus.setFileStatus((String) o[3]);

		Timestamp timeStampStartUpload = (java.sql.Timestamp) o[4];
		if (timeStampStartUpload != null) {
			fileStatus.setStartOfUploadTime(EYDateUtil.toISTDateTimeFromUTC(
					timeStampStartUpload.toLocalDateTime()));
		}
		Timestamp timeStampEndUpload = (java.sql.Timestamp) o[5];
		if (timeStampEndUpload != null) {
			fileStatus.setEndOfUploadTime(EYDateUtil.toISTDateTimeFromUTC(
					timeStampEndUpload.toLocalDateTime()));
		}

		Integer total = 0;
		if (o[6] != null) {
			total = (GenUtil.getBigInteger(o[6])).intValue();
		}
		fileStatus.setTotal(total);

		Integer processed = 0;
		if (o[7] != null) {
			processed = (GenUtil.getBigInteger(o[7])).intValue();
		}
		fileStatus.setProcessed(processed);

		Integer error = 0;
		if (o[8] != null) {
			error = (GenUtil.getBigInteger(o[8])).intValue();
		}
		fileStatus.setError(error);

		Integer info = 0;
		if (o[9] != null) {
			info = (GenUtil.getBigInteger(o[9])).intValue();
		}
		fileStatus.setInformation(info);
		fileStatus.setUpdatedBy((String) o[10]);

		Timestamp createdOn = (java.sql.Timestamp) o[11];

		if (createdOn != null) {
			fileStatus.setUpdatedOn(EYDateUtil
					.toISTDateTimeFromUTC(createdOn.toLocalDateTime()));
		}
		Timestamp receivedDate = (java.sql.Timestamp) o[11];
		if (receivedDate != null) {

			LocalDateTime recvdate = EYDateUtil
					.toISTDateTimeFromUTC(receivedDate.toLocalDateTime());

			fileStatus.setReceivedDate(recvdate.toLocalDate());
		}
		fileStatus.setSource((String) o[13]);
		fileStatus.setDataType((String) o[14]);
		return fileStatus;
	}

	private void createQueryString(String sectionType, String buildQuery,
			StringBuilder build) {

		String tableName = "";
		// String build = null;

		if ("DATARECV".equalsIgnoreCase(sectionType)) {
			tableName = SECTION_TABLE__FILESTATUS_MAP.get(sectionType);
			build.append("SELECT ID,FILE_NAME,FILE_TYPE,FILE_STATUS, "
					+ "UPLOAD_START_TIME,UPLOAD_END_TIME,TOTAL_RECORDS, "
					+ "PROCESSED_RECORDS,ERROR_RECORDS, "
					+ "INFORMATION_RECORDS,CREATED_BY,CREATED_ON, "
					+ "RECEIVED_DATE,SOURCE,DATA_TYPE FROM \"" + tableName
					+ "\" WHERE " + buildQuery + " ORDER BY ID DESC");
		}
	}

	@Override
	public List<Object[]> fileStatus(String build, String dataType,
			String fileType, LocalDate dataRecvFrom, LocalDate dataRecvTo,
			String source) {
		Query q = entityManager.createNativeQuery(build);
		q.setParameter("dataType", dataType);
		q.setParameter("fileType", fileType);
		q.setParameter("dataRecvFrom", dataRecvFrom);
		q.setParameter("dataRecvTo", dataRecvTo);
		if (source != null
				&& source.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
			List<String> list = new ArrayList<>();
			list.add(JobStatusConstants.WEB_UPLOAD);
			list.add(JobStatusConstants.ERP);
			q.setParameter("source", list);
		} else {
			q.setParameter("source", JobStatusConstants.SFTP_WEB_UPLOAD);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		return list;
	}

	@Override
	public List<Object[]> masterFileStatus(String buildQuery,
			LocalDate fromDate, LocalDate toDate, String fileType,
			Long entityId) {
		Query q = entityManager.createNativeQuery(buildQuery);
		q.setParameter("fromDate", fromDate);
		q.setParameter("toDate", toDate);
		q.setParameter("fileType", fileType);
		q.setParameter("entityId", entityId);
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		return list;

	}

}