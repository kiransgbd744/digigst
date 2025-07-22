package com.ey.advisory.service.gstr1.sales.register;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Stream;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.docs.dto.FileStatusReqDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("SalesRegisterFileStatusService")
@Slf4j
public class SalesRegisterFileStatusService implements SearchService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

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

			List<SalesRegisterFileStatusResponseDto> fileStatusResponseDtos = new ArrayList<>();
			StringBuilder build = new StringBuilder();
			if (dataType != null) {
				build.append(" AND DATA_TYPE = :dataType ");
			}
			if (fileType != null) {
				build.append(" AND FILE_TYPE = :fileType ");
			}
			if (dataRecvFrom != null && dataRecvTo != null) {

				build.append(" AND RECEIVED_DATE BETWEEN :dataRecvFrom "
						+ "AND :dataRecvTo ");
			}

			if (source != null) {
				build.append(" AND SOURCE = :source ");
			}
			build.append(" ORDER BY ID DESC ");
			String buildQuery = build.toString().substring(4);
			String sql = null;

			sql = sqlQuery(buildQuery);

			List<Object[]> list = fileStatus(sql, dataType.toUpperCase(),
					fileType, dataRecvFrom, dataRecvTo, source);

			for (Object[] obj : list) {
				SalesRegisterFileStatusResponseDto fileStatusResDto = new SalesRegisterFileStatusResponseDto();

				String uploadedBy = (obj[1] != null)
						? String.valueOf(obj[1]).trim() : "";

				String uploadedDate = null;
				if (obj[0] != null) {
					LocalDateTime localDateTime = ((Timestamp) obj[0])
							.toLocalDateTime();
					DateTimeFormatter formatter = DateTimeFormatter
							.ofPattern("yyyy-MM-dd");
					localDateTime = EYDateUtil
							.toISTDateTimeFromUTC(localDateTime);
					uploadedDate = formatter.format(localDateTime);
				}
				String fileName = (obj[2] != null)
						? String.valueOf(obj[2]).trim() : "";

				fileStatusResDto.setUploadedOn(uploadedDate);
				fileStatusResDto.setUploadedBy(uploadedBy);
				fileStatusResDto.setFileName(fileName);
				fileStatusResDto.setTotalRecords(
						obj[3] != null ? obj[3].toString() : "0");
				fileStatusResDto.setProcessedRecords(
						obj[4] != null ? obj[4].toString() : "0");
				fileStatusResDto.setErrorRecords(
						obj[5] != null ? obj[5].toString() : "0");
				Long id = null;
				if (obj[6].toString() != null) {
					String idStr = String.valueOf(obj[6]).trim();
					id = Long.valueOf(idStr);
				}
				fileStatusResDto.setStatus(
						obj[7] != null ? obj[7].toString() : null);
				fileStatusResDto.setId(id);
				fileStatusResponseDtos.add(fileStatusResDto);
			}
			return (SearchResult<R>) new SearchResult<>(fileStatusResponseDtos);
		} catch (Exception e) {
			LOGGER.error("Exception Occred:", e);
		}
		return (SearchResult<R>) new SearchResult<>(response);
	}

	private String sqlQuery(String buildQuery) {

		StringBuilder builder = new StringBuilder();
		builder.append(
				"SELECT CREATED_ON,CREATED_BY,FILE_NAME,TOTAL_RECORDS, ");
		builder.append("PROCESSED_RECORDS,ERROR_RECORDS,ID,FILE_STATUS FROM FILE_STATUS ");
		builder.append("WHERE  ");
		builder.append(buildQuery);
		return builder.toString();
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

	public List<Object[]> fileStatus(String build, String dataType,
			String fileType, LocalDate dataRecvFrom, LocalDate dataRecvTo,
			String source) {
		Query q = entityManager.createNativeQuery(build);
		if (dataType != null) {
		q.setParameter("dataType", dataType.toUpperCase());
		}
		if (fileType != null) {
		q.setParameter("fileType", fileType);
		}
		if (dataRecvFrom != null) {
		q.setParameter("dataRecvFrom", dataRecvFrom);
		}
		if (dataRecvTo != null) {
		q.setParameter("dataRecvTo", dataRecvTo);
		}
		if (source != null) {
			q.setParameter("source", source);
		}
		List<Object[]> list = q.getResultList();
		return list;
	}
}
