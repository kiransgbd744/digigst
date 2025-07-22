package com.ey.advisory.app.ims.handlers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsERPRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsRevIntgMetaRepository;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */
@Slf4j
@Component("ImsRevIntgServiceImpl")
public class ImsRevIntgServiceImpl implements ImsRevIntgService {

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	@Autowired
	private AnxErpBatchRepository batchRepo;

	@Autowired
	private DestinationConnectivity connectivity;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	private AnxErpBatchHandler anxErpBatchHandler;

	@Autowired
	private ImsRevIntgMetaRepository meta2aprRepo;

	@Autowired
	@Qualifier("imsERPRequestRepository")
	private ImsERPRequestRepository erpReqRepo;

	static String date = null;

	public static List<String> distinctPan = null;

	private static final String P_BATCHID = "P_BATCHID";
	private static final String FAILED = "FAILED";
	public static final String CONF_KEY = "ims.rev.intg.chunk.size";

	public static final DateTimeFormatter dtf = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	@Override
	public Integer getImsErpPush(ImsRevIntgReqDto dto) {

		Integer respCode = 0;
		Integer chunkSize = null;

		try {

			ImsERPRequestEntity entity = erpReqRepo
					.findByBatchId(dto.getBatchId());

			Map<String, Config> configMap = configManager
					.getConfigs("IMSREVINTG", CONF_KEY, "DEFAULT");
			chunkSize = configMap.get(CONF_KEY) == null ? 10000
					: Integer.valueOf(configMap.get(CONF_KEY).getValue());

			try {

				int noOfChunk = getChunk(entity, chunkSize);

				if (noOfChunk == 0) {

					entity.setIsErpPush(false);
					entity.setStatus("NO_DATA");
					entity.setUpdatedOn(LocalDateTime.now());
					erpReqRepo.save(entity);
					return 0;
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Total Header-item no of chunks for revIntg {} "
									+ "and batchId {}",
							noOfChunk, entity.getBatchId());
				}

				respCode = pushData(entity, noOfChunk, dto);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Recieved respCode for reverse push  {}",
							respCode);
				}

				if (respCode == HttpURLConnection.HTTP_OK) {

					entity.setIsErpPush(true);
					entity.setErpPushDate(LocalDateTime.now());
					entity.setStatus("SUCCESS");
					entity.setUpdatedOn(LocalDateTime.now());
					erpReqRepo.save(entity);

				} else {
					entity.setIsErpPush(false);
					entity.setErpPushDate(LocalDateTime.now());
					entity.setStatus(FAILED);
					entity.setUpdatedOn(LocalDateTime.now());
					entity.setErrorDescription(String
							.format("ERP Push Error with code %d", respCode));
					erpReqRepo.save(entity);
				}

			} catch (Exception ex) {

				String msg = String.format(
						" Ims ERP PUSH failed for batchId %d",
						entity.getBatchId());
				LOGGER.error(msg, ex);
				if (entity != null) {
					entity.setIsErpPush(false);
					entity.setErpPushDate(LocalDateTime.now());
					entity.setStatus(FAILED);
					entity.setUpdatedOn(LocalDateTime.now());
					entity.setErrorDescription(ex.getMessage());
					erpReqRepo.save(entity);

				}

				throw new AppException(msg, ex);

			}

			return respCode;
		} catch (Exception e) {
			String msg = String.format(" Ims ERP PUSH failed for batchId %d",
					dto.getBatchId());
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage(), e);

		}
	}

	private int getChunk(ImsERPRequestEntity entity, int chunkSize) {
		int noOfChunk = 0;
		String msg = null;

		Long batchId = entity.getBatchId();
		String supplyType = entity.getSupplyType();
		try {
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_IMS_INS_REVERSE_INTEGRATION");

			// Registering input parameters
			storedProc.registerStoredProcedureParameter(P_BATCHID, Long.class,
					ParameterMode.IN);
			storedProc.setParameter(P_BATCHID, batchId);

			storedProc.registerStoredProcedureParameter("P_Section",
					String.class, ParameterMode.IN);
			storedProc.setParameter("P_Section", supplyType);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing Chunk proc USP_IMS_INS_REVERSE_INTEGRATION: '%d'",
						batchId);
				LOGGER.debug(msg);
			}

			Object result = storedProc.getSingleResult();
			Integer chunksize = null;
			if (result instanceof BigInteger) {
				chunksize = ((BigInteger) result).intValue();
			} else if (result instanceof Integer) {
				chunksize = (Integer) result;
			} else {
				throw new IllegalArgumentException("Unexpected result type: "
						+ result.getClass().getName());
			}

			noOfChunk = chunksize;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Chunk proc Executed "
						+ "USP_IMS_INS_REVERSE_INTEGRATION: batchId '%d', noOfChunk %d ",
						batchId, noOfChunk);
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			msg = String.format("Error while executing Chunk proc batchId %d",
					batchId);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return noOfChunk;
	}

	private Integer pushData(ImsERPRequestEntity e, int noOfChunk,
			ImsRevIntgReqDto dto) {

		Integer respCode = 0;
		ImsRevIntgMetaEntity metaEntity = null;
		AnxErpBatchEntity batchEntity = null;

		String destinationName = dto.getDestinationName();
		Long erpId = dto.getErpId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reverse push started for DestName {} and erpId  {}",
					destinationName, erpId);
		}

		try {

			int counter = 0;
			while (counter < noOfChunk) {

				counter++;

				StoredProcedureQuery dispProc = entityManager
						.createStoredProcedureQuery(
								"USP_IMS_DISP_REVERSE_INTEGRATION");

				dispProc.registerStoredProcedureParameter(P_BATCHID, Long.class,
						ParameterMode.IN);

				dispProc.setParameter(P_BATCHID, e.getBatchId());

				dispProc.registerStoredProcedureParameter("P_Section",
						String.class, ParameterMode.IN);

				dispProc.setParameter("P_Section", e.getSupplyType());

				dispProc.registerStoredProcedureParameter("P_CHUNK_VALUE",
						Integer.class, ParameterMode.IN);

				dispProc.setParameter("P_CHUNK_VALUE", counter);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Data proc about to Execute"
							+ " USP_IMS_DISP_REVERSE_INTEGRATION: batchId '%d', "
							+ "noOfChunk %d ", e.getBatchId(), counter);
					LOGGER.debug(msg);
				}

				@SuppressWarnings("unchecked")
				List<Object[]> records = dispProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Data proc Executed"
									+ " USP_IMS_DISP_REVERSE_INTEGRATION: batchId '%d', "
									+ "noOfChunk %d record size %d",
							e.getBatchId(), counter, records.size());
					LOGGER.debug(msg);
				}

				if (records != null && !records.isEmpty()) {

					List<ImsRevIntgDataDto> dtoList = records.stream()
							.map(o -> convertToHeaderItemDto(o))
							.collect(Collectors.toCollection(ArrayList::new));

					ImsErpPushDto erpPushDto = new ImsErpPushDto();
					List<ImsErpPushDto.ImsItemDto> items = new ArrayList<>();

					for (ImsRevIntgDataDto data : dtoList) {
						ImsErpPushDto.ImsItemDto itemDto = new ImsErpPushDto.ImsItemDto();

						itemDto.setActionGstn(data.getAction());
						itemDto.setTableType(data.getTableType());
						itemDto.setRecipientGstin(data.getRecipientGstin());
						itemDto.setSupplierGstin(data.getSupplierGstin());
						itemDto.setSupplierLName(data.getSupplierLegalName());
						itemDto.setSupplierTName(data.getSupplierTradeName());
						itemDto.setDocumentType(data.getDocumentType());
						itemDto.setDocumentNumber(data.getDocumentNumber());
						itemDto.setDocumentDate(data.getDocumentDate() != null
								? data.getDocumentDate().toString() : null);
						itemDto.setTaxableValue(data.getTaxableValue() != null
								? data.getTaxableValue().toString() : null);
						itemDto.setIgst(data.getIgst().toString());
						itemDto.setCgst(data.getCgst().toString());
						itemDto.setSgst(data.getSgst().toString());
						itemDto.setCess(data.getCess().toString());
						itemDto.setInvoiceValue(data.getInvoiceValue() != null
								? data.getInvoiceValue().toString() : null);
						itemDto.setPos(data.getPos() != null
								? data.getPos().toString() : null);
						itemDto.setFormType(data.getFormType());
						itemDto.setGstr1FilStatus(data.getGstr1FilingStatus());
						itemDto.setGstr1FilPeriod(data.getGstr1FilingPeriod());
						itemDto.setOriginalDocNum(
								data.getOriginalDocumentNumber());
						itemDto.setOriginalDocDate(
								data.getOriginalDocumentDate() != null ? data
										.getOriginalDocumentDate().toString()
										: null);
						itemDto.setPendingActBlocked(
								data.getPendingActionBlocked());
						itemDto.setChecksum(data.getChecksum());
						itemDto.setGetCallDateTime(
								data.getGetCallDateTime() != null
										? data.getGetCallDateTime().toString()
										: null);
						itemDto.setActiveInIMS_GSTN(data.getActiveInIms());
						itemDto.setEntityName(data.getEntityName());
						itemDto.setFlag(data.getFlag());
						items.add(itemDto);
					}

					erpPushDto.setItems(items);

					batchEntity = erpBatchHandler.createErpBatch(
							dto.getGroupCode(), null, dto.getGstin(),
							dto.getDestinationName(), dto.getScenarioId(),
							Long.valueOf(records.size()), null,
							ERPConstants.EVENT_BASED_JOB, dto.getErpId(), null,
							APIConstants.SYSTEM.toUpperCase());

					String subGstin = null;
					if (e.getGstin().length() > 14) {
						subGstin = e.getGstin().substring(0, 15);
					} else {
						subGstin = e.getGstin();
					}

					metaEntity = craeteMetaRecord(e.getBatchId(),
							e.getBatchId(), subGstin, e.getTotalRecord(),
							counter, Long.valueOf(dtoList.size()));

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Reverse push started for SAP {} "
										+ "and erpId {} and itemSize {}",
								destinationName, erpId, dtoList.size());
					}

					respCode = connectivity.pushToErp(erpPushDto,
							"ImsErpPushDto", batchEntity);

					if (respCode != HttpURLConnection.HTTP_OK) {
						metaEntity.setStatus("Failed");
						metaEntity.setUpdatedDate(LocalDateTime.now());
						meta2aprRepo.save(metaEntity);
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Received respCode {} for requestId {}",
								respCode, e.getRequestId());
					}
					if (respCode != HttpURLConnection.HTTP_OK) {
						String msg = String
								.format("Ims reverse intg has failed with "
										+ "code %s, ", respCode);
						throw new AppException(msg);
					}
					if (batchEntity != null) {
						batchEntity.setHttpCode(respCode);
						batchEntity.setHttpStatus("OK");
						batchEntity.setSuccess(true);
						batchRepo.save(batchEntity);
					}
				}
			}

		} catch (Exception ex) {
			anxErpBatchHandler.updateErpBatch(batchEntity, respCode, null,
					false, null, ex.getMessage());

			String msg = String.format(
					"ImsRevIntgServiceImpl :"
							+ "Error while executing Disp proc batchId %d",
					e.getRequestId());
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return respCode;

	}

	private ImsRevIntgDataDto convertToHeaderItemDto(Object[] arr) {

		ImsRevIntgDataDto dto = new ImsRevIntgDataDto();

		dto.setAction(arr[0] != null ? String.valueOf(arr[0]) : null);

		dto.setTableType(arr[1] != null ? String.valueOf(arr[1]) : null);

		dto.setRecipientGstin(arr[2] != null ? String.valueOf(arr[2]) : null);

		dto.setSupplierGstin(arr[3] != null ? String.valueOf(arr[3]) : null);

		dto.setSupplierLegalName(
				arr[4] != null ? String.valueOf(arr[4]) : null);

		dto.setSupplierTradeName(
				arr[5] != null ? String.valueOf(arr[5]) : null);

		dto.setDocumentType(arr[6] != null ? String.valueOf(arr[6]) : null);

		dto.setDocumentNumber(arr[7] != null ? String.valueOf(arr[7]) : null);

		dto.setDocumentDate(arr[8] != null
				? formatToDDMMYYYY(parseDate(String.valueOf(arr[8]))) : null);

		dto.setTaxableValue(arr[9] != null
				? new BigDecimal(String.valueOf(arr[9])) : BigDecimal.ZERO);

		dto.setIgst(arr[10] != null ? new BigDecimal(String.valueOf(arr[10]))
				: BigDecimal.ZERO);

		dto.setCgst(arr[11] != null ? new BigDecimal(String.valueOf(arr[11]))
				: BigDecimal.ZERO);

		dto.setSgst(arr[12] != null ? new BigDecimal(String.valueOf(arr[12]))
				: BigDecimal.ZERO);

		dto.setCess(arr[13] != null ? new BigDecimal(String.valueOf(arr[13]))
				: BigDecimal.ZERO);

		dto.setInvoiceValue(arr[14] != null
				? new BigDecimal(String.valueOf(arr[14])) : BigDecimal.ZERO);

		dto.setPos(arr[15] != null ? Integer.valueOf(String.valueOf(arr[15]))
				: null);

		dto.setFormType(arr[16] != null ? String.valueOf(arr[16]) : null);

		dto.setGstr1FilingStatus(
				arr[17] != null ? String.valueOf(arr[17]) : null);

		dto.setGstr1FilingPeriod(
				arr[18] != null ? String.valueOf(arr[18]) : null);

		dto.setOriginalDocumentNumber(
				arr[19] != null ? String.valueOf(arr[19]) : null);

		dto.setOriginalDocumentDate(arr[20] != null
				? formatToDDMMYYYY(parseDate(String.valueOf(arr[20]))) : null);

		dto.setPendingActionBlocked(
				arr[21] != null ? String.valueOf(arr[21]) : null);

		dto.setChecksum(arr[22] != null ? String.valueOf(arr[22]) : null);

		dto.setGetCallDateTime(arr[23] != null
				? formatDateTime(String.valueOf(arr[23]),
						"yyyy-MM-dd HH:mm:ss.SSS", "dd-MM-yyyy HH:mm:ss")
				: null);

		dto.setActiveInIms(arr[24] != null ? String.valueOf(arr[24]) : null);
		
		dto.setEntityName(arr[25] != null ? String.valueOf(arr[25]) : null);

		dto.setFlag(arr[26] != null ? String.valueOf(arr[26]) : null);
		
		return dto;

	}
	
	private static LocalDate parseDate(String dateString) {
		DateTimeFormatter formatter = null;

		if (dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
			formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		} else if (dateString.matches("\\d{2}-\\d{2}-\\d{4}")) {
			formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		} else {
			throw new IllegalArgumentException(
					"Unexpected date format: " + dateString);
		}

		return LocalDate.parse(dateString, formatter);
	}

	private static String formatToDDMMYYYY(LocalDate date) {
		if (date == null) {
			return null;
		}

		DateTimeFormatter outputFormatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy");
		return date.format(outputFormatter);
	}

	public static String formatDateTime(String dateTimeString,
			String inputPattern, String outputPattern) {
		DateTimeFormatter inputFormatter = DateTimeFormatter
				.ofPattern(inputPattern);
		DateTimeFormatter outputFormatter = DateTimeFormatter
				.ofPattern(outputPattern);

		LocalDateTime dateTime = LocalDateTime.parse(dateTimeString,
				inputFormatter);
		
		LocalDateTime istDateTime = EYDateUtil.toISTDateTimeFromUTC(dateTime);

		return istDateTime.format(outputFormatter);
	}

	private ImsRevIntgMetaEntity craeteMetaRecord(Long batchId,
			Long reportConfigId, String gstin, Long ttlCntrlRec,
			Integer headerChunkId, Long ttlChunkRec) {
		ImsRevIntgMetaEntity e = new ImsRevIntgMetaEntity();
		e.setBatchId(batchId);
		e.setGstin(gstin);
		e.setTotalRecord(ttlCntrlRec);
		e.setTotalChunkRecordSent(ttlChunkRec);
		e.setHeaderChunkId(headerChunkId);
		e.setStatus("INITIATED");
		e.setCreatedDate(LocalDateTime.now());
		meta2aprRepo.save(e);
		return e;
	}

}
