package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.gstr1A.repositories.client.DocRepositoryGstr1A;
import com.ey.advisory.app.data.repositories.client.Anx2GstnSaveRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GSTINDeleteDataRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1vsEinvReconRespProcessedRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2XProcessedTcsTdsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6GstnDistributionSaveRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6GstnInwardSaveRepository;
import com.ey.advisory.app.data.repositories.client.Itc04DocRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7ProcessedRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("batchHandler")
public class BatchHandler {

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository outwardDocRepo;
	
	@Autowired
	@Qualifier("DocRepositoryGstr1A")
	private DocRepositoryGstr1A docRepositoryGstr1A;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardDocRepo;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepo;

	@Autowired
	private Anx2GstnSaveRepository anx2GstnSaveRepo;

	@Autowired
	private Gstr6GstnInwardSaveRepository inwardSaveRepository;

	@Autowired
	private Gstr6GstnDistributionSaveRepository distributionSaveRepository;

	@Autowired
	private Gstr7ProcessedRepository gstr7ProcessedRepository;

	@Autowired
	private Itc04DocRepository itc04DocRepository;

	@Autowired
	private Gstr2XProcessedTcsTdsRepository gstr2XProcessedTcsTdsRepository;

	@Autowired
	private Gstr1GSTINDeleteDataRepository autoDraftRepo;

	@Autowired
	private Gstr1vsEinvReconRespProcessedRepository deleteRespRepo;

	public void updateAutoDraftBatch(Long gstnBatchId, String groupCode,
			List<Long> ids, String operationType,
			Map<Long, Long> orgCanIdsMap) {

		if (ids != null && gstnBatchId != null) {

			TenantContext.setTenantId(groupCode);
			List<Long> chunk = new ArrayList<>();
			int counter = 1;
			for (Long id : ids) {
				chunk.add(id);
				if (counter % 500 == 0 || counter == ids.size()) {
					autoDraftRepo.updateBatchId(gstnBatchId, chunk);
					chunk.clear();
				}
				counter++;
			}

		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
	}

	public void updateDeleteResponseBatch(Long gstnBatchId, String groupCode,
			List<Long> ids, String operationType,
			Map<Long, Long> orgCanIdsMap) {

		if (ids != null && gstnBatchId != null) {

			TenantContext.setTenantId(groupCode);
			List<Long> chunk = new ArrayList<>();
			int counter = 1;
			for (Long id : ids) {
				chunk.add(id);
				if (counter % 500 == 0 || counter == ids.size()) {
					deleteRespRepo.updateBatchId(gstnBatchId, chunk);
					chunk.clear();
				}
				counter++;
			}

		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
	}

	public Gstr1SaveBatchEntity saveBatch(String sgstin, String returnPeriod,
			String section, String groupCode, String returnType, int batchSize,
			String operationType, Long retryCount, Long userRequestId) {

		Gstr1SaveBatchEntity batch = new Gstr1SaveBatchEntity();
		if (!(sgstin == null && returnPeriod == null && groupCode == null)) {
			// try {
			int derReturnPeriod = GenUtil.convertTaxPeriodToInt(returnPeriod);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			batch.setReturnType(
					returnType != null ? returnType.toUpperCase() : null);
			batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
			batch.setCreatedOn(now);
			batch.setBatchDate(now);
			batch.setGroupCode(groupCode);
			batch.setSgstin(sgstin);
			batch.setReturnPeriod(returnPeriod);
			batch.setDerivedTaxperiod(derReturnPeriod);
			batch.setSection(section != null ? section.toUpperCase() : null);
			batch.setBatchSize(batchSize);
			batch.setOperationType(
					operationType != null ? operationType.toUpperCase() : null);
			batch.setRetryCount(retryCount != null ? retryCount : 0l);
			batch.setUserRequestId(userRequestId != null ? userRequestId : 0l);
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			batch = batchSaveStatusRepo.save(batch);
			LOGGER.debug("Batch is created successfully.");

			/*
			 * } catch (Exception ex) { throw new
			 * AppException("Error in Saving Batch", ex); }
			 */
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
		return batch;

	}

	public Gstr1SaveBatchEntity saveBatch(String sgstin, String returnPeriod,
			String section, String groupCode, String returnType, int batchSize,
			String operationType, Long retryCount, Long userRequestId,
			String origin) {

		Gstr1SaveBatchEntity batch = new Gstr1SaveBatchEntity();
		if (!(sgstin == null && returnPeriod == null && groupCode == null)) {
			// try {
			int derReturnPeriod = GenUtil.convertTaxPeriodToInt(returnPeriod);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			batch.setReturnType(
					returnType != null ? returnType.toUpperCase() : null);
			batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
			batch.setCreatedOn(now);
			batch.setBatchDate(now);
			batch.setGroupCode(groupCode);
			batch.setSgstin(sgstin);
			batch.setReturnPeriod(returnPeriod);
			batch.setDerivedTaxperiod(derReturnPeriod);
			batch.setSection(section != null ? section.toUpperCase() : null);
			batch.setBatchSize(batchSize);
			batch.setOperationType(
					operationType != null ? operationType.toUpperCase() : null);
			batch.setRetryCount(retryCount != null ? retryCount : 0l);
			batch.setUserRequestId(userRequestId != null ? userRequestId : 0l);
			batch.setOrigin(origin);
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			batch = batchSaveStatusRepo.save(batch);
			LOGGER.debug("Batch is created successfully.");

			/*
			 * } catch (Exception ex) { throw new
			 * AppException("Error in Saving Batch", ex); }
			 */
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
		return batch;

	}

	public void deleteBatch(Long gstnBatchId, String groupCode) {

		if (gstnBatchId != null && gstnBatchId != 0) {
			// try {

			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			Optional<Gstr1SaveBatchEntity> batch = batchSaveStatusRepo
					.findById(gstnBatchId);
			if (batch.isPresent()) {
				Gstr1SaveBatchEntity batch1 = batch.get();
				batch1.setDelete(true);
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				batch1.setModifiedOn(now);
				batch1.setModifiedBy(APIConstants.SYSTEM.toUpperCase());
				batchSaveStatusRepo.save(batch1);
				LOGGER.debug("Batch is deleted softly.");
			}
			/*
			 * } catch (Exception ex) { throw new
			 * AppException("Error in deleting Batch", ex); }
			 */
		} else {
			LOGGER.debug("Invalid Batch Id to delete batch.");
		}

	}
	
	public void updateOutwardBatchGstr1A(Long gstnBatchId, String groupCode,
			List<Long> ids, String operationType,
			Map<Long, Long> orgCanIdsMap) {

		if (ids != null && gstnBatchId != null) {

			if (GSTConstants.CAN.equalsIgnoreCase(operationType)) {

				List<Long> canIdsList = new ArrayList<>();
				// If original Doc is exist in the json/list then
				// get the respected CAN id from the orgCanIdsMap to
				// update the CAN invoice in the DB.
				orgCanIdsMap.forEach((k, v) -> {
					if (ids.contains(k)) {
						canIdsList.add(v);
					}
				});
				ids.clear();
				ids.addAll(canIdsList);
			}
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			// try {
			TenantContext.setTenantId(groupCode);
			List<Long> chunk = new ArrayList<>();
			int counter = 1;
			for (Long id : ids) {

				chunk.add(id);
				if (counter % 500 == 0 || counter == ids.size()) {
					docRepositoryGstr1A.updateBatchId(gstnBatchId, chunk, now);
					chunk.clear();
				}
				counter++;
			}
			/*
			 * } catch (Exception ex) { throw new
			 * AppException("Error in updating Batch", ex); }
			 */
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
	}


	public void updateOutwardBatch(Long gstnBatchId, String groupCode,
			List<Long> ids, String operationType,
			Map<Long, Long> orgCanIdsMap) {

		if (ids != null && gstnBatchId != null) {

			if (GSTConstants.CAN.equalsIgnoreCase(operationType)) {

				List<Long> canIdsList = new ArrayList<>();
				// If original Doc is exist in the json/list then
				// get the respected CAN id from the orgCanIdsMap to
				// update the CAN invoice in the DB.
				orgCanIdsMap.forEach((k, v) -> {
					if (ids.contains(k)) {
						canIdsList.add(v);
					}
				});
				ids.clear();
				ids.addAll(canIdsList);
			}
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			// try {
			TenantContext.setTenantId(groupCode);
			List<Long> chunk = new ArrayList<>();
			int counter = 1;
			for (Long id : ids) {

				chunk.add(id);
				if (counter % 500 == 0 || counter == ids.size()) {
					outwardDocRepo.updateBatchId(gstnBatchId, chunk, now);
					chunk.clear();
				}
				counter++;
			}
			/*
			 * } catch (Exception ex) { throw new
			 * AppException("Error in updating Batch", ex); }
			 */
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
	}

	public void updateInwardBatch(Long gstnBatchId, String groupCode,
			List<Long> ids, String operationType,
			Map<Long, Long> orgCanIdsMap) {

		if (ids != null && gstnBatchId != null) {

			if (GSTConstants.CAN.equalsIgnoreCase(operationType)) {

				List<Long> canIdsList = new ArrayList<>();
				// If original Doc is exist in the json/list then
				// get the respected CAN id from the orgCanIdsMap to
				// update the CAN invoice in the DB.
				orgCanIdsMap.forEach((k, v) -> {
					if (ids.contains(k)) {
						canIdsList.add(v);
					}
				});
				ids.clear();
				ids.addAll(canIdsList);
			}
			// try {
			TenantContext.setTenantId(groupCode);
			List<Long> chunk = new ArrayList<>();
			int counter = 1;
			for (Long id : ids) {

				chunk.add(id);
				if (counter % 500 == 0 || counter == ids.size()) {
					inwardDocRepo.updateBatchId(gstnBatchId, chunk);
					chunk.clear();
				}
				counter++;
			}
			/*
			 * } catch (Exception ex) { throw new
			 * AppException("Error in updating Batch", ex); }
			 */
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
	}

	public void updateAnx2ProcBatch(Long gstnBatchId, String groupCode,
			List<Long> ids) {

		if (ids != null && gstnBatchId != null) {

			// try {
			TenantContext.setTenantId(groupCode);
			List<Long> chunk = new ArrayList<>();
			int counter = 1;
			for (Long id : ids) {

				chunk.add(id);
				if (counter % 500 == 0 || counter == ids.size()) {
					anx2GstnSaveRepo.updateBatchId(gstnBatchId, chunk);
					chunk.clear();
				}
				counter++;
			}
			/*
			 * } catch (Exception ex) { throw new
			 * AppException("Error in updating Batch", ex); }
			 */
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
	}

	public String updateRefIdAndTxnId(String groupCode, Long gstnBatchId,
			APIResponse resp) {
		String refId = null;
		// try {
		if (groupCode != null && gstnBatchId != 0 && resp != null) {
			if (resp.isSuccess()) {

				String saveJsonResp = resp.getResponse();
				String txnId = resp.getTxnId();
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = (JsonObject) jsonParser
						.parse(saveJsonResp);
				if (jsonObject.get(APIIdentifiers.REF_ID) != null) {
					refId = jsonObject.get(APIIdentifiers.REF_ID).getAsString();
				} else if (jsonObject
						.get(APIIdentifiers.REFERENECE_ID) != null) {
					refId = jsonObject.get(APIIdentifiers.REFERENECE_ID)
							.getAsString();
				}

				if (refId != null && refId.length() > 0) {
					LocalDateTime now = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					TenantContext.setTenantId(groupCode);
					outwardDocRepo.updateGstnSaveRefId(gstnBatchId, refId,
							LocalDateTime.now());
					batchSaveStatusRepo.updateBatchRefID(refId, gstnBatchId,
							txnId, now);
				}

			} else {
				String errorCode = resp.getError().getErrorCode();
				String errorDesc = resp.getError().getErrorDesc();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Error thrown by Gstn ErrorCode: {} and "
							+ "ErrorDesc: {}", errorCode, errorDesc);
				}
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				TenantContext.setTenantId(groupCode);
				batchSaveStatusRepo.updateErrorMesg(gstnBatchId, errorCode,
						errorDesc, now);
			}

		}
		return refId;
		/*
		 * } catch (Exception e) { throw new AppException(
		 * "Error in updating RefId to BatchSaveStatusRepository", e); }
		 */
	}

	// ---------------------GSTR6------------------------------------//

	public void updateGstr6ProcBatch(Long gstnBatchId, String groupCode,
			List<Long> ids, String section) {
		if (ids != null && gstnBatchId != null) {
			TenantContext.setTenantId(groupCode);
			List<Long> chunk = new ArrayList<>();
			int counter = 1;
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			for (Long id : ids) {
				chunk.add(id);
				if (counter % 500 == 0 || counter == ids.size()) {
					if (APIConstants.B2B.equalsIgnoreCase(section)
							|| APIConstants.B2BA.equalsIgnoreCase(section)
							|| APIConstants.CDN.equalsIgnoreCase(section)
							|| APIConstants.CDNA.equalsIgnoreCase(section)) {
						inwardSaveRepository.updateBatchId(gstnBatchId, chunk,
								now);
						chunk.clear();
					} else if (APIConstants.ISD.equalsIgnoreCase(section)
							|| APIConstants.ISDA.equalsIgnoreCase(section)) {
						distributionSaveRepository.updateBatchId(gstnBatchId,
								chunk, now);
						chunk.clear();
					} else {
						LOGGER.debug("Invalid BatchSection Data for GSTR6");
					}
				}
				counter++;
			}
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
	}

	// -------------------GSTR7----------------------//
	public void updateGstr7ProcBatch(Long gstnBatchId, String groupCode,
			List<Long> ids, String section) {
		if (ids != null && gstnBatchId != null) {
			TenantContext.setTenantId(groupCode);
			List<Long> chunk = new ArrayList<>();
			int counter = 1;
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			for (Long id : ids) {
				chunk.add(id);
				if (counter % 500 == 0 || counter == ids.size()) {
					if (APIConstants.TDS.equalsIgnoreCase(section)
							|| APIConstants.TDSA.equalsIgnoreCase(section)) {
						gstr7ProcessedRepository.updateBatchId(gstnBatchId,
								chunk, now);
						chunk.clear();
					} else {
						LOGGER.debug("Invalid BatchSection Data for GSTR7");
					}
				}
				counter++;
			}
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
	}
	// ---------------------ITC04------------------------------------//

	public void updateItc04ProcBatch(Long gstnBatchId, String groupCode,
			List<Long> ids, String section) {
		if (ids != null && gstnBatchId != null) {
			TenantContext.setTenantId(groupCode);
			List<Long> chunk = new ArrayList<>();
			int counter = 1;
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			for (Long id : ids) {
				chunk.add(id);
				if (counter % 500 == 0 || counter == ids.size()) {
					if (APIConstants.M2JW.equalsIgnoreCase(section)
							|| APIConstants.TABLE5A.equalsIgnoreCase(section)
							|| APIConstants.TABLE5B.equalsIgnoreCase(section)
							|| APIConstants.TABLE5C.equalsIgnoreCase(section)) {
						itc04DocRepository.updateBatchId(gstnBatchId, chunk,
								now);
						chunk.clear();
					} else {
						LOGGER.debug("Invalid BatchSection Data for ITC04");
					}
				}
				counter++;
			}
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
	}

	public Long itc04SaveBatch(String sgstin, String returnPeriod,
			String section, String groupCode, String returnType, int batchSize,
			String operationType, Long retryCount, Long userRequestId) {

		Long gstnBatchId = (long) 0;
		if (!(sgstin == null && returnPeriod == null && groupCode == null)) {
			// try {
			/*
			 * int derReturnPeriod = GenUtil
			 * .convertTaxPeriodToInt(returnPeriod);
			 */
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			Gstr1SaveBatchEntity batch = new Gstr1SaveBatchEntity();

			batch.setReturnType(
					returnType != null ? returnType.toUpperCase() : null);
			batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
			batch.setCreatedOn(now);
			batch.setBatchDate(now);
			batch.setGroupCode(groupCode);
			batch.setSgstin(sgstin);
			batch.setReturnPeriod(returnPeriod);
			// batch.setDerivedTaxperiod(derReturnPeriod);
			batch.setSection(section != null ? section.toUpperCase() : null);
			batch.setBatchSize(batchSize);
			batch.setOperationType(
					operationType != null ? operationType.toUpperCase() : null);
			batch.setRetryCount(retryCount != null ? retryCount : 0l);
			batch.setUserRequestId(userRequestId != null ? userRequestId : 0l);
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			batch = batchSaveStatusRepo.save(batch);
			LOGGER.debug("Batch is created successfully.");
			gstnBatchId = batch.getId();

			/*
			 * } catch (Exception ex) { throw new
			 * AppException("Error in Saving Batch", ex); }
			 */
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
		return gstnBatchId;

	}

	// --------------------GSTR2X----------------------//
	public void updateGstr2xProcBatch(Long gstnBatchId, String groupCode,
			List<Long> ids, String section) {
		if (ids != null && gstnBatchId != null) {
			TenantContext.setTenantId(groupCode);
			List<Long> chunk = new ArrayList<>();
			int counter = 1;
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			for (Long id : ids) {
				chunk.add(id);
				if (counter % 500 == 0 || counter == ids.size()) {
					if (APIConstants.TDS.equalsIgnoreCase(section)
							|| APIConstants.TDSA.equalsIgnoreCase(section)
							|| APIConstants.TCS.equalsIgnoreCase(section)
							|| APIConstants.TCSA.equalsIgnoreCase(section)) {
						gstr2XProcessedTcsTdsRepository
								.updateBatchId(gstnBatchId, chunk, now);
						chunk.clear();
					} else {
						LOGGER.debug("Invalid BatchSection Data for GSTR2X");
					}
				}
				counter++;
			}
		} else {
			LOGGER.debug("Invalid data to DB relationship");
		}
	}
	
	//----------------------GSTR1A---------------------------------------
	
	public String updateGstr1ARefIdAndTxnId(String groupCode, Long gstnBatchId,
			APIResponse resp) {
		String refId = null;
		// try {
		if (groupCode != null && gstnBatchId != 0 && resp != null) {
			if (resp.isSuccess()) {

				String saveJsonResp = resp.getResponse();
				String txnId = resp.getTxnId();
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = (JsonObject) jsonParser
						.parse(saveJsonResp);
				if (jsonObject.get(APIIdentifiers.REF_ID) != null) {
					refId = jsonObject.get(APIIdentifiers.REF_ID).getAsString();
				} else if (jsonObject
						.get(APIIdentifiers.REFERENECE_ID) != null) {
					refId = jsonObject.get(APIIdentifiers.REFERENECE_ID)
							.getAsString();
				}

				if (refId != null && refId.length() > 0) {
					LocalDateTime now = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					TenantContext.setTenantId(groupCode);
					docRepositoryGstr1A.updateGstnSaveRefId(gstnBatchId, refId,
							LocalDateTime.now());
					batchSaveStatusRepo.updateBatchRefID(refId, gstnBatchId,
							txnId, now);
				}

			} else {
				String errorCode = resp.getError().getErrorCode();
				String errorDesc = resp.getError().getErrorDesc();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Error thrown by Gstn ErrorCode: {} and "
							+ "ErrorDesc: {}", errorCode, errorDesc);
				}
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				TenantContext.setTenantId(groupCode);
				batchSaveStatusRepo.updateErrorMesg(gstnBatchId, errorCode,
						errorDesc, now);
			}

		}
		return refId;
		/*
		 * } catch (Exception e) { throw new AppException(
		 * "Error in updating RefId to BatchSaveStatusRepository", e); }
		 */
	}
}
