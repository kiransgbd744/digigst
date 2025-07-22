package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.AutoRecon2AERPMetaEntity;
import com.ey.advisory.app.data.entities.client.asprecon.AutoRecon2AERPRequestEntity;
import com.ey.advisory.app.data.repositories.client.AutoRecon2AERPMetaRepository;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoRecon2AERPRequestRepository;
import com.ey.advisory.app.docs.dto.erp.GSTR2aAutoReconRevIntgHeaderDto;
import com.ey.advisory.app.docs.dto.erp.GSTR2aAutoReconRevIntgHeaderItemDto;
import com.ey.advisory.app.docs.dto.erp.GSTR2aAutoReconRevIntgItemDto;
import com.ey.advisory.app.docs.dto.erp.GSTR2aAutoReconRevIntgPayloadDto;
import com.ey.advisory.app.docs.dto.erp.GSTR2aAutoReconSFTPRevIntgItemDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.sftp.service.GSTR2ASftpResponseHandler;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.repositories.master.EinvMasterGstinRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("GSTR2aAutoReconRevIntgServiceImpl")
public class GSTR2aAutoReconRevIntgServiceImpl
		implements GSTR2aAutoReconRevIntgService {

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	@Autowired
	private AnxErpBatchRepository batchRepo;

	@Autowired
	private AutoRecon2AERPRequestRepository reqRepo;

	@Autowired
	private AutoReconStatusRepository autoReconStatusRepo;

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
	private GSTR2ASftpResponseHandler sftpResponseHandler;

	@Autowired
	private AutoRecon2AERPMetaRepository meta2aprRepo;

	@Autowired
	private EinvMasterGstinRepository einvMasterGstinRepo;

	@Autowired
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	static String date = null;

	public static List<String> distinctPan = null;

	private static final String REQUEST_ID = "P_REQUEST_ID";
	private static final String FAILED = "FAILED";
	public static final String CONF_KEY = "gstr2a.rev.intg.chunk.size";
	public static final String SFTP_CONF_KEY = "gstr2a.sftp.rev.intg.chunk.size";
	
	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;

	public static final DateTimeFormatter dtf = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");
	public static DateTimeFormatter dateFormatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");
	public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
	public static List<String> status = Arrays.asList("INITIATED", "WAITING",
			"FAILED", "Job_Posted");

	@Override
	public Integer autoReconGet2AToErpPush(GSTR2aAutoReconRevIntgReqDto dto) {

		Integer respCode = 0;
		Integer chunkSize = null;

		try {

			List<AutoRecon2AERPRequestEntity> entities = reqRepo
					.findRequestId(dto.getConfigId(), dto.getGstin(), status);

			if (entities.isEmpty()) {

				LOGGER.error(
						"RequestId not found while performing reverse "
								+ "integration for configId {} and gstin {},"
								+ "hence aborting the process",
						dto.getConfigId(), dto.getGstin());
				autoReconStatusRepo.updateERPPushStatus(FAILED,
						LocalDateTime.now(), dto.getAutoReconId());

				throw new AppException(String.format(
						"RequestId not found for configId %d and gstin %s",
						dto.getConfigId(), dto.getGstin()));
			}

			if ("NONSAP".equalsIgnoreCase(dto.getDestinationType())) {
				Map<String, Config> configMap = configManager
						.getConfigs("GSTR2AREVINTG", SFTP_CONF_KEY, "DEFAULT");
				chunkSize = configMap.get(SFTP_CONF_KEY) == null ? 10000
						: Integer.valueOf(
								configMap.get(SFTP_CONF_KEY).getValue());

			} else {
				Map<String, Config> configMap = configManager
						.getConfigs("GSTR2AREVINTG", CONF_KEY, "DEFAULT");
				chunkSize = configMap.get(CONF_KEY) == null ? 10000
						: Integer.valueOf(configMap.get(CONF_KEY).getValue());
			}

			Pair<String, String> entityInfo = getEntityInfo(
					dto.getGstin().trim());

			for (AutoRecon2AERPRequestEntity e : entities) {

				try {

					int noOfChunk = getChunk(e.getRequestId(), chunkSize);

					if (noOfChunk == 0) {

						e.setERPPush(false);
						e.setStatus("NO_DATA");
						e.setUpdatedOn(LocalDateTime.now());
						reqRepo.save(e);
						if (dto.getAutoReconId() != null) {
							autoReconStatusRepo.updateERPPushStatus("NO_DATA",
									LocalDateTime.now(), dto.getAutoReconId());
						}
						continue;
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Total no of chunks for rev intg {} and requuestId {}",
								noOfChunk, e.getRequestId());
					}

					respCode = pushData(e, noOfChunk, dto,
							entityInfo.getValue0(), entityInfo.getValue1());

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Recieved respCode for reverse push  {}",
								respCode);
					}

					if (respCode == HttpURLConnection.HTTP_OK) {

						e.setERPPush(true);
						e.setErpPushDate(LocalDateTime.now());
						e.setStatus("SUCCESS");
						e.setUpdatedOn(LocalDateTime.now());
						reqRepo.save(e);
						if (dto.getAutoReconId() != null) {
							autoReconStatusRepo.updateERPPushStatus("SUCCESS",
									LocalDateTime.now(), dto.getAutoReconId());
						}
					} else {
						e.setERPPush(false);
						e.setErpPushDate(LocalDateTime.now());
						e.setStatus(FAILED);
						e.setUpdatedOn(LocalDateTime.now());
						e.setErrorDesc(String.format(
								"ERP Push Error with code %d", respCode));
						reqRepo.save(e);

						if (dto.getAutoReconId() != null) {
							autoReconStatusRepo.updateERPPushStatus(FAILED,
									LocalDateTime.now(), dto.getAutoReconId());
						}
					}
				} catch (Exception ex) {

					String msg = String.format(
							" AutoReconGSTR2A ERP PUSH failed for RequestId %d",
							e.getRequestId());
					LOGGER.error(msg, ex);
					if (e != null) {
						e.setERPPush(false);
						e.setErpPushDate(LocalDateTime.now());
						e.setStatus(FAILED);
						e.setUpdatedOn(LocalDateTime.now());
						e.setErrorDesc(ex.getMessage());
						reqRepo.save(e);

					}

					if (dto.getAutoReconId() != null) {
						autoReconStatusRepo.updateERPPushStatus(FAILED,
								LocalDateTime.now(), dto.getAutoReconId());
					}

				}
			}
			return respCode;
		} catch (Exception e) {
			String msg = String.format(
					" AutoReconGSTR2A ERP PUSH failed for AutoReconId %d",
					dto.getAutoReconId());

			autoReconStatusRepo.updateERPPushStatus(FAILED, LocalDateTime.now(),
					dto.getAutoReconId());

			LOGGER.error(msg, e);
			throw new AppException(e.getMessage(), e);

		}
	}

	private int getChunk(Long configId, int chunkSize) {
		int noOfChunk = 0;
		String msg = null;
		try {

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_AUTO_2APR_INS_CHUNK_ERP_GSTIN");

			storedProc.registerStoredProcedureParameter(REQUEST_ID, Long.class,
					ParameterMode.IN);

			storedProc.setParameter(REQUEST_ID, configId);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_AUTO_2APR_INS_CHUNK_ERP_GSTIN: '%d'",
						configId);
				LOGGER.debug(msg);
			}

			Integer chunksize = (Integer) storedProc.getSingleResult();

			noOfChunk = chunksize;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_AUTO_2APR_INS_CHUNK_ERP_GSTIN: configId '%d', "
						+ "noOfChunk %d ", configId, noOfChunk);
				LOGGER.debug(msg);
			}

		} catch (Exception ex) {

			msg = String.format(
					"Error while executing chunking proc " + "configId %d",
					configId);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return noOfChunk;
	}

	private Integer pushData(AutoRecon2AERPRequestEntity e, int noOfChunk,
			GSTR2aAutoReconRevIntgReqDto dto, String entityName,
			String entityPan) {

		String localTimeString = LocalDateTime.now().toString();
		String localDateString = LocalDate.now().toString();
		Integer respCode = 0;
		// Integer totalItems = 0;
		AutoRecon2AERPMetaEntity metaEntity = null;
		AnxErpBatchEntity batchEntity = null;
		// GSTNDetailEntity gstinInfo = gstnDetailRepository
		// .findByGstinAndIsDeleteFalse(dto.getGstin());
		// Long gstinId = gstinInfo.getId();
		//
		// ErpEventsScenarioPermissionEntity scenarioPermision =
		// erpEventsScenPermissionRepo
		// .findByScenarioIdAndErpIdAndIsDeleteFalse(dto.getScenarioId(),
		// dto.getErpId());
		String destinationName = dto.getDestinationName();
		Long erpId = dto.getErpId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reverse push started for DestName {} and erpId  {}",
					destinationName, erpId);
		}

		try {
			distinctPan = einvMasterGstinRepo.getAllDistinctPan();

			Map<String, Config> configMap = configManager.getConfigs("EINVAPP",
					"einv.applicable.date");

			date = configMap.get("einv.applicable.date") == null ? "NA"
					: String.valueOf(
							configMap.get("einv.applicable.date").getValue());

			int counter = 0;

			while (counter < noOfChunk) {

				counter++;

				StoredProcedureQuery dispProc = entityManager
						.createStoredProcedureQuery(
								"USP_AUTO_2APR_DISP_CHUNK_ERP_GSTIN");

				dispProc.registerStoredProcedureParameter(REQUEST_ID,
						Long.class, ParameterMode.IN);

				dispProc.setParameter(REQUEST_ID, e.getRequestId());

				dispProc.registerStoredProcedureParameter("P_CHUNK_VALUE",
						Integer.class, ParameterMode.IN);

				dispProc.setParameter("P_CHUNK_VALUE", counter);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Data proc Executed"
									+ " USP_AUTO_2APR_DISP_CHUNK_ERP_GSTIN: configId '%d', "
									+ "noOfChunk %d ",
							e.getRequestId(), counter);
					LOGGER.debug(msg);
				}

				@SuppressWarnings("unchecked")
				List<Object[]> records = dispProc.getResultList();

				if (records != null && !records.isEmpty()) {

					batchEntity = erpBatchHandler.createErpBatch(
							dto.getGroupCode(), null, dto.getGstin(),
							dto.getDestinationName(), dto.getScenarioId(),
							Long.valueOf(records.size()), null,
							ERPConstants.BACKGROUND_BASED_JOB, dto.getErpId(),
							null, APIConstants.SYSTEM.toUpperCase());

					batchRepo.save(batchEntity);

					if ("NONSAP".equalsIgnoreCase(dto.getDestinationType())) {

						List<GSTR2aAutoReconSFTPRevIntgItemDto> itemsList = records
								.stream()
								.map(o -> convertForSftp(o, dto.getRetType()))
								.collect(Collectors
										.toCollection(ArrayList::new));

						// totalItems += itemsList.size();

						String fileName = createSftpFileName(e.getGstin(),
								String.valueOf(counter),
								e.getSourceIdentifier()) + ".csv";

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Reverse push started for NONSAP {} "
											+ "and erpId {} and itemSize {}",
									destinationName, erpId, itemsList.size());
						}
						respCode = sftpResponseHandler
								.uploadToSftpServer(fileName, itemsList);

					} else {
						List<GSTR2aAutoReconRevIntgItemDto> itemsList = records
								.stream()
								.map(o -> convert(o, localTimeString,
										localDateString, dto.getRetType(),
										entityName, entityPan))
								.collect(Collectors
										.toCollection(ArrayList::new));
						GSTR2aAutoReconRevIntgHeaderDto hdrDto = new GSTR2aAutoReconRevIntgHeaderDto();
						GSTR2aAutoReconRevIntgHeaderItemDto hdrItemDto = new GSTR2aAutoReconRevIntgHeaderItemDto();
						GSTR2aAutoReconRevIntgPayloadDto payloadDto = new GSTR2aAutoReconRevIntgPayloadDto();
						String subGstin = null;
						if (e.getGstin().length() > 14) {
							subGstin = e.getGstin().substring(0, 15);
						} else {
							subGstin = e.getGstin();
						}

						payloadDto.setItemDtos(itemsList);
						// totalItems += itemsList.size();
						hdrItemDto.setControlId(
								String.valueOf(e.getReconConfigID()));
						hdrItemDto.setGstin(subGstin);
						hdrItemDto.setTotalCntrlRec(e.getTotalRecord());
						hdrItemDto.setChunkId(String.valueOf(counter));
						hdrItemDto.setTotalChunkRec(
								Long.valueOf(itemsList.size()));
						hdrItemDto.setPayload(payloadDto);
						hdrDto.setItemHdr(hdrItemDto);
						metaEntity = craeteMetaRecord(e.getRequestId(),
								e.getReconConfigID(), subGstin,
								e.getTotalRecord(), counter,
								Long.valueOf(itemsList.size()));

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Reverse push started for SAP {} "
											+ "and erpId {} and itemSize {}",
									destinationName, erpId, itemsList.size());
						}

						respCode = connectivity.pushToErp(hdrDto,
								"GSTR2aAutoReconRevIntgHeaderDto", batchEntity);

						if (respCode != HttpURLConnection.HTTP_OK) {
							metaEntity.setStatus("Failed");
							metaEntity.setUpdatedDate(LocalDateTime.now());
							meta2aprRepo.save(metaEntity);
						}
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Received respCode {} for requestId {}",
								respCode, e.getRequestId());
					}
					if (respCode != HttpURLConnection.HTTP_OK) {
						String msg = String.format(
								"2APR reverse intg has failed with code %s, ",
								respCode);
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
					"Error while executing Disp proc " + "configId %d",
					e.getRequestId());
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return respCode;

	}

	private GSTR2aAutoReconSFTPRevIntgItemDto convertForSftp(Object[] arr,
			String retType) {

		GSTR2aAutoReconSFTPRevIntgItemDto obj = new GSTR2aAutoReconSFTPRevIntgItemDto();

		obj.setUserResponse((arr[0] != null) ? arr[0].toString() : null);
		obj.setTaxPeriodGSTR3B((arr[1] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[1].toString())
				: null);
		obj.setMatchReason((arr[2] != null) ? arr[2].toString() : null);
		obj.setMisMatchReason((arr[3] != null) ? arr[3].toString() : null);
		obj.setReportCategory((arr[4] != null) ? arr[4].toString() : null);
		obj.setReportType((arr[5] != null) ? arr[5].toString() : null);
		obj.setErpReportType((arr[6] != null) ? arr[6].toString() : null);
		obj.setTaxPeriod2A((arr[7] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[7].toString())
				: null);
		obj.setTaxPeriod2B((arr[8] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString())
				: null);
		obj.setTaxPeriodPR((arr[9] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString())
				: null);
		obj.setRgstin2A((arr[10] != null) ? arr[10].toString() : null);
		obj.setRgstinPR((arr[11] != null) ? arr[11].toString() : null);
		obj.setSgstin2A((arr[12] != null) ? arr[12].toString() : null);
		obj.setSgstinPR((arr[13] != null) ? arr[13].toString() : null);
		obj.setDocType2A((arr[14] != null) ? arr[14].toString() : null);
		obj.setDocTypePR((arr[15] != null) ? arr[15].toString() : null);
		obj.setDocNumber2A((arr[16] != null) ? arr[16].toString() : null);
		obj.setDocNumberPR((arr[17] != null) ? arr[17].toString() : null);
		obj.setDocDate2A((arr[18] != null) ? arr[18].toString() : null);
		obj.setDocDatePR((arr[19] != null) ? arr[19].toString() : null);
		obj.setPos2A((arr[20] != null) ? arr[20].toString() : null);
		obj.setPosPR((arr[21] != null) ? arr[21].toString() : null);

		obj.setTaxableValue2A((arr[22] != null) ? arr[22].toString() : null);
		obj.setTaxableValuePR((arr[23] != null) ? arr[23].toString() : null);

		obj.setIgst2A((arr[24] != null) ? arr[24].toString() : null);
		obj.setIgstPR((arr[25] != null) ? arr[25].toString() : null);
		obj.setCgst2A((arr[26] != null) ? arr[26].toString() : null);
		obj.setCgstPR((arr[27] != null) ? arr[27].toString() : null);
		obj.setSgst2A((arr[28] != null) ? arr[28].toString() : null);
		obj.setSgstPR((arr[29] != null) ? arr[29].toString() : null);
		obj.setCess2A((arr[30] != null) ? arr[30].toString() : null);
		obj.setCessPR((arr[31] != null) ? arr[31].toString() : null);
		obj.setInvoiceValue2A((arr[32] != null) ? arr[32].toString() : null);
		obj.setInvoiceValuePR((arr[33] != null) ? arr[33].toString() : null);
		obj.setItcAvailable2B((arr[34] != null) ? arr[34].toString() : null);
		obj.setItcUnavailable2B((arr[35] != null) ? arr[35].toString() : null);
		obj.setGstr1FilingStat((arr[36] != null) ? arr[36].toString() : null);
		obj.setGstr1FilingDate((arr[37] != null) ? arr[37].toString() : null);
		obj.setGstr1FilingPeriod(
				(arr[38] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[38].toString()) : null);
		obj.setGstr3BFilingStat((arr[39] != null) ? arr[39].toString() : null);
		obj.setRevChrgFlag2A((arr[40] != null) ? arr[40].toString() : null);
		obj.setRevChrgFlagPR((arr[41] != null) ? arr[41].toString() : null);
		obj.setPlantCode((arr[42] != null) ? arr[42].toString() : null);
		obj.setDivision((arr[43] != null) ? arr[43].toString() : null);
		obj.setPurchaseOrg((arr[44] != null) ? arr[44].toString() : null);
		obj.setTableType2A((arr[45] != null) ? arr[45].toString() : null);
		obj.setSupplyType2A((arr[46] != null) ? arr[46].toString() : null);
		obj.setSupplyTypePR((arr[47] != null) ? arr[47].toString() : null);
		obj.setAccNo((arr[48] != null) ? arr[48].toString() : null);
		obj.setAccDate((arr[49] != null) ? arr[49].toString() : null);
		obj.setApprovalStatus((arr[50] != null) ? arr[50].toString() : null);

		obj.setRecordStatus((arr[51] != null) ? arr[51].toString() : null);
		obj.setKeyDescription((arr[52] != null) ? arr[52].toString() : null);
		obj.setReconDate((arr[56] != null) ? arr[56].toString() : null);
		obj.setRevIntDate(LocalDate.now().toString());
		obj.setIrn2A((arr[57] != null) ? arr[57].toString() : null);
		obj.setIrnDate2A((arr[58] != null) ? arr[58].toString() : null);
		obj.setUserDefinedField1((arr[59] != null) ? arr[59].toString() : null);
		obj.setUserDefinedField2((arr[60] != null) ? arr[60].toString() : null);
		obj.setUserDefinedField3((arr[61] != null) ? arr[61].toString() : null);
		obj.setUserDefinedField4((arr[62] != null) ? arr[62].toString() : null);
		obj.setUserDefinedField5((arr[63] != null) ? arr[63].toString() : null);
		obj.setRequestId(
				(arr[64] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[64].toString()) : null);
		obj.setIdPR((arr[65] != null) ? arr[65].toString() : null);
		obj.setId2A((arr[66] != null) ? arr[66].toString() : null);
		obj.setResponseRemarks((arr[67] != null) ? (arr[67].toString()) : null);
		obj.setEligibilityIndicator(
				(arr[68] != null) ? (arr[68].toString()) : null);
		obj.setAvailableIgst((arr[69] != null) ? (arr[69].toString()) : null);
		obj.setAvailableCgst((arr[70] != null) ? (arr[70].toString()) : null);
		obj.setAvailableSgst((arr[71] != null) ? (arr[71].toString()) : null);
		obj.setAvailableCess((arr[72] != null) ? (arr[72].toString()) : null);
		obj.setReturnFilingFreq(
				(arr[73] != null) ? (arr[73].toString()) : null);
		obj.setSgstinCanDate((arr[74] != null) ? (arr[74].toString()) : null);
		obj.setVendorCompTrend((arr[75] != null) ? (arr[75].toString()) : null);
		obj.setSupplierCode((arr[76] != null) ? (arr[76].toString()) : null);
		obj.setBoeRefDate2A((arr[77] != null) ? (arr[77].toString()) : null);
		obj.setPortCode2A((arr[78] != null) ? (arr[78].toString()) : null);
		obj.setPortCodePR((arr[79] != null) ? (arr[79].toString()) : null);
		obj.setBillOfEntry2A((arr[80] != null) ? (arr[80].toString()) : null);
		obj.setBillOfEntryPR((arr[81] != null) ? (arr[81].toString()) : null);
		obj.setBoeDate2A((arr[82] != null) ? (arr[82].toString()) : null);
		obj.setBoeDatePR((arr[83] != null) ? (arr[83].toString()) : null);

		obj.setCompanyCode((arr[84] != null) ? (arr[84].toString()) : null);
		obj.setSourceIdentifier(
				(arr[85] != null) ? (arr[85].toString()) : null);
		obj.setVendorType((arr[86] != null) ? (arr[86].toString()) : null);
		obj.setHsn((arr[87] != null) ? (arr[87].toString()) : null);
		obj.setVendorRiskCateg((arr[88] != null) ? (arr[88].toString()) : null);
		obj.setVendorPymntTerm((arr[89] != null) ? (arr[89].toString()) : null);
		obj.setVendorRemarks((arr[90] != null) ? (arr[90].toString()) : null);

		String gstin2A = (arr[12] != null) ? arr[12].toString() : "";
		
		if (gstin2A.length() == 15) {
			String pan2A = gstin2A.substring(2, 12);
			if (distinctPan.contains(pan2A))
				obj.setEinvApplicability(
						"Applicable as per NIC Master(Last Updated : " + date
								+ ")");
			else
				obj.setEinvApplicability(
						"Not Applicable as per NIC Master(Last Updated : "
								+ date + ")");
		}

		/*
		 * obj.setEinvApplicability( (arr[91] != null) ? (arr[91].toString()) :
		 * null);
		 */
		obj.setQrCodeCheck((arr[92] != null) ? (arr[92].toString()) : null);
		obj.setQrValidResult((arr[93] != null) ? (arr[93].toString()) : null);
		obj.setQrCodeMatchCount(
				(arr[94] != null) ? (arr[94].toString()) : null);
		obj.setQrCodeMisMatchCount(
				(arr[95] != null) ? (arr[95].toString()) : null);
		obj.setQrMismatchAttr((arr[96] != null) ? (arr[96].toString()) : null);
		obj.setGstr3BFilingDate(
				(arr[97] != null) ? (arr[97].toString()) : null);
		obj.setSupplierGstinStatus(
				(arr[98] != null) ? (arr[98].toString()) : null);

		obj.setSystemDefinedField1(
				(arr[99] != null) ? (arr[99].toString()) : null);
		obj.setSystemDefinedField2(
				(arr[100] != null) ? (arr[100].toString()) : null);
		//US 111198:Auto Recon 108DFs
		obj.setSystemDefinedField3((arr[101] != null)
				? (new BigDecimal(arr[101].toString())).toString() : null);
		obj.setSystemDefinedField4((arr[102] != null)
				? (new BigDecimal(arr[102].toString())).toString() : null);
		obj.setSystemDefinedField5(
				(arr[103] != null) ? (arr[103].toString()) : null);
		obj.setSystemDefinedField6(
				(arr[104] != null) ? (arr[104].toString()) : null);
		obj.setSystemDefinedField7(
				(arr[105] != null) ? (arr[105].toString()) : null);
		obj.setSystemDefinedField8(
				(arr[106] != null) ? (arr[106].toString()) : null);
		//US 111198:Auto Recon 108DFs
		if (arr[107] != null) {
			LocalDateTime generationDate2B = LocalDateTime
					.parse(arr[107].toString(), dateTimeFormatter);
			obj.setSystemDefinedField9(EYDateUtil
					.toLocalDateTimeFromUTC(generationDate2B).toString().replace("T", " "));
		} else {
			obj.setSystemDefinedField9(null);
		}
		if (arr[108] != null) {
			LocalDateTime generationDate2A = LocalDateTime
					.parse(arr[108].toString(), dateTimeFormatter);
			obj.setSystemDefinedField10(EYDateUtil
					.toLocalDateTimeFromUTC(generationDate2A).toString().replace("T", " "));
		} else {
			obj.setSystemDefinedField10(null);
		}

		// Suggested Resp
		if ((arr[109] != null && (!arr[109].toString().isEmpty())
				&& (arr[109].toString().matches("[0-9]+")))) {

			obj.setSuggestedResponse(arr[109].toString().length() == 5
					? "'".concat(("0").concat(arr[109].toString()))
					: "'".concat((arr[109].toString())));
		} else {
			obj.setSuggestedResponse(
					(arr[109] != null) ? (arr[109].toString()) : null);
		}

		obj.setRetType(retType);

		return obj;

	}

	private GSTR2aAutoReconRevIntgItemDto convert(Object[] arr,
			String createdOn, String createdDate, String retType,
			String entityName, String entityPan) {
		GSTR2aAutoReconRevIntgItemDto obj = new GSTR2aAutoReconRevIntgItemDto();

		obj.setUserResp((arr[0] != null) ? arr[0].toString() : null);
		obj.setTaxPeriodGstr3B((arr[1] != null) ? (arr[1].toString()) : null);
		obj.setMatchReason((arr[2] != null) ? arr[2].toString() : null);
		obj.setMisMatchReason((arr[3] != null) ? arr[3].toString() : null);
		obj.setReportCategory((arr[4] != null) ? arr[4].toString() : null);
		obj.setReportType((arr[5] != null) ? arr[5].toString() : null);
		obj.setErpReportType((arr[6] != null) ? arr[6].toString() : null);
		obj.setTaxPeriod2A((arr[7] != null) ? (arr[7].toString()) : null);
		obj.setTaxPeriod2B((arr[8] != null) ? (arr[8].toString()) : null);
		obj.setTaxPeriodPR((arr[9] != null) ? (arr[9].toString()) : null);
		obj.setRgstin2A((arr[10] != null) ? arr[10].toString() : null);
		obj.setRgstinPR((arr[11] != null) ? arr[11].toString() : null);
		obj.setSgstin2A((arr[12] != null) ? arr[12].toString() : null);
		obj.setSgstinPR((arr[13] != null) ? arr[13].toString() : null);
		obj.setDocType2A((arr[14] != null) ? arr[14].toString() : null);
		obj.setDocTypePR((arr[15] != null) ? arr[15].toString() : null);
		obj.setDocNumber2A((arr[16] != null) ? arr[16].toString() : null);
		obj.setDocNumberPR((arr[17] != null) ? arr[17].toString() : null);
		obj.setDocDate2A((arr[18] != null) ? arr[18].toString() : null);
		obj.setDocDatePR((arr[19] != null) ? arr[19].toString() : null);
		obj.setPos2A((arr[20] != null) ? arr[20].toString() : null);
		obj.setPosPR((arr[21] != null) ? arr[21].toString() : null);

		obj.setTaxableValue2A((arr[22] != null)
				? new BigDecimal(arr[22].toString()) : BigDecimal.ZERO);
		obj.setTaxableValuePR((arr[23] != null)
				? new BigDecimal(arr[23].toString()) : BigDecimal.ZERO);

		obj.setIgst2A((arr[24] != null) ? new BigDecimal(arr[24].toString())
				: BigDecimal.ZERO);
		obj.setIgstPR((arr[25] != null) ? new BigDecimal(arr[25].toString())
				: BigDecimal.ZERO);
		obj.setCgst2A((arr[26] != null) ? new BigDecimal(arr[26].toString())
				: BigDecimal.ZERO);
		obj.setCgstPR((arr[27] != null) ? new BigDecimal(arr[27].toString())
				: BigDecimal.ZERO);
		obj.setSgst2A((arr[28] != null) ? new BigDecimal(arr[28].toString())
				: BigDecimal.ZERO);
		obj.setSgstPR((arr[29] != null) ? new BigDecimal(arr[29].toString())
				: BigDecimal.ZERO);
		obj.setCess2A((arr[30] != null) ? new BigDecimal(arr[30].toString())
				: BigDecimal.ZERO);
		obj.setCessPR((arr[31] != null) ? new BigDecimal(arr[31].toString())
				: BigDecimal.ZERO);
		obj.setInvoiceValue2A((arr[32] != null)
				? new BigDecimal(arr[32].toString()) : BigDecimal.ZERO);
		obj.setInvoiceValuePR((arr[33] != null)
				? new BigDecimal(arr[33].toString()) : BigDecimal.ZERO);
		obj.setItcAvailable2B((arr[34] != null) ? arr[34].toString() : null);
		obj.setItcUnavailable2B((arr[35] != null) ? arr[35].toString() : null);
		obj.setGstr1FilingStat((arr[36] != null) ? arr[36].toString() : null);
		obj.setGstr1FilingDate((arr[37] != null) ? arr[37].toString() : null);
		obj.setGstr1FilingPeriod(
				(arr[38] != null) ? (arr[38].toString()) : null);
		obj.setGstr3BFilingStat((arr[39] != null) ? arr[39].toString() : null);
		obj.setRevChrgFlag2A((arr[40] != null) ? arr[40].toString() : null);
		obj.setRevChrgFlagPR((arr[41] != null) ? arr[41].toString() : null);
		obj.setPlantCode((arr[42] != null) ? arr[42].toString() : null);
		obj.setDivision((arr[43] != null) ? arr[43].toString() : null);
		obj.setPurchaseOrg((arr[44] != null) ? arr[44].toString() : null);
		obj.setTableType2A((arr[45] != null) ? arr[45].toString() : null);
		obj.setSupplyType2A((arr[46] != null) ? arr[46].toString() : null);
		obj.setSupplyTypePR((arr[47] != null) ? arr[47].toString() : null);
		obj.setAccNo((arr[48] != null) ? arr[48].toString() : null);
		obj.setAccDate((arr[49] != null) ? arr[49].toString() : null);
		obj.setApprovalStatus((arr[50] != null) ? arr[50].toString() : null);

		obj.setIsDelink((arr[51] != null) ? arr[51].toString() : null);

		obj.setDeLinkReason((arr[52] != null) ? arr[52].toString() : null);
		obj.setReconLinkId((arr[55] != null) ? arr[55].toString() : null);
		obj.setInvKey2A((arr[53] != null) ? arr[53].toString() : null);
		obj.setInvKeyPR((arr[54] != null) ? arr[54].toString() : null);
		obj.setReconDate((arr[56] != null) ? arr[56].toString() : null);
		obj.setIrn2A((arr[57] != null) ? arr[57].toString() : null);
		obj.setIrnDate2A((arr[58] != null) ? arr[58].toString() : null);
		obj.setUserDefinedField1((arr[59] != null) ? arr[59].toString() : null);
		obj.setUserDefinedField2((arr[60] != null) ? arr[60].toString() : null);
		obj.setUserDefinedField3((arr[61] != null) ? arr[61].toString() : null);
		obj.setUserDefinedField4((arr[62] != null) ? arr[62].toString() : null);
		obj.setUserDefinedField5((arr[63] != null) ? arr[63].toString() : null);
		obj.setRequestId(
				(arr[64] != null) ? Long.valueOf(arr[64].toString()) : 0L);
		obj.setIdPR((arr[65] != null) ? Long.valueOf(arr[65].toString()) : 0L);
		obj.setId2A((arr[66] != null) ? Long.valueOf(arr[66].toString()) : 0L);
		obj.setResponseRemarks((arr[67] != null) ? (arr[67].toString()) : null);
		obj.setEligibilityIndicator(
				(arr[68] != null) ? (arr[68].toString()) : null);
		obj.setAvailableIgst((arr[69] != null) ? (arr[69].toString()) : null);
		obj.setAvailableCgst((arr[70] != null) ? (arr[70].toString()) : null);
		obj.setAvailableSgst((arr[71] != null) ? (arr[71].toString()) : null);
		obj.setAvailableCess((arr[72] != null) ? (arr[72].toString()) : null);
		obj.setReturnFilingFreq(
				(arr[73] != null) ? (arr[73].toString()) : null);
		obj.setSgstinCanDate((arr[74] != null) ? (arr[74].toString()) : null);
		obj.setVendorCompTrend((arr[75] != null) ? (arr[75].toString()) : null);
		obj.setSupplierCode((arr[76] != null) ? (arr[76].toString()) : null);
		obj.setBoeRefDate2A((arr[77] != null) ? (arr[77].toString()) : null);
		obj.setPortCode2A((arr[78] != null) ? (arr[78].toString()) : null);
		obj.setPortCodePR((arr[79] != null) ? (arr[79].toString()) : null);
		obj.setBillOfEntry2A((arr[80] != null) ? (arr[80].toString()) : null);
		obj.setBillOfEntryPR((arr[81] != null) ? (arr[81].toString()) : null);
		obj.setBoeDate2A((arr[82] != null) ? (arr[82].toString()) : null);
		obj.setBoeDatePR((arr[83] != null) ? (arr[83].toString()) : null);

		obj.setCompanyCode((arr[84] != null) ? (arr[84].toString()) : null);
		obj.setSourceIdentifier(
				(arr[85] != null) ? (arr[85].toString()) : null);
		obj.setVendorType((arr[86] != null) ? (arr[86].toString()) : null);
		obj.setHsn((arr[87] != null) ? (arr[87].toString()) : null);
		obj.setVendorRiskCateg((arr[88] != null) ? (arr[88].toString()) : null);
		obj.setVendorPymntTerm((arr[89] != null) ? (arr[89].toString()) : null);
		obj.setVendorRemarks((arr[90] != null) ? (arr[90].toString()) : null);

		String gstin2A = (arr[12] != null) ? arr[12].toString() : "";
		
		if (gstin2A.length() == 15) {
			String pan2A = gstin2A.substring(2, 12);
		
		if (distinctPan.contains(pan2A))
			obj.setEinvApplicability(
					"Applicable as per NIC Master(Last Updated : " + date
							+ ")");
		else
			obj.setEinvApplicability(
					"Not Applicable as per NIC Master(Last Updated : " + date
							+ ")");
		
		}

		/*
		 * obj.setEinvApplicability( (arr[91] != null) ? (arr[91].toString()) :
		 * null);
		 */
		obj.setQrCodeCheck((arr[92] != null) ? (arr[92].toString()) : null);
		obj.setQrValidResult((arr[93] != null) ? (arr[93].toString()) : null);
		obj.setQrCodeMatchCount(
				(arr[94] != null) ? (arr[94].toString()) : null);
		obj.setQrCodeMisMatchCount(
				(arr[95] != null) ? (arr[95].toString()) : null);
		obj.setQrMismatchAttr((arr[96] != null) ? (arr[96].toString()) : null);
		obj.setGstr3BFilingDate(
				(arr[97] != null) ? (arr[97].toString()) : null);
		obj.setSupplierGstinStatus(
				(arr[98] != null) ? (arr[98].toString()) : null);

		obj.setSystemDefinedField1(
				(arr[99] != null) ? (arr[99].toString()) : null);
		obj.setSystemDefinedField2(
				(arr[100] != null) ? (arr[100].toString()) : null);
		//US 111198:Auto Recon 108DFs
		obj.setSystemDefinedField3((arr[101] != null)
				? (new BigDecimal(arr[101].toString())).toString() : null);
		obj.setSystemDefinedField4((arr[102] != null)
				? (new BigDecimal(arr[102].toString())).toString() : null);
		obj.setSystemDefinedField5(
				(arr[103] != null) ? (arr[103].toString()) : null);
		obj.setSystemDefinedField6(
				(arr[104] != null) ? (arr[104].toString()) : null);
		obj.setSystemDefinedField7(
				(arr[105] != null) ? (arr[105].toString()) : null);
		obj.setSystemDefinedField8(
				(arr[106] != null) ? (arr[106].toString()) : null);
		//US 111198:Auto Recon 108DFs
		if (arr[107] != null) {
			LocalDateTime generationDate2B = LocalDateTime
					.parse(arr[107].toString(), dateTimeFormatter);
			obj.setSystemDefinedField9(EYDateUtil
					.toLocalDateTimeFromUTC(generationDate2B).toString().replace("T", " "));
		} else {
			obj.setSystemDefinedField9(null);
		}
		if (arr[108] != null) {
			LocalDateTime generationDate2A = LocalDateTime
					.parse(arr[108].toString(), dateTimeFormatter);
			obj.setSystemDefinedField10(EYDateUtil
					.toLocalDateTimeFromUTC(generationDate2A).toString().replace("T", " "));
		} else {
			obj.setSystemDefinedField10(null);
		}
		obj.setSuggestedResp((arr[109] != null) ? (arr[109].toString()) : null);

		obj.setCreatedOn(createdOn);
		obj.setCreatedDate(createdDate);
		obj.setRetType(retType);
		obj.setEntityName(entityName);
		obj.setEntityPan(entityPan);
		return obj;

	}

	private String createSftpFileName(String gstin, String chunkNo,
			String sourceIdentifier) {
		StringJoiner joiner = new StringJoiner("_");
		String timeMilli = dtf
				.format(EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

		if (sourceIdentifier != null) {
			sourceIdentifier = sourceIdentifier.replaceAll("[./]", "_");
			joiner.add(sourceIdentifier);
		}
		joiner.add(gstin);

		joiner.add(chunkNo).add(timeMilli);
		return joiner.toString();

	}

	private AutoRecon2AERPMetaEntity craeteMetaRecord(Long requestId,
			Long reportConfigId, String gstin, Long ttlCntrlRec,
			Integer chunkId, Long ttlChunkRec) {
		AutoRecon2AERPMetaEntity e = new AutoRecon2AERPMetaEntity();
		e.setRequestId(requestId);
		e.setReconReportConfigId(reportConfigId);
		e.setGstin(gstin);
		e.setTotalRecord(ttlCntrlRec);
		e.setTotalChunkRecordSent(ttlChunkRec);
		e.setChunkId(chunkId);
		e.setStatus("INITIATED");
		e.setCreatedDate(LocalDateTime.now());
		meta2aprRepo.save(e);
		return e;
	}

	private Pair<String, String> getEntityInfo(String gstin) {

		try {
			if (gstin.length() == 15) {
				GSTNDetailEntity gstnDetailEntity = gstnDetailRepository
						.findByGstinAndIsDeleteFalse(gstin);

				EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
						.findEntityByEntityId(gstnDetailEntity.getEntityId());

				return new Pair<>(entityInfoEntity.getEntityName(),
						entityInfoEntity.getPan());
			} else if (gstin.length() == 10) {

				List<EntityInfoEntity> infoList = entityInfoDetailsRepository
						.findByPanAndIsDeleteFalse(gstin);
				EntityInfoEntity entityInfoEntity = infoList.get(0);
				return new Pair<>(entityInfoEntity.getEntityName(),
						entityInfoEntity.getPan());
			} else {
				return new Pair<>(null, null);
			}
		} catch (Exception e) {
			LOGGER.error("Error while getting entity info for {} {}", gstin, e);
			throw new AppException(e);
		}
	}
}
