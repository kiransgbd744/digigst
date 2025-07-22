/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.beust.jcommander.Strings;
import com.ey.advisory.app.data.entities.client.EwbEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.data.services.einvoice.EinvoiceAsyncService;
import com.ey.advisory.app.data.services.einvoice.GenerateEWBByIrnService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.EwbProcessingStatus;
import com.ey.advisory.common.EwbStatusNew;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.einv.common.EinvConstant;
import com.ey.advisory.einv.dto.GenerateEWBByIrnNICReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.client.domain.EwbLifecycleEntity;
import com.ey.advisory.ewb.client.repositories.EwbLifecycleRepository;
import com.ey.advisory.ewb.common.EwbConstants;
import com.ey.advisory.ewb.common.EwbSynchCountConstant;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbRequestListDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.ey.advisory.ewb.dto.GetEWBReqDto;
import com.ey.advisory.ewb.dto.GetPartBDetailsDto;
import com.ey.advisory.ewb.dto.RejectEwbReqDto;
import com.ey.advisory.ewb.dto.RejectEwbResponseDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterRespDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("EwbServiceUiImpl")
public class EwbServiceUiImpl implements EwbServiceUi {

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("EwbServiceImpl")
	private EwbService ewbService;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	@Autowired
	@Qualifier("EwbLifecycleRepository")
	private EwbLifecycleRepository ewbLcRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docrepo;

	@Autowired
	private AsyncJobsService persistenceMngr;

	@Autowired
	@Qualifier("EinvoiceAsyncServiceImpl")
	private EinvoiceAsyncService einvoiceService;

	@Autowired
	@Qualifier("GenerateEWBByIrnServiceImpl")
	private GenerateEWBByIrnService generateEWBByIrn;
	
	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Override
	public String updatePartBEwb(List<UpdatePartBEwbRequestDto> reqList) {
		try {
			List<Long> docIds = reqList.stream()
					.map(UpdatePartBEwbRequestDto::getDocHeaderId)
					.collect(Collectors.toList());
			docrepo.updateEwbStatusByDocIdList(docIds,
					EwbProcessingStatus.INPROGRESS_UPDATE_PARTB
							.getEwbProcessingStatusCode());

			if (EwbSynchCountConstant.UPDATEPARTB < reqList.size()) {
				reqList.forEach(o -> {
					JsonObject jsonParams = new JsonObject();
					Gson gson = new Gson();
					jsonParams.add("jsonString", gson.toJsonTree(o));
					jsonParams.addProperty("updateDb", true);
					jsonParams.addProperty("erpPush", false);
					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.EWB_UPDATE_PARTB,
							jsonParams.toString(), "SYSTEM", 1L, null, null);
				});
				return EwbConstants.ASYNC_SUCCESS_RESPONSE;
			}
			reqList.forEach(o -> {
				try {
					ewbService.updateEwbPartB(o, o.getGstin(), true, false);
				} catch (Exception e) {
					LOGGER.error("Error in sync updatepartB processing", e);
					docrepo.updateProcessingStatusByDocId(o.getDocHeaderId(),
							EwbProcessingStatus.ERROR_UPDATE_PARTB
									.getEwbProcessingStatusCode());
				}
			});
			return EwbConstants.SYNC_SUCCESS_RESPONSE;
		} catch (Exception e) {
			String msg = "Exception Occured while submitting the job to "
					+ "Update PartB";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	@Override
	public String updateTransporter(List<UpdateEWBTransporterReqDto> reqList) {
		try {
			List<Long> docIds = reqList.stream()
					.map(UpdateEWBTransporterReqDto::getDocHeaderId)
					.collect(Collectors.toList());

			docrepo.updateEwbStatusByDocIdList(docIds,
					EwbProcessingStatus.INPROGRESS_UPDATE_TRANSPORTER
							.getEwbProcessingStatusCode());
			if (EwbSynchCountConstant.UPDATETRANSPORTER < reqList.size()) {
				reqList.forEach(o -> {
					JsonObject jsonParams = new JsonObject();
					Gson gson = new Gson();
					jsonParams.add("jsonString", gson.toJsonTree(o));
					jsonParams.addProperty("updateDb", true);
					jsonParams.addProperty("erpPush", false);
					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.EWB_UPDATE_TRANSPORTER,
							jsonParams.toString(), "SYSTEM", 1L, null, null);
				});
				return EwbConstants.ASYNC_SUCCESS_RESPONSE;
			}
			reqList.forEach(o -> {
				try {
					ewbService.updateTransporter(o, o.getGstin(), true, false);
				} catch (Exception e) {
					LOGGER.error("Error in sync updateTransporter processing",
							e);
					docrepo.updateProcessingStatusByDocId(o.getDocHeaderId(),
							EwbProcessingStatus.ERROR_UPDATE_TRANSPORTER
									.getEwbProcessingStatusCode());
				}
			});
			return EwbConstants.SYNC_SUCCESS_RESPONSE;
		} catch (Exception e) {
			String msg = "Exception Occured while submitting the job to "
					+ "Update Transporter";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	@Override
	public String extendEwb(List<ExtendEWBReqDto> reqList) {
		try {
			List<Long> docIds = reqList.stream()
					.map(ExtendEWBReqDto::getDocHeaderId)
					.collect(Collectors.toList());
			docrepo.updateEwbStatusByDocIdList(docIds,
					EwbProcessingStatus.INPROGRESS_EXTEND
							.getEwbProcessingStatusCode());
			if (EwbSynchCountConstant.EXTENDEWB < reqList.size()) {
				reqList.forEach(o -> {
					JsonObject jsonParams = new JsonObject();
					Gson gson = new Gson();
					jsonParams.add("jsonString", gson.toJsonTree(o));
					jsonParams.addProperty("updateDb", true);
					jsonParams.addProperty("erpPush", false);
					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.EWB_EXTEND, jsonParams.toString(),
							"SYSTEM", 1L, null, null);
				});
				return EwbConstants.ASYNC_SUCCESS_RESPONSE;
			}
			reqList.forEach(o -> {
				try {
					ewbService.extendEWB(o, o.getGstin(), true, false);
				} catch (Exception e) {
					LOGGER.error("Error in sync extendEWB processing", e);
					docrepo.updateProcessingStatusByDocId(o.getDocHeaderId(),
							EwbProcessingStatus.ERROR_EXTEND
									.getEwbProcessingStatusCode());
				}
			});
			return EwbConstants.SYNC_SUCCESS_RESPONSE;
		} catch (Exception e) {
			String msg = "Exception Occured while submitting the job to "
					+ "EXTEND EWB";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	@Override
	public String consolidateEwb(List<ConsolidateEWBReqDto> reqList) {
		try {

			if (EwbSynchCountConstant.CONSOLIDATEEWB < reqList.size()) {
				reqList.forEach(o -> {
					JsonObject jsonParams = new JsonObject();
					Gson gson = new Gson();
					jsonParams.add("jsonString", gson.toJsonTree(o));
					jsonParams.addProperty("updateDb", true);
					jsonParams.addProperty("erpPush", false);
					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.EWB_CONSOLIDATE, jsonParams.toString(),
							"SYSTEM", 1L, null, null);
				});
				return EwbConstants.ASYNC_SUCCESS_RESPONSE;
			}
			reqList.forEach(o -> {
				try {
					ewbService.consolidateEWB(o, o.getGstin(), true, false);
				} catch (Exception e) {
					LOGGER.error("Error in sync consolidateEWB processing", e);
				}
			});
			return EwbConstants.SYNC_SUCCESS_RESPONSE;
		} catch (Exception e) {
			String msg = "Exception Occured while submitting the job to "
					+ "Update PartB";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	@Override
	public String getEwb(List<GetEWBReqDto> req) {
		return null;
	}

	@Override
	public List<GetPartBDetailsDto> getPartBDetailsByEwbNo(
			List<String> ewbNos) {

		List<EwbLifecycleEntity> updatePartbResponseList = ewbLcRepository
				.findLatestPartBDetailsByewbNum(ewbNos);
		List<EwbLifecycleEntity> updateTransporterResponseList = ewbLcRepository
				.findLatestTransporterDetailsByewbNum(ewbNos);
		List<EwbEntity> ewbEntityList = ewbRepository.findEwbByEwbNums(ewbNos);
		List<EwbLifecycleEntity> masterTransporterList = ewbEntityList.stream()
				.map(o -> convertToLifecycle(o)).collect(Collectors.toList());

		Map<String, EwbLifecycleEntity> partBMap = updatePartbResponseList
				.stream().collect(Collectors.toMap(
						EwbLifecycleEntity::getEwbNum, Function.identity()));
		Map<String, EwbLifecycleEntity> transporterDetailsMap = updateTransporterResponseList
				.stream().collect(Collectors.toMap(
						EwbLifecycleEntity::getEwbNum, Function.identity()));
		Map<String, EwbLifecycleEntity> mastertransporterDetailsMap = masterTransporterList
				.stream().collect(Collectors.toMap(
						EwbLifecycleEntity::getEwbNum, Function.identity()));

		return ewbNos.stream()
				.map(o -> convertToUpdatePartBreq(o, partBMap.get(o),
						transporterDetailsMap.get(o),
						mastertransporterDetailsMap.get(o)))
				.collect(Collectors.toList());
	}

	private EwbLifecycleEntity convertToLifecycle(EwbEntity o) {
		EwbLifecycleEntity ewbLc = new EwbLifecycleEntity();
		ewbLc.setTransporterId(o.getTransporterId());
		ewbLc.setEwbNum(o.getEwbNum());
		ewbLc.setTransDocDate(o.getTransDocDate());
		ewbLc.setTransDocNo(o.getTransDocNum());
		ewbLc.setTransMode(o.getTransMode());
		ewbLc.setTransitType(o.getTransitType());
		return ewbLc;
	}

	private GetPartBDetailsDto convertToUpdatePartBreq(String ewbNo,
			EwbLifecycleEntity partb, EwbLifecycleEntity transporter,
			EwbLifecycleEntity masterTransporter) {

		GetPartBDetailsDto dto = new GetPartBDetailsDto();
		dto.setEwbNo(ewbNo);
		dto.setTransMode(EyEwbCommonUtil
				.getTransModeDesc(partb != null && partb.getTransMode() != null
						? partb.getTransMode()
						: masterTransporter.getTransMode()));
		dto.setTransDocNo(partb != null && partb.getTransDocNo() != null
				? partb.getTransDocNo() : masterTransporter.getTransDocNo());
		dto.setTransDocDate(partb != null && partb.getTransDocDate() != null
				? partb.getTransDocDate()
				: masterTransporter.getTransDocDate());
		dto.setVehicleNo(partb != null && partb.getVehicleNum() != null
				? partb.getVehicleNum() : masterTransporter.getVehicleNum());
		dto.setVehicleType(partb != null && partb.getVehicleType() != null
				? partb.getVehicleType() : masterTransporter.getVehicleType());
		dto.setReasonCode(partb != null && partb.getReasonCode() != null
				? partb.getReasonCode() : masterTransporter.getVehicleNum());
		dto.setFromPlace(partb != null && partb.getFromPlace() != null
				? partb.getFromPlace() : masterTransporter.getFromPlace());
		dto.setFromState(partb != null && partb.getFromState() != null
				? partb.getFromState() : masterTransporter.getFromState());
		dto.setTransporterId(
				transporter != null && transporter.getTransporterId() != null
						? transporter.getTransporterId()
						: masterTransporter.getTransporterId());
		return dto;
	}

	@Override
	public String generateEwbSync(Long id) {
		docrepo.updateProcessingStatusByDocId(id,
				EwbProcessingStatus.INPROGRESS_GENERATION
						.getEwbProcessingStatusCode());

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("generateEwBill Method Begin ,fetching the"
						+ " doc header details from db for id " + id);
			}
			EwbResponseDto response;
			GenerateEWBByIrnResponseDto geneEWBByIrnresponse;
			try {
				Optional<OutwardTransDocument> transDocuments = docrepo
						.findById(id);
				boolean isEinv = isEInvoiceRequest(transDocuments);
				if (isEinv) {
					GenerateEWBByIrnNICReqDto req = einvoiceService
							.convertOutWardtoGenEWBIrn(transDocuments.get());
					geneEWBByIrnresponse = generateEWBByIrn
							.generateEwayIrnRequest(req, transDocuments.get());

					if (geneEWBByIrnresponse.getEwbNo() != null) {
						return EinvConstant.SYNC_SUCCESS_RESPONSE;
					} else {
						return geneEWBByIrnresponse.getErrorMessage();
					}
				} else {
					response = ewbService.generateEwb(transDocuments.get(),
							true, false);
					if (response.getEwayBillNo() != null) {
						return EwbConstants.SYNC_SUCCESS_RESPONSE;
					} else {
						return response.getErrorDesc();
					}
				}
			} catch (Exception e) {
				LOGGER.error("ERROR IN SYNC processGenerateEwb", e);
				docrepo.updateProcessingStatusByDocId(id,
						EwbProcessingStatus.GENERATION_ERROR
								.getEwbProcessingStatusCode());
				return e.getMessage();
			}
		} catch (Exception e) {
			throw new AppException("Exception while processing the request", e);
		}
	}

	@Override
	public String generateEwbAsync(List<Long> docIds) {
		try {
			docrepo.updateMasterEwbStatusByDocIdList(docIds,
					EwbStatusNew.PUSHED_TO_NIC.getEwbNewStatusCode());
			JsonObject jsonParams = new JsonObject();
			List<AsyncExecJob> geneEwbAsyncJobs = new ArrayList<>();
			docIds.forEach(o -> {
				try {
					jsonParams.addProperty("id", o);
					jsonParams.addProperty("updateDb", true);
					jsonParams.addProperty("erpPush", false);
					geneEwbAsyncJobs.add(asyncJobsService.createJob(
							TenantContext.getTenantId(),
							JobConstants.GENERATE_EWAYBILL,
							jsonParams.toString(), "SYSTEM", 1L, null, null));

				} catch (Exception e) {
					LOGGER.error("ERROR IN ASYNC processGenerateEwb", e);
					docrepo.updateProcessingStatusByDocId(o,
							EwbProcessingStatus.GENERATION_ERROR
									.getEwbProcessingStatusCode());

				}
			});

			if (!geneEwbAsyncJobs.isEmpty()) {
				persistenceMngr.createJobs(geneEwbAsyncJobs);
			}
			return EwbConstants.ASYNC_SUCCESS_RESPONSE;
		} catch (Exception e) {
			String msg = "Exception Occured while submitting the job to "
					+ "generate EWb";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

	}

	@Override
	public Pair<String, String> cancelEwbSync(CancelEwbReqDto cancelEwb) {
		docrepo.updateProcessingStatusByDocId(cancelEwb.getDocHeaderId(),
				EwbProcessingStatus.INPROGRESS_CANCELLATION
						.getEwbProcessingStatusCode());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Cancel EWB Method Begin ,fetching the"
					+ " doc header details from db for id "
					+ cancelEwb.getDocHeaderId());
		}
		CancelEwbResponseDto response;
		try {
			Long id = cancelEwb.getDocHeaderId();
			Optional<OutwardTransDocument> transDocuments = docrepo
					.findById(id);
			if (!transDocuments.isPresent()) {
				LOGGER.error("generateIrn  ,no invoice available");
				throw new AppException("No Invoice Found in the System");
			}
			OutwardTransDocument doc = transDocuments.get();

			LOGGER.debug("Cancel Ewb  ,convert the doc header detail "
					+ "into einvoice request dto");

			reqLogHelper.logAdditionalParams(doc.getSgstin(), doc.getDocType(),
					doc.getDocNo(), true, true);

			response = ewbService.cancelEwb(cancelEwb, true, false);

			if (response.getCancelDate() != null) {
				return new Pair<>(String.valueOf(cancelEwb.getDocHeaderId()),
						EwbConstants.SYNC_SUCCESS_RESPONSE);
			} else {
				return new Pair<>(String.valueOf(cancelEwb.getDocHeaderId()),
						EwbConstants.REQUEST_FAILED);
			}
		} catch (Exception e) {
			LOGGER.error("error in sync cancelewb processing", e);
			docrepo.updateProcessingStatusByDocId(cancelEwb.getDocHeaderId(),
					EwbProcessingStatus.ERROR_CANCELLATION
							.getEwbProcessingStatusCode());
			return new Pair<>(String.valueOf(cancelEwb.getDocHeaderId()),
					EwbConstants.REQUEST_FAILED);
		}
	}

	@Override
	public String cancelEwbAsync(CancelEwbRequestListDto cancelEwb) {
		try {
			List<CancelEwbReqDto> reqList = cancelEwb.getCancelEwbReqDtoList();
			List<Long> docIds = reqList.stream().map(o -> o.getDocHeaderId())
					.collect(Collectors.toList());
			docrepo.updateEwbStatusByDocIdList(docIds,
					EwbProcessingStatus.INPROGRESS_CANCELLATION
							.getEwbProcessingStatusCode());
			List<AsyncExecJob> cnlEwbAsyncJobs = new ArrayList<>();

			reqList.forEach(o -> {
				JsonObject jsonParams = new JsonObject();
				Gson gson = new Gson();
				jsonParams.add("jsonString", gson.toJsonTree(o));
				jsonParams.addProperty("updateDb", true);
				jsonParams.addProperty("erpPush", false);
				cnlEwbAsyncJobs.add(asyncJobsService.createJob(
						TenantContext.getTenantId(), JobConstants.EWB_CANCEL,
						jsonParams.toString(), "SYSTEM", 1L, null, null));
			});

			if (!cnlEwbAsyncJobs.isEmpty()) {
				persistenceMngr.createJobs(cnlEwbAsyncJobs);
			}
			return EwbConstants.ASYNC_SUCCESS_RESPONSE;
		} catch (Exception e) {
			String msg = "Exception Occured while submitting the job to "
					+ "CANCEL EWb";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	@Override
	public Pair<String, String> updatePartEwbSync(
			UpdatePartBEwbRequestDto updatePartEwb) {
	
		OutwardTransDocument doc = null;
		try {
			Long id = updatePartEwb.getDocHeaderId();
			Optional<OutwardTransDocument> transDocuments = docrepo
					.findById(id);
			if (!transDocuments.isPresent()) {
				LOGGER.error("updatePartEwbSync  ,no invoice available");
				throw new AppException("No Invoice Found in the System");
			}
			doc = transDocuments.get();

			LOGGER.debug("Update Part-B Ewb  ,convert the doc header detail "
					+ "into einvoice request dto");

			reqLogHelper.logAdditionalParams(doc.getSgstin(), doc.getDocType(),
					doc.getDocNo(), true, true);
			UpdatePartBEwbResponseDto respDto = ewbService.updateEwbPartB(
					updatePartEwb, updatePartEwb.getGstin(), true, false);
			if (respDto.getValidUpto() != null) {
				return new Pair<>(
						String.valueOf(updatePartEwb.getDocHeaderId()),
						EwbConstants.SYNC_SUCCESS_RESPONSE);
			} else {
				return new Pair<>(
						String.valueOf(updatePartEwb.getDocHeaderId()),
						EwbConstants.REQUEST_FAILED);
			}
		} catch (Exception e) {
			LOGGER.error("error in sync updatePartEwb processing", e);
			docrepo.updateProcessingStatusByDocId(
					updatePartEwb.getDocHeaderId(),
					EwbProcessingStatus.ERROR_CANCELLATION
							.getEwbProcessingStatusCode());
			return new Pair<>(String.valueOf(updatePartEwb.getDocHeaderId()),
					EwbConstants.FAILED);
		}
	}

	@Override
	public String updatePartEwbASync(List<UpdatePartBEwbRequestDto> reqList) {
		try {
			List<Long> docIds = reqList.stream()
					.map(UpdatePartBEwbRequestDto::getDocHeaderId)
					.collect(Collectors.toList());
			docrepo.updateEwbStatusByDocIdList(docIds,
					EwbProcessingStatus.INPROGRESS_UPDATE_PARTB
							.getEwbProcessingStatusCode());

			List<AsyncExecJob> updatePartBEwbAsyncJobs = new ArrayList<>();

			reqList.forEach(o -> {
				JsonObject jsonParams = new JsonObject();
				Gson gson = new Gson();
				jsonParams.add("jsonString", gson.toJsonTree(o));
				jsonParams.addProperty("updateDb", true);
				jsonParams.addProperty("erpPush", false);
				updatePartBEwbAsyncJobs.add(asyncJobsService.createJobAndReturn(
						TenantContext.getTenantId(),
						JobConstants.EWB_UPDATE_PARTB, jsonParams.toString(),
						"SYSTEM", 1L, null, null));
			});

			if (!updatePartBEwbAsyncJobs.isEmpty()) {
				persistenceMngr.createJobs(updatePartBEwbAsyncJobs);
			}

			return EwbConstants.ASYNC_SUCCESS_RESPONSE;
		} catch (Exception e) {
			String msg = "Exception Occured while submitting the job to "
					+ "Update PartB";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	@Override
	public Pair<String, String> updateTransporterSync(
			UpdateEWBTransporterReqDto updateTransporterReq) {

		UpdateEWBTransporterRespDto respDto = ewbService.updateTransporter(
				updateTransporterReq, updateTransporterReq.getGstin(), true,
				false);
		if (respDto.getTransUpdateDate() != null) {
			return new Pair<>(
					String.valueOf(updateTransporterReq.getDocHeaderId()),
					EwbConstants.SYNC_SUCCESS_RESPONSE);
		} else {
			return new Pair<>(
					String.valueOf(updateTransporterReq.getDocHeaderId()),
					EwbConstants.FAILED);
		}
	}

	@Override
	public String updateTransporterASync(
			List<UpdateEWBTransporterReqDto> reqList) {
		try {
			List<Long> docIds = reqList.stream()
					.map(UpdateEWBTransporterReqDto::getDocHeaderId)
					.collect(Collectors.toList());

			docrepo.updateEwbStatusByDocIdList(docIds,
					EwbProcessingStatus.INPROGRESS_UPDATE_TRANSPORTER
							.getEwbProcessingStatusCode());

			List<AsyncExecJob> updateTransportAsyncJobs = new ArrayList<>();

			reqList.forEach(o -> {
				JsonObject jsonParams = new JsonObject();
				Gson gson = new Gson();
				jsonParams.add("jsonString", gson.toJsonTree(o));
				jsonParams.addProperty("updateDb", true);
				jsonParams.addProperty("erpPush", false);
				updateTransportAsyncJobs.add(asyncJobsService
						.createJobAndReturn(TenantContext.getTenantId(),
								JobConstants.EWB_UPDATE_TRANSPORTER,
								jsonParams.toString(), "SYSTEM", 1L, null,
								null));
			});

			if (!updateTransportAsyncJobs.isEmpty()) {
				persistenceMngr.createJobs(updateTransportAsyncJobs);
			}

			return EwbConstants.ASYNC_SUCCESS_RESPONSE;

		} catch (Exception e) {
			String msg = "Exception Occured while submitting the job to "
					+ "Update PartB";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	@Override
	public Pair<String, String> extendEwbSync(ExtendEWBReqDto extendEwbReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Cancel EWB Method Begin ,fetching the"
					+ " doc header details from db for id "
					+ extendEwbReq.getDocHeaderId());
		}
		try {
			ewbService.extendEWB(extendEwbReq, extendEwbReq.getGstin(), true,
					false);
			return new Pair<>(String.valueOf(extendEwbReq.getDocHeaderId()),
					EwbConstants.SYNC_SUCCESS_RESPONSE);
		} catch (Exception e) {
			LOGGER.error("error in sync extendEwb processing", e);
			docrepo.updateProcessingStatusByDocId(extendEwbReq.getDocHeaderId(),
					EwbProcessingStatus.ERROR_CANCELLATION
							.getEwbProcessingStatusCode());
			return new Pair<>(String.valueOf(extendEwbReq.getDocHeaderId()),
					EwbConstants.FAILED);
		}
	}

	@Override
	public String extendEwbAsync(List<ExtendEWBReqDto> reqList) {
		try {
			List<Long> docIds = reqList.stream()
					.map(ExtendEWBReqDto::getDocHeaderId)
					.collect(Collectors.toList());
			docrepo.updateEwbStatusByDocIdList(docIds,
					EwbProcessingStatus.INPROGRESS_EXTEND
							.getEwbProcessingStatusCode());
			if (EwbSynchCountConstant.EXTENDEWB < reqList.size()) {
				reqList.forEach(o -> {
					JsonObject jsonParams = new JsonObject();
					Gson gson = new Gson();
					jsonParams.add("jsonString", gson.toJsonTree(o));
					jsonParams.addProperty("updateDb", true);
					jsonParams.addProperty("erpPush", false);
					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.EWB_EXTEND, jsonParams.toString(),
							"SYSTEM", 1L, null, null);
				});
				return EwbConstants.ASYNC_SUCCESS_RESPONSE;
			}
			return EwbConstants.ASYNC_SUCCESS_RESPONSE;
		} catch (Exception e) {
			String msg = "Exception Occured while submitting the job to "
					+ "Extend EWB";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	@Override
	public Pair<String, String> consolidateEwbsync(
			ConsolidateEWBReqDto consolidateEwbReq) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("consolidateEwbReq Method Begin ,fetching the"
					+ " doc header details from db for id "
					+ consolidateEwbReq.getDocHeaderId());
		}
		try {
			ewbService.consolidateEWB(consolidateEwbReq,
					consolidateEwbReq.getGstin(), true, false);
			return new Pair<>(
					String.valueOf(consolidateEwbReq.getDocHeaderId()),
					EwbConstants.SYNC_SUCCESS_RESPONSE);
		} catch (Exception e) {
			LOGGER.error("error in sync consolidateEwbReq processing", e);
			return new Pair<>(
					String.valueOf(consolidateEwbReq.getDocHeaderId()),
					EwbConstants.FAILED);
		}
	}

	@Override
	public String consolidateEwbAsync(List<ConsolidateEWBReqDto> reqList) {
		try {

			if (EwbSynchCountConstant.CONSOLIDATEEWB < reqList.size()) {
				reqList.forEach(o -> {
					JsonObject jsonParams = new JsonObject();
					Gson gson = new Gson();
					jsonParams.add("jsonString", gson.toJsonTree(o));
					jsonParams.addProperty("updateDb", true);
					jsonParams.addProperty("erpPush", false);
					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.EWB_CONSOLIDATE, jsonParams.toString(),
							"SYSTEM", 1L, null, null);
				});
				return EwbConstants.ASYNC_SUCCESS_RESPONSE;
			}
			return EwbConstants.ASYNC_SUCCESS_RESPONSE;
		} catch (Exception e) {
			String msg = "Exception Occured while submitting the job to "
					+ "Update PartB";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}
	
	@Override
	public String rejectEwbSync(Long id) {
		docrepo.updateProcessingStatusByDocId(id,
				EwbProcessingStatus.INPROGRESS_CANCELLATION
						.getEwbProcessingStatusCode());
		OutwardTransDocument doc = null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Reject EWB Method Begin ,fetching the"
					+ " doc header details from db for id " + id);
		}
		RejectEwbResponseDto response;
		try {
			Optional<OutwardTransDocument> transDocuments = docrepo
					.findById(id);
			if (!transDocuments.isPresent()) {
				LOGGER.error("Reject EWB  ,no invoice available");
				throw new AppException("No Invoice Found in the System");
			}
			doc = transDocuments.get();
			RejectEwbReqDto rejectEwbReq = new RejectEwbReqDto();
			rejectEwbReq.setGstin(doc.getSgstin());
			rejectEwbReq.setDocHeaderId(id);
			rejectEwbReq.setEwbNo(String.valueOf(doc.getEwbNoresp()));

			response = ewbService.rejectEwb(rejectEwbReq, true, false);
			if (response.getEwbRejectedDate() != null) {

				EwbEntity ewb = ewbRepository
						.findByEwbNum(String.valueOf(doc.getEwbNoresp()));
				
				saveEwbLifeCycle(ewb);
				
				ewbRepository.updateRejectStatus(
						EwbStatusNew.REJECTED.getEwbNewStatusCode(),
						response.getEwbRejectedDate(),
						String.valueOf(doc.getEwbNoresp()));

				return EwbConstants.SYNC_SUCCESS_RESPONSE;
			} else {
				return response.getErrorMessage();
			}
		} catch (Exception e) {
			LOGGER.error("error in sync cancelewb processing", e);
			docrepo.updateProcessingStatusByDocId(id,
					EwbProcessingStatus.ERROR_CANCELLATION
							.getEwbProcessingStatusCode());
			return e.getMessage();
		}
	}
	
	private void saveEwbLifeCycle(EwbEntity ewb) {

		EwbLifecycleEntity ewbLcEntity = new EwbLifecycleEntity();
			ewbLcEntity.setEwbNum(ewb.getEwbNum());
			ewbLcEntity.setVehicleNum(ewb.getVehicleNum());
			ewbLcEntity.setVehicleType(ewb.getVehicleType());
			ewbLcEntity.setTransMode(ewb.getTransMode());
			ewbLcEntity.setTransDocNo(ewb.getTransDocNum());
			ewbLcEntity.setTransDocDate(ewb.getTransDocDate());
			ewbLcEntity.setFromPlace(ewb.getFromPlace());
			ewbLcEntity.setFromState(ewb.getFromState());
			ewbLcEntity.setVehicleUpdateDate(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			ewbLcEntity.setValidUpto(ewb.getValidUpto());
			ewbLcEntity.setModifiedOn(ewb.getEwbDate());
			ewbLcEntity.setAspDistance(ewb.getAspDistance());
			ewbLcEntity.setActive(true);
			ewbLcEntity.setEwbOrigin(2);
			ewbLcEntity.setFunction(APIIdentifiers.UPDATE_VEHICLE_DETAILS);
			ewbLcRepository.save(ewbLcEntity);

	}


	private boolean isEInvoiceRequest(
			Optional<OutwardTransDocument> transDocuments) {
		return !Strings.isStringEmpty(transDocuments.get().getIrnResponse())
				|| (!Strings.isStringEmpty(transDocuments.get().getIrn())
						&& transDocuments.get().getIrn().length() == 64);
	}

}
