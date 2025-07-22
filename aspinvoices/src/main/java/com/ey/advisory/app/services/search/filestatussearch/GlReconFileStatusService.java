package com.ey.advisory.app.services.search.filestatussearch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.docs.dto.FileStatusReqDto;
import com.ey.advisory.app.docs.dto.FileStatusResponseDto;
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
@Component("GlReconFileStatusService")
@Slf4j
public class GlReconFileStatusService implements SearchService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings({ "unchecked" })
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		List<Gstr1FileStatusEntity> response = new ArrayList<>();
		try {
			FileStatusReqDto req = (FileStatusReqDto) criteria;

			LocalDate dataRecvFrom = req.getDataRecvFrom();
			LocalDate dataRecvTo = req.getDataRecvTo();

			String source = req.getSource();

			List<FileStatusResponseDto> fileStatusResponseDtos = new ArrayList<>();
			StringBuilder build = new StringBuilder();

			if (dataRecvFrom != null && dataRecvTo != null) {

				build.append(" AND TO_DATE(CREATED_ON) BETWEEN :dataRecvFrom "
						+ "AND :dataRecvTo ");
			}

			if (source != null) {
				build.append(" AND SOURCE = :source ");
			}

			String buildQuery = build.toString().substring(4);
			String sql = sqlQuery(buildQuery);

			Query q = entityManager.createNativeQuery(sql);
			if (dataRecvFrom != null && dataRecvTo != null) {
			q.setParameter("dataRecvFrom", dataRecvFrom);
			q.setParameter("dataRecvTo", dataRecvTo);
			}
			if (source != null) {
				q.setParameter("source", source);
			}
			List<Object[]> list = q.getResultList();

			for (Object[] obj : list) {

				FileStatusResponseDto fileStatusResDto = new FileStatusResponseDto();

				String uploadedDate = null;
				if (obj[0] != null) {
					String dateTime = String.valueOf(obj[0]);
					uploadedDate = dateTime;
				}
				String uploadedBy = (obj[1] != null)
						? String.valueOf(obj[1]).trim() : "";
				String dataType = (obj[2] != null)
						? String.valueOf(obj[2]).trim() : "";
				Long fileId = null;
				if (obj[3].toString() != null) {
					String idStr = String.valueOf(obj[3]).trim();
					fileId = Long.valueOf(idStr);
				}
				String fileType = (obj[4] != null)
						? String.valueOf(obj[4]).trim() : "";
				String fileName = (obj[5] != null)
						? String.valueOf(obj[5]).trim() : "";
				String fileStatus = (obj[6] != null)
						? String.valueOf(obj[6]).trim() : "";
				String transformationStatus = (obj[7] != null)
						? String.valueOf(obj[7]).trim() : "";
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

				Integer activeError = 0;
				if (obj[11] != null) {
					String activeErrorStr = String.valueOf(obj[11]).trim();
					activeError = Integer.valueOf(activeErrorStr);
				}
				Integer inActiveError = 0;
				if (obj[12] != null) {
					String inActiveErrorStr = String.valueOf(obj[12]).trim();
					inActiveError = Integer.valueOf(inActiveErrorStr);
				}

				fileStatusResDto.setId(fileId);
				fileStatusResDto.setUploadedOn(uploadedDate);
				fileStatusResDto.setUploadedBy(uploadedBy);
				fileStatusResDto.setFileType(fileType);
				fileStatusResDto.setFileName(fileName);
				fileStatusResDto.setDataType(dataType);
				fileStatusResDto.setFileStatus(fileStatus);
				fileStatusResDto.setTotal(totalRecordRes);
				fileStatusResDto.setProcessedActive(activeProcessed);
				fileStatusResDto.setProcessedInactive(inActiveProcessed);
				fileStatusResDto.setErrorsActive(activeError);
				fileStatusResDto.setErrorsInactive(inActiveError);
				fileStatusResDto.setTransformationStatus(transformationStatus);

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
		builder.append("SELECT TO_DATE(CREATED_ON) AS DATE_OF_UPLOAD, "
				+ "CREATED_BY AS UPLOADED_BY,'GL Recon' AS DATA_TYPE,ID, "
				+ "FILE_TYPE,FILE_NAME,FILE_STATUS,TRANSFORMATION_STATUS, "
				+ "TOTAL_RECORDS,TOTAL_PSD_ACTIVE,TOTAL_PSD_INACTIVE, "
				+ "TOTAL_ERR_ACTIVE,TOTAL_ERR_INACTIVE FROM "
				+ "GL_RECON_FILE_STATUS WHERE " + buildQuery
				+ " ORDER BY ID DESC");
		
		return builder.toString();
	}

	@Override
	public <R> Stream<R> find(SearchCriteria arg0, Class<? extends R> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
