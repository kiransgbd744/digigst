/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsActionResponseUIRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportStatusConstants;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ImsRequestStatusServiceImpl")
public class ImsRequestStatusServiceImpl implements ImsRequestStatusService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	ImsActionResponseUIRepository saveRepo;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	ImsActionResponseUiErrorDwldService reporService;

	@Override
	public String saveTosttagingTable(ImsActioResponseReqDto reqDto,
			Long fileId, String userName, Long batchId) {

		List<ImsActionResponseUiEntity> imsList = new ArrayList<>();

		try {

			List<String> imsUniqueIdList = reqDto.getImsUniqueId();
			for (String imsUniqueId : imsUniqueIdList) {
				ImsActionResponseUiEntity entity = new ImsActionResponseUiEntity();
				entity.setBatchId(batchId);
				entity.setFileId(fileId);
				entity.setImsResponse(reqDto.getActionTaken());
				entity.setResponseRemarks(reqDto.getResponseRemark());
				entity.setImsUniqueId(imsUniqueId);
				entity.setCreatedOn(LocalDateTime.now());
				entity.setCreatedBy(userName);
				entity.setItcRedRequired(reqDto.getItcRedRequired());
				entity.setDeclIgst(convertToBigDecimal(reqDto.getDeclIgst()));
				entity.setDeclSgst(convertToBigDecimal(reqDto.getDeclSgst()));
				entity.setDeclCgst(convertToBigDecimal(reqDto.getDeclCgst()));
				entity.setDeclCess(convertToBigDecimal(reqDto.getDeclCess()));
				imsList.add(entity);
			}

			saveRepo.saveAll(imsList);

			String response = callProseesingProc(batchId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"ImsActionResponseUiProcessor response %s "
								+ "from USP_IMS_ACTION_RESPONSE_UI batchId %d ",
						response, batchId);
				LOGGER.debug(msg);
			}

			if (response.equalsIgnoreCase("SUCCESS")) {

				fileStatusRepository.updateFileStatus(fileId, "Completed");

			}

			Integer errorCount = fileStatusRepository.findById(fileId).get()
					.getError();

			if (errorCount != null) {

				FileStatusDownloadReportEntity reportReqEntity = new FileStatusDownloadReportEntity();

				if (LOGGER.isDebugEnabled()) {
					String msg = "Setting request data to entity to SaveorPersist";
					LOGGER.debug(msg);
				}

				reportReqEntity.setFileId(fileId);

				reportReqEntity.setReportType("error");
				reportReqEntity.setCreatedBy(userName);
				reportReqEntity.setCreatedDate(LocalDateTime.now());
				reportReqEntity
						.setReportStatus(ReportStatusConstants.INITIATED);
				reportReqEntity.setReportCateg("IMS");
				reportReqEntity.setDataType("Inward");

				reportReqEntity = fileStatusDownloadReportRepo
						.save(reportReqEntity);

				Long id = reportReqEntity.getId();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Successfully saved to DB with Report Id : %d", id);
					LOGGER.debug(msg);
				}

				reporService.generateReports(id, batchId);

			}

		} catch (Exception ex) {
			LOGGER.error("ImsRequestStatusServiceImpl - "
					+ "Exception occured while saving into ImsActionResponseUiEntity");

			throw new AppException(ex);

		}

		return "succsess";
	}

	@Override
	public List<ImsRequestStatusRespDto> requestStatusData(Long entityId) {
		try {
			List<ImsRequestStatusRespDto> resp = new ArrayList<>();
			resp = getRequestStatusData(entityId);
			return resp;
		} catch (Exception e) {
			LOGGER.error("Error while fetching the request screen data ", e);
			throw new AppException(e);
		}
	}

	private List<ImsRequestStatusRespDto> getRequestStatusData(Long entityId) {
		try {

			List<ImsRequestStatusRespDto> respData = new ArrayList<>();
			String queryStr = createQuery();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"query string created for recon result request status {}",
						queryStr);
			}
			Query q = entityManager.createNativeQuery(queryStr);

			q.setParameter("entityId", entityId);

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = q.getResultList();

			if (Collections.isEmpty(resultList))
				return null;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("with the query string total objects obtained {}",
						resultList.size());
			}

			respData = resultList.stream().map(o -> convertToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"with the query string the partitioned objects obtained {}",
						respData);
			}
			return respData;
		} catch (Exception ex) {
			String msg = String.format(
					"Entity Id is '%d', result result processing failed",
					entityId);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private String createQuery() {

		return " SELECT to_varchar(CREATED_ON,'yyyyMMdd'), ID, "
				+ " FILE_STATUS, TOTAL_RECORDS, PROCESSED_RECORDS, ERROR_RECORDS, CREATED_ON, "
				+ "  DOC_ID from FILE_STATUS WHERE SOURCE = 'IMS_UI' "
				+ " AND ENTITY_ID =:entityId ORDER BY ID;";
	}

	private ImsRequestStatusRespDto convertToDto(Object arr[]) {

		ImsRequestStatusRespDto dto = new ImsRequestStatusRespDto();

		dto.setReqId((arr[0].toString() != null && arr[1].toString() != null)
				? generateCustomId(Long.valueOf(arr[1].toString()),
						arr[0].toString())
				: null);
		dto.setFileId((arr[1].toString() != null) ? arr[1].toString() : null);
		dto.setReqStatus(
				(arr[2].toString() != null) ? arr[2].toString() : null);
		dto.setTotalRecords(
				(arr[3].toString() != null) ? arr[3].toString() : "0");
		dto.setProcessed((arr[4].toString() != null) ? arr[4].toString() : "0");
		dto.setError((arr[5].toString() != null) ? arr[5].toString() : "0");
		if (arr[5].toString() != null
				&& Integer.parseInt(arr[5].toString()) > 0) {
			dto.setErrorDownld(true);
			dto.setErrFilePath(arr[7] != null ? arr[7].toString() : null);
			dto.setDocId(arr[7] != null ? arr[7].toString() : null);
		}
		// to-do conversion
		dto.setDate(arr[6].toString() != null
				? dateChange(arr[6].toString().substring(0, 19))
				: null);

		return dto;
	}

	private static String generateCustomId(Long fileId, String createdOn) {
		String reportId = "";
		if (fileId != null && fileId > 0) {

			reportId = createdOn.concat(String.valueOf(fileId));
		}
		return reportId;
	}

	private String dateChange(String oldDate) {
		DateTimeFormatter formatter = null;
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTimes = LocalDateTime.parse(oldDate, formatter);
		LocalDateTime dateTimeFormatter = EYDateUtil
				.toISTDateTimeFromUTC(dateTimes);
		DateTimeFormatter FOMATTER = DateTimeFormatter
				.ofPattern("dd-MM-yyyy : HH:mm:ss");
		String newdate = FOMATTER.format(dateTimeFormatter);
		return newdate;

	}

	private String callProseesingProc(Long batchId) {

		String response = null;

		try {

			LOGGER.debug("before Invoke USP_IMS_ACTION_RESPONSE_UI batchId {}",
					batchId);
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_IMS_ACTION_RESPONSE_UI");

			storedProc.registerStoredProcedureParameter("BATCH_ID", Long.class,
					ParameterMode.IN);

			storedProc.setParameter("BATCH_ID", batchId);

			response = (String) storedProc.getSingleResult();

		} catch (Exception ex) {
			String msg = String.format(
					"Error occured in while calling USP_IMS_ACTION_RESPONSE_UI "
							+ "for batchId %d ",
					batchId);
			LOGGER.error(msg, ex);

		}

		return response;
	}

	private BigDecimal convertToBigDecimal(String value) {
		if (value == null || value.trim().isEmpty()) {
			return BigDecimal.ZERO; // Or null if your entity allows it
		}
		try {
			return new BigDecimal(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Invalid number format for value: " + value, e);
		}
	}
}
