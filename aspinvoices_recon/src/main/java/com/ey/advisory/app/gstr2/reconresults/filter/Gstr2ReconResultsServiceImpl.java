package com.ey.advisory.app.gstr2.reconresults.filter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.asprecon.ReconResultsUIGstr2AprEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReconResultsUIGstr2BprEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ReconResultsUIGstr2AprRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReconResultsUIGstr2BprRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.common.collect.Lists;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("Gstr2ReconResultsServiceImpl")
public class Gstr2ReconResultsServiceImpl implements Gstr2ReconResultsService {

	@Autowired
	ReconResultsUIGstr2BprRepository recon2bprUiRepo;

	@Autowired
	ReconResultsUIGstr2AprRepository recon2aprUiRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public String reconResponseUpload(Gstr2ReconResultsReqDto reqDto,
			Long fileId, String userName, Long batchId, String identifier) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						" inside Gstr2ReconResultsServiceImpl for batchId %s",
						batchId.toString());
				LOGGER.debug(msg);
			}

			if (reqDto.getReconType().equalsIgnoreCase("2A_PR")) {
				List<ReconResultsUIGstr2AprEntity> recon2AprEntity = new ArrayList<>();

				if (reqDto.getReconLinkId().isEmpty())
					return null;

				for (Long reconId : reqDto.getReconLinkId()) {
					ReconResultsUIGstr2AprEntity entity = new ReconResultsUIGstr2AprEntity();
					entity.setBatchId(batchId);
					entity.setReconLinkId(reconId);
					entity.setRespRemarks(
							reqDto.getResponseRemarks() != null
									? StringUtils.truncate(
											reqDto.getResponseRemarks(), 500)
									: null);
					if (reqDto.getIndentifier().equalsIgnoreCase("Force")) {
						entity.setUserResp("Lock");
						entity.setFmResponse("Lock");
					} else if (reqDto.getIndentifier().equalsIgnoreCase("3B")) {
						entity.setUserResp(reqDto.getTaxPeriodGstr3b());
						entity.setRspTaxPeriod3B(reqDto.getTaxPeriodGstr3b());
					}else if (reqDto.getIndentifier().equalsIgnoreCase("Unlock"))
					{
						entity.setUserResp("Unlock");
						entity.setFmResponse("Unlock");
					}
					
					if("Multi".equalsIgnoreCase(identifier))
					{
						entity.setSource("2A/6AvsPR:MultiUnlock");
						entity.setIdentifier("MultiUnlock");
					}else if (reqDto.getIndentifier().equalsIgnoreCase("IMS")){
						
						entity.setImsUserResponse(reqDto.getImsUserResponse());
						entity.setImsResponseRemarks(reqDto.getImsResponseRemarks());
					}
					/*
					 * entity.setRspTaxPeriod3B(reqDto.getTaxPeriodGstr3b() !=
					 * null ? reqDto.getTaxPeriodGstr3b() : null);
					 */
					entity.setAvaiCgst(reqDto.getAvaiCgst() != null
							&& !reqDto.getAvaiCgst().isEmpty()
									? new BigDecimal(reqDto.getAvaiCgst())
									: null);
					entity.setAvaiSgst(reqDto.getAvaiSgst() != null
							&& !reqDto.getAvaiSgst().isEmpty()
									? new BigDecimal(reqDto.getAvaiSgst())
									: null);
					entity.setAvaiIgst(reqDto.getAvaiIgst() != null
							&& !reqDto.getAvaiIgst().isEmpty()
									? new BigDecimal(reqDto.getAvaiIgst())
									: null);
					entity.setAvaiCess(reqDto.getAvaiCess() != null
							&& !reqDto.getAvaiCess().isEmpty()
									? new BigDecimal(reqDto.getAvaiCess())
									: null);
					entity.setCreatedOn(LocalDateTime.now());
					entity.setFileId(fileId);
					entity.setItcReversal(reqDto.getItcReversal());
					recon2AprEntity.add(entity);
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							" reconLinkidList %s for batchId %s",
							recon2AprEntity, batchId.toString());
					LOGGER.debug(msg);
				}

				List<List<ReconResultsUIGstr2AprEntity>> chunks = Lists
						.partition(recon2AprEntity, 2000);
				for (List<ReconResultsUIGstr2AprEntity> chunk : chunks) {
					recon2aprUiRepo.saveAll(chunk);
				}

			} else {

				List<ReconResultsUIGstr2BprEntity> recon2BprEntity = new ArrayList<>();

				if (reqDto.getReconLinkId().isEmpty())
					return null;

				for (Long reconId : reqDto.getReconLinkId()) {
					ReconResultsUIGstr2BprEntity entity = new ReconResultsUIGstr2BprEntity();
					entity.setBatchId(batchId);
					entity.setReconLinkId(reconId);
					entity.setRespRemarks(
							reqDto.getResponseRemarks() != null
									? StringUtils.truncate(
											reqDto.getResponseRemarks(), 500)
									: null);

					if (reqDto.getIndentifier().equalsIgnoreCase("Force")) {
						entity.setUserResp("Lock");
						entity.setFmResponse("Lock");
					} else if (reqDto.getIndentifier().equalsIgnoreCase("3B")) {
						entity.setUserResp(reqDto.getTaxPeriodGstr3b());
						entity.setRspTaxPeriod3B(reqDto.getTaxPeriodGstr3b());
					}else if (reqDto.getIndentifier().equalsIgnoreCase("Unlock"))
					{
						entity.setUserResp("Unlock");
						entity.setFmResponse("Unlock");
					}else if (reqDto.getIndentifier().equalsIgnoreCase("IMS")){
						
						entity.setImsUserResponse(reqDto.getImsUserResponse());
						entity.setImsResponseRemarks(reqDto.getImsResponseRemarks());
					}
					if("Multi".equalsIgnoreCase(identifier))
					{
						entity.setSource("2BPR:MultiUnlock");
						entity.setIdentifier("MultiUnlock");
					}
					entity.setAvaiCgst(reqDto.getAvaiCgst() != null
							&& !reqDto.getAvaiCgst().isEmpty()
									? new BigDecimal(reqDto.getAvaiCgst())
									: null);
					entity.setAvaiSgst(reqDto.getAvaiSgst() != null
							&& !reqDto.getAvaiSgst().isEmpty()
									? new BigDecimal(reqDto.getAvaiSgst())
									: null);
					entity.setAvaiIgst(reqDto.getAvaiIgst() != null
							&& !reqDto.getAvaiIgst().isEmpty()
									? new BigDecimal(reqDto.getAvaiIgst())
									: null);
					entity.setAvaiCess(reqDto.getAvaiCess() != null
							&& !reqDto.getAvaiCess().isEmpty()
									? new BigDecimal(reqDto.getAvaiCess())
									: null);
					entity.setCreatedOn(LocalDateTime.now());
					entity.setFileId(fileId);
					entity.setItcReversal(reqDto.getItcReversal());
					recon2BprEntity.add(entity);
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							" reconLinkidList %s for batchId %s",
							recon2BprEntity, batchId.toString());
					LOGGER.debug(msg);
				}
				List<List<ReconResultsUIGstr2BprEntity>> chunks = Lists
						.partition(recon2BprEntity, 2000);
				for (List<ReconResultsUIGstr2BprEntity> chunk : chunks) {
					recon2bprUiRepo.saveAll(chunk);
				}
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(" data saved for batchId %s",
						batchId.toString());
				LOGGER.debug(msg);
			}

			/*
			 * jsonParams.addProperty("batchId", batchId);
			 * jsonParams.addProperty("fileId", fileId);
			 * jsonParams.addProperty("reconType", reqDto.getReconType());
			 * 
			 * String groupCode = TenantContext.getTenantId();
			 * asyncJobsService.createJob(groupCode,
			 * JobConstants.GSTR2_RECON_RESPONSE, jsonParams.toString(),
			 * userName, 1L, null, null);
			 */
		} catch (Exception ex) {
			String msg = String.format(
					"Batch Id is '%s', result result processing failed",
					batchId.toString());
			LOGGER.error(msg, ex);
			gstr1FileStatusRepository.updateFailedStatus(fileId,
					StringUtils.truncate(ex.getLocalizedMessage(), 4000),
					"Failed");
			throw new AppException(ex);
		}

		return batchId.toString();
	}

	@Override
	public List<Gstr2ReconResultsRequestStatusRespDto> requestStatusData(
			Long entityId,String identifier) {
		try {
			List<Gstr2ReconResultsRequestStatusRespDto> resp = new ArrayList<>();
			resp = getRequestStatusData(entityId,identifier);
			return resp;
		} catch (Exception e) {
			LOGGER.error("Error while fetching the request screen data ", e);
			throw new AppException(e);
		}
	}

	private List<Gstr2ReconResultsRequestStatusRespDto> getRequestStatusData(
			Long entityId,String identifier) {
		try {

			List<Gstr2ReconResultsRequestStatusRespDto> respData = new ArrayList<>();
			String queryStr = createQuery(identifier);

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

	private String createQuery(String identifier) {
		
		if("SINGLE".equalsIgnoreCase(identifier))
		{
		return " SELECT to_varchar(CREATED_ON,'yyyyMMdd'), ID, "
				+ " FILE_STATUS, TOTAL_RECORDS, PROCESSED_RECORDS, ERROR_RECORDS, CREATED_ON, "
				+ " SOURCE, DATA_TYPE, ERROR_FILE_NAME from FILE_STATUS WHERE "
				+ "SOURCE IN('2A/6AvsPR','2BvsPR','2A/6AvsPR + IMS','2BvsPR + IMS') "
				+ " AND DATA_TYPE NOT IN ('Multi Unlock','Multi Unlock (Bulk Response)') "
				+ " AND ENTITY_ID =:entityId ORDER BY ID;";
		}
		else
		{
		return " SELECT to_varchar(CREATED_ON,'yyyyMMdd'), ID, "
					+ " FILE_STATUS, TOTAL_RECORDS, PROCESSED_RECORDS, ERROR_RECORDS, CREATED_ON, "
					+ " SOURCE, DATA_TYPE, ERROR_FILE_NAME from FILE_STATUS WHERE "
					+ "SOURCE IN('2A/6AvsPR','2BvsPR','2A/6AvsPR + IMS','2BvsPR + IMS') "
					+ " AND DATA_TYPE IN ('Multi Unlock','Multi Unlock (Bulk Response)') "
					+ " AND ENTITY_ID =:entityId ORDER BY ID;";
		}
	}

	private Gstr2ReconResultsRequestStatusRespDto convertToDto(Object arr[]) {

		Gstr2ReconResultsRequestStatusRespDto dto = new Gstr2ReconResultsRequestStatusRespDto();

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
			dto.setErrFilePath(arr[9] != null ? arr[9].toString() : null);
		}
		// to-do conversion
		dto.setDate(arr[6].toString() != null
				? dateChange(arr[6].toString().substring(0, 19)) : null);
		dto.setReconType(
				(arr[7].toString() != null) ? arr[7].toString() : null);
		dto.setReconResponseAction(
				(arr[8].toString() != null) ? arr[8].toString() : null);

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
}
