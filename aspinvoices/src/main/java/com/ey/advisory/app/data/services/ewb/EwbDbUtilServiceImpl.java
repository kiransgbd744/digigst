/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.EwbEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.CewbRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.EwbProcessingStatus;
import com.ey.advisory.common.EwbStatus;
import com.ey.advisory.common.EwbStatusNew;
import com.ey.advisory.common.IrnStatusMaster;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.client.domain.EwbConsolidateEntity;
import com.ey.advisory.ewb.client.domain.EwbLifecycleEntity;
import com.ey.advisory.ewb.client.repositories.EwbConsolidatedRepository;
import com.ey.advisory.ewb.client.repositories.EwbLifecycleRepository;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBResponseDto;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.ey.advisory.ewb.dto.ExtendEWBResponseDto;
import com.ey.advisory.ewb.dto.RejectEwbReqDto;
import com.ey.advisory.ewb.dto.RejectEwbResponseDto;
import com.ey.advisory.ewb.dto.TripSheetEwbBills;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterRespDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbResponseDto;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("EwbDbUtilServiceImpl")
@Slf4j
public class EwbDbUtilServiceImpl implements EwbDbUtilService {

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	@Autowired
	@Qualifier("EwbConsolidatedRepository")
	private EwbConsolidatedRepository ewbConsolidatedRepo;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docrepo;

	@Autowired
	@Qualifier("EwbLifecycleRepository")
	private EwbLifecycleRepository ewbLCeRepo;

	@Autowired
	CewbRepository cewbRepo;

	@Override
	public void generateEwbDbUpdate(OutwardTransDocument doc,
			EwayBillRequestDto nicReq, EwbResponseDto resp,
			String apiIdentifier) {

		EwbLifecycleEntity ewbLcEntity = new EwbLifecycleEntity();
		if (resp.getValidUpto() != null) {
			ewbLcEntity.setEwbNum(resp.getEwayBillNo());
			ewbLcEntity.setVehicleNum(nicReq.getVehicleNo());
			ewbLcEntity.setVehicleType(nicReq.getVehicleType());
			ewbLcEntity.setTransMode(nicReq.getTransMode());
			ewbLcEntity.setTransDocNo(nicReq.getTransDocNo());
			ewbLcEntity.setTransDocDate(nicReq.getTransDocDate());
			ewbLcEntity.setFromPlace(nicReq.getFromPlace());
			ewbLcEntity.setFromState(nicReq.getFromStateCode());
			ewbLcEntity.setVehicleUpdateDate(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			ewbLcEntity.setValidUpto(resp.getValidUpto());
			ewbLcEntity.setErrorCode(resp.getErrorCode());
			ewbLcEntity.setErrorDesc(resp.getErrorDesc());
			ewbLcEntity.setModifiedOn(resp.getEwayBillDate());
			ewbLcEntity.setAspDistance(resp.getNicDistance());
			ewbLcEntity.setActive(true);
			ewbLcEntity.setFunctionStatus(
					!Strings.isNullOrEmpty(resp.getErrorCode()));
			ewbLcEntity.setFunction(APIIdentifiers.GENERATE_EWB);
			ewbLCeRepo.save(ewbLcEntity);
		}
		if (!Strings.isNullOrEmpty(resp.getEwayBillNo())) {
			docrepo.updateEwayBillNo(doc.getId(),
					Long.valueOf(resp.getEwayBillNo()), resp.getEwayBillDate(),
					resp.getValidUpto() == null
							? EwbStatus.PARTA_GENERATED.getEwbStatusCode()
							: EwbStatus.EWB_ACTIVE.getEwbStatusCode(),
					resp.getValidUpto() == null
							? EwbProcessingStatus.PART_A_GENERATED
									.getEwbProcessingStatusCode()
							: EwbProcessingStatus.UPDATED_PARTB
							.getEwbProcessingStatusCode(),
					null, null,
					AspInvoiceStatus.ASP_PROCESSED.getAspInvoiceStatusCode());

			Optional<EwbEntity> isEwbAva = ewbRepository
					.findEwbDtls(resp.getEwayBillNo());

			if (!isEwbAva.isPresent()) {
				saveEwbEntity(resp, doc.getId(), nicReq, doc,
						ewbLcEntity.getId());
			}
		} else {

			boolean isDupEwb = false;
			if (resp.getErrorCode() != null
					&& (resp.getErrorCode().contains("604")
							|| resp.getErrorCode().contains("312"))) {
				LOGGER.debug(
						"Ewb is already generated or Ewb is already cancelled");
				isDupEwb = true;
			}
			if (!apiIdentifier.equalsIgnoreCase(
					com.ey.advisory.einv.app.api.APIIdentifiers.GENERATE_EWBByIRN)) {
				LOGGER.error(
						"EwbDBUtilService ewb failure with error code : {}",
						resp.getErrorCode());
				docrepo.updateErrorEwbResponseById(doc.getId(),
						resp.getErrorCode(), resp.getErrorDesc(),
						EwbStatusNew.PENDING_ERROR.getEwbNewStatusCode(),
						isDupEwb ? EwbProcessingStatus.ERROR_CANCELLATION
								.getEwbProcessingStatusCode()
								: EwbProcessingStatus.GENERATION_ERROR
										.getEwbProcessingStatusCode(),
						AspInvoiceStatus.ASP_ERROR.getAspInvoiceStatusCode());
			} else {

				LOGGER.error(
						"EwbDBUtilService with GenerateEwbIrn, ewb failure "
								+ "with error code : {}",
						resp.getErrorCode());

				docrepo.updateErrorEwbResponseById(doc.getId(),
						resp.getErrorCode(), resp.getErrorDesc(),
						EwbStatusNew.PENDING_ERROR.getEwbNewStatusCode(),
						isDupEwb ? EwbProcessingStatus.ERROR_CANCELLATION
								.getEwbProcessingStatusCode()
								: EwbProcessingStatus.GENERATION_ERROR
										.getEwbProcessingStatusCode(),
						AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
			}
		}
	}

	private void saveEwbEntity(EwbResponseDto resp, Long id,
			EwayBillRequestDto nicReq, OutwardTransDocument doc,
			Long lifeCycleId) {
		EwbEntity ewbEntity = new EwbEntity();
		ewbEntity.setAlert(resp.getAlert());
		ewbEntity.setCreatedBy("SYSTEM");
		ewbEntity.setCreatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		ewbEntity.setDocHeaderId(id);
		ewbEntity.setEwbDate(resp.getEwayBillDate());
		ewbEntity.setEwbNum(resp.getEwayBillNo());
		ewbEntity.setModifiedBy("SYSTEM");
		ewbEntity.setModifiedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		ewbEntity.setValidUpto(resp.getValidUpto());
		ewbEntity.setFromPlace(nicReq.getFromPlace());
		ewbEntity.setFromPincode(nicReq.getFromPincode());
		ewbEntity.setFromState(nicReq.getFromStateCode());
		ewbEntity.setLifeCycleId(lifeCycleId);
		if (!Strings.isNullOrEmpty(resp.getAlert())
				&& resp.getNicDistance() != null) {
			ewbEntity.setRemDistance(resp.getNicDistance());
		} else {
			ewbEntity.setRemDistance(nicReq.getTransDistance());
		}
		ewbEntity.setAspDistance(
				nicReq.getAspDistance() == null ? 0 : nicReq.getAspDistance());
		ewbEntity.setStatus(resp.getValidUpto() != null
				? EwbStatus.EWB_ACTIVE.getEwbStatusCode()
				: EwbStatus.PARTA_GENERATED.getEwbStatusCode());
		ewbEntity.setTransDocDate(nicReq.getTransDocDate());
		ewbEntity.setTransDocNum(nicReq.getTransDocNo());
		ewbEntity.setTransMode(nicReq.getTransMode());
		ewbEntity.setTransporterId(nicReq.getTransporterId());
		ewbEntity.setVehicleType(nicReq.getVehicleType());
		ewbEntity.setVehicleNum(nicReq.getVehicleNo());
		ewbEntity.setEwbOrigin(1);
		ewbEntity.setTransType(doc.getTransactionType());
		ewbRepository.save(ewbEntity);

	}

	@Override
	public void cancelEwbDbUpdate(CancelEwbReqDto req,
			CancelEwbResponseDto resp) {
		EwbLifecycleEntity ewbLcEntity = new EwbLifecycleEntity();
		ewbLcEntity.setEwbNum(req.getEwbNo());
		ewbLcEntity.setReasonCode(req.getCancelRsnCode());
		ewbLcEntity.setReasonRemark(req.getCancelRmrk());
		ewbLcEntity.setCancelDate(resp.getCancelDate());
		ewbLcEntity.setErrorCode(resp.getErrorCode());
		ewbLcEntity.setErrorDesc(resp.getErrorMessage());
		ewbLcEntity.setModifiedOn(resp.getCancelDate());
		ewbLcEntity.setActive(true);
		ewbLcEntity
				.setFunctionStatus(!Strings.isNullOrEmpty(resp.getErrorCode()));
		ewbLcEntity.setFunction(APIIdentifiers.CANCEL_EWB);
		ewbLCeRepo.save(ewbLcEntity);
		if (Strings.isNullOrEmpty(resp.getErrorCode())) {

			ewbRepository.updateEwbCancelStatusByEwbNo(resp.getEwayBillNo(),
					true, resp.getCancelDate(),
					EwbStatusNew.CANCELLED.getEwbNewStatusCode(),
					ewbLcEntity.getId());

			Optional<OutwardTransDocument> doc = docrepo
					.findById(req.getDocHeaderId());
			String supplyType = doc.get().getSupplyType();

			if (!"CAN".equalsIgnoreCase(supplyType)) {

				boolean isDelete = false;
				int irnStatus = docrepo.findByIrnStatus(req.getDocHeaderId());
				if (IrnStatusMaster.NOT_APPLICABLE
						.getIrnStatusMaster() == irnStatus
						|| IrnStatusMaster.NOT_OPTED
								.getIrnStatusMaster() == irnStatus) {
					isDelete = true;
				}

				docrepo.updateEwbStatusByDocId(req.getDocHeaderId(),
						EwbStatusNew.CANCELLED.getEwbNewStatusCode(),
						EwbProcessingStatus.CANCELLED
								.getEwbProcessingStatusCode(),
						isDelete);
			} else {
				docrepo.updateEwbStatusByDocId(req.getDocHeaderId(),
						EwbStatusNew.CANCELLED.getEwbNewStatusCode(),
						EwbProcessingStatus.CANCELLED
								.getEwbProcessingStatusCode());
			}
		} else {
			ewbRepository.updateLifeCycleIdByEwbNumber(req.getEwbNo(),
					ewbLcEntity.getId());

			docrepo.updateCancelErrorEwbResponseById(req.getDocHeaderId(),
					resp.getErrorCode(), resp.getErrorMessage(),
					AspInvoiceStatus.ASP_ERROR.getAspInvoiceStatusCode(),
					EwbProcessingStatus.ERROR_CANCELLATION
							.getEwbProcessingStatusCode());
		}
	}

	@Override
	public void updatePartBDbUpdate(UpdatePartBEwbRequestDto req,
			UpdatePartBEwbResponseDto resp) {
		try {
			EwbLifecycleEntity ewbLcEntity = new EwbLifecycleEntity();
			ewbLcEntity.setEwbNum(req.getEwbNo());
			ewbLcEntity.setVehicleNum(req.getVehicleNo());
			ewbLcEntity.setVehicleType(req.getVehicleType());
			ewbLcEntity.setTransMode(req.getTransMode());
			ewbLcEntity.setTransDocNo(req.getTransDocNo());
			ewbLcEntity.setTransDocDate(req.getTransDocDate());
			ewbLcEntity.setReasonCode(req.getReasonCode());
			ewbLcEntity.setReasonRemark(req.getReasonRem());
			ewbLcEntity.setFromPlace(req.getFromPlace());
			ewbLcEntity.setFromState(req.getFromState());
			ewbLcEntity.setVehicleUpdateDate(resp.getVehUpdDate());
			ewbLcEntity.setValidUpto(resp.getValidUpto());
			ewbLcEntity.setErrorCode(resp.getErrorCode());
			ewbLcEntity.setErrorDesc(resp.getErrorMessage());
			ewbLcEntity.setModifiedOn(resp.getVehUpdDate());
			ewbLcEntity.setActive(true);
			ewbLcEntity.setFunctionStatus(
					!Strings.isNullOrEmpty(resp.getErrorCode()));
			ewbLcEntity.setFunction(APIIdentifiers.UPDATE_VEHICLE_DETAILS);

			ewbLCeRepo.save(ewbLcEntity);
			EwbEntity ewbEntity = ewbRepository.findByEwbNum(req.getEwbNo());
			if (Strings.isNullOrEmpty(resp.getErrorCode())) {
				ewbEntity.setValidUpto(resp.getValidUpto());
				ewbEntity.setModifiedOn(resp.getVehUpdDate());
				ewbEntity.setVehicleNum(req.getVehicleNo());
				ewbEntity.setVehicleType(req.getVehicleType());
				ewbEntity.setTransMode(req.getTransMode());
				ewbEntity.setTransDocNum(req.getTransDocNo());
				ewbEntity.setTransDocDate(req.getTransDocDate());
				ewbEntity.setReasonCode(req.getReasonCode());
				ewbEntity.setReasonRemark(req.getReasonRem());
				ewbEntity.setFromPlace(req.getFromPlace());
				ewbEntity.setFromState(req.getFromState());
				ewbEntity.setStatus(EwbStatus.EWB_ACTIVE.getEwbStatusCode());
				ewbEntity.setLifeCycleId(ewbLcEntity.getId());
				ewbRepository.save(ewbEntity);
				docrepo.updateEwbStatusByDocId(req.getDocHeaderId(),
						EwbStatus.EWB_ACTIVE.getEwbStatusCode(),
						EwbProcessingStatus.UPDATED_PARTB
								.getEwbProcessingStatusCode(),
						false);
			} else {
				ewbRepository.updateLifeCycleIdByEwbNumber(req.getEwbNo(),
						ewbLcEntity.getId());
				docrepo.updateProcessingStatusByDocId(req.getDocHeaderId(),
						EwbProcessingStatus.ERROR_UPDATE_PARTB
								.getEwbProcessingStatusCode());
			}
		} catch (Exception e) {
			LOGGER.error("ERROR IN SAVING EWBLIFECYCLE", e);
		}
	}

	@Override
	public void updateTransporterDbUpdate(UpdateEWBTransporterReqDto req,
			UpdateEWBTransporterRespDto resp) {
		EwbLifecycleEntity ewbLcEntity = new EwbLifecycleEntity();
		ewbLcEntity.setEwbNum(req.getEwbNo());
		ewbLcEntity.setTransporterId(req.getTransporterId());
		ewbLcEntity.setErrorCode(resp.getErrorCode());
		ewbLcEntity.setErrorDesc(resp.getErrorMessage());
		ewbLcEntity.setModifiedOn(resp.getTransUpdateDate());
		ewbLcEntity.setActive(true);
		ewbLcEntity
				.setFunctionStatus(!Strings.isNullOrEmpty(resp.getErrorCode()));
		ewbLcEntity.setFunction(APIIdentifiers.UPDATE_TRANSPORTER);
		ewbLCeRepo.save(ewbLcEntity);
		EwbEntity ewbEntity = ewbRepository.findByEwbNum(req.getEwbNo());
		if (Strings.isNullOrEmpty(resp.getErrorCode())) {
			ewbEntity.setTransporterId(resp.getTransporterId());
			ewbEntity.setModifiedOn(resp.getTransUpdateDate());
			ewbEntity.setLifeCycleId(ewbLcEntity.getId());
			ewbRepository.save(ewbEntity);
		} else {
			ewbRepository.updateLifeCycleIdByEwbNumber(req.getEwbNo(),
					ewbLcEntity.getId());
		}
		docrepo.updateProcessingStatusByDocId(req.getDocHeaderId(),
				Strings.isNullOrEmpty(resp.getErrorCode())
						? EwbProcessingStatus.UPDATED_TRANSPORTER
								.getEwbProcessingStatusCode()
						: EwbProcessingStatus.ERROR_UPDATE_TRANSPORTER
								.getEwbProcessingStatusCode());

	}

	@Override
	public void extendDbUpdate(ExtendEWBReqDto req, ExtendEWBResponseDto resp) {
		EwbLifecycleEntity ewbLcEntity = new EwbLifecycleEntity();
		ewbLcEntity.setEwbNum(req.getEwbNo());
		ewbLcEntity.setAddressLine1(req.getAddressLine1());
		ewbLcEntity.setAddressLine2(req.getAddressLine2());
		ewbLcEntity.setAddressLine3(req.getAddressLine3());
		ewbLcEntity.setVehicleNum(req.getVehicleNo());
		ewbLcEntity.setFromPlace(req.getFromPlace());
		ewbLcEntity.setFromState(req.getFromState());
		ewbLcEntity.setRemDistance(req.getRemainingDistance());
		ewbLcEntity.setTransDocDate(req.getTransDocDate());
		ewbLcEntity.setTransDocNo(req.getTransDocNo());
		ewbLcEntity.setTransitType(req.getTransitType());
		ewbLcEntity.setTransMode(req.getTransMode());
		ewbLcEntity.setValidUpto(resp.getValidUpto());
		ewbLcEntity.setFromPincode(req.getFromPincode());
		ewbLcEntity.setReasonCode(req.getExtnRsnCode());
		ewbLcEntity.setReasonRemark(req.getExtnRemarks());
		ewbLcEntity.setConsignmentStatus(req.getConsignmentStatus());
		ewbLcEntity.setModifiedOn(resp.getUpdatedDate());
		ewbLcEntity.setErrorCode(resp.getErrorCode());
		ewbLcEntity.setErrorDesc(resp.getErrorDesc());
		ewbLcEntity.setActive(true);
		ewbLcEntity
				.setFunctionStatus(!Strings.isNullOrEmpty(resp.getErrorCode()));
		ewbLcEntity.setFunction(APIIdentifiers.EXTEND_VEHICLE_DETAILS);
		ewbLCeRepo.save(ewbLcEntity);
		EwbEntity ewbEntity = ewbRepository.findByEwbNum(req.getEwbNo());
		if (Strings.isNullOrEmpty(resp.getErrorCode())) {
			ewbEntity.setModifiedOn(resp.getUpdatedDate());
			ewbEntity.setAddLine1(req.getAddressLine1());
			ewbEntity.setAddLine2(req.getAddressLine2());
			ewbEntity.setAddLine3(req.getAddressLine3());
			ewbEntity.setVehicleNum(req.getVehicleNo());
			ewbEntity.setFromPlace(req.getFromPlace());
			ewbEntity.setFromState(req.getFromState());
			ewbEntity.setRemDistance(req.getRemainingDistance());
			ewbEntity.setTransDocDate(req.getTransDocDate());
			ewbEntity.setTransDocNum(req.getTransDocNo());
			ewbEntity.setTransitType(req.getTransitType());
			ewbEntity.setTransMode(req.getTransMode());
			ewbEntity.setValidUpto(resp.getValidUpto());
			ewbEntity.setFromPincode(req.getFromPincode() != null
					? (Integer.parseInt((req.getFromPincode()))) : null);
			ewbEntity.setReasonCode(req.getExtnRsnCode());
			ewbEntity.setReasonRemark(req.getExtnRemarks());
			ewbEntity.setConsignmentStatus(req.getConsignmentStatus());
			ewbEntity.setLifeCycleId(ewbLcEntity.getId());
			ewbRepository.save(ewbEntity);
		} else {
			ewbRepository.updateLifeCycleIdByEwbNumber(req.getEwbNo(),
					ewbLcEntity.getId());
		}
		docrepo.updateProcessingStatusByDocId(req.getDocHeaderId(),
				Strings.isNullOrEmpty(resp.getErrorCode())
						? EwbProcessingStatus.EXTENDED
								.getEwbProcessingStatusCode()
						: EwbProcessingStatus.ERROR_EXTEND
								.getEwbProcessingStatusCode());

	}

	@Override
	public void consolidateEwbDbUpdate(ConsolidateEWBReqDto req,
			ConsolidateEWBResponseDto resp) {

		List<TripSheetEwbBills> ewbTripSheetList = req.getTripSheetEwbBills();
		List<String> ewbList = ewbTripSheetList.stream().map(o -> o.getEwbNo())
				.collect(Collectors.toList());

		/*
		 * ewbRepository.updateConsildatedEwb(ewbList, resp.getCEwbNo(),
		 * resp.getCEWBDate());
		 */

		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";

		if (Strings.isNullOrEmpty(resp.getErrorCode())
				|| Strings.isNullOrEmpty(resp.getErrorDesc())) {

			List<EwbConsolidateEntity> ewbConsolidateEntityList = new ArrayList<>();

			for (int i = 0; i < ewbList.size(); i++) {

				EwbConsolidateEntity ewbConsolidateEntity = new EwbConsolidateEntity(
						resp.getCEwbNo(), resp.getCEWBDate(), false,
						req.getFromPlace(), req.getFromState(),
						req.getVehicleNo(), req.getTransMode(),
						req.getTransDocNo(), req.getTransDocDate(), userName,
						LocalDateTime.now());

				ewbConsolidateEntity.setEwbNum(ewbList.get(i));

				ewbConsolidateEntityList.add(ewbConsolidateEntity);

				cewbRepo.updateConsolidatedEWB(req.getSerialNo(),
						ewbList.get(i), req.getFileId(), resp.getCEwbNo(),
						resp.getCEWBDate());

			}

			ewbConsolidatedRepo.saveAll(ewbConsolidateEntityList);

			EwbEntity ewbEntity = new EwbEntity();
			ewbEntity.setEwbNum(resp.getCEwbNo());
			ewbEntity.setEwbDate(resp.getCEWBDate());
			ewbEntity.setFromPlace(req.getFromPlace());
			ewbEntity.setFromState(req.getFromState());
			ewbEntity.setVehicleNum(req.getVehicleNo());
			ewbEntity.setTransMode(req.getTransMode());
			ewbEntity.setTransDocNum(req.getTransDocNo());
			ewbEntity.setTransDocDate(req.getTransDocDate());
			ewbEntity.setCreatedBy(userName);
			ewbEntity.setCreatedOn(LocalDateTime.now());
			ewbEntity.setType("CONSOLIDATED");
			ewbRepository.save(ewbEntity);

		} else {
			for (int i = 0; i < ewbList.size(); i++) {
				cewbRepo.updateErrorResponse(req.getSerialNo(), ewbList.get(i),
						req.getFileId(), resp.getErrorCode(),
						resp.getErrorDesc());
			}
		}

		EwbLifecycleEntity ewbLcEntity = new EwbLifecycleEntity();
		ewbLcEntity.setCEwbNum(resp.getCEwbNo());
		ewbLcEntity.setCEwbDate(resp.getCEWBDate());
		ewbLcEntity.setFromPlace(req.getFromPlace());
		ewbLcEntity.setFromState(req.getFromState());
		ewbLcEntity.setVehicleNum(req.getVehicleNo());
		ewbLcEntity.setTransMode(req.getTransMode());
		ewbLcEntity.setTransDocNo(req.getTransDocNo());
		ewbLcEntity.setTransDocDate(req.getTransDocDate());
		ewbLcEntity.setErrorCode(resp.getErrorCode());
		ewbLcEntity.setErrorDesc(resp.getErrorDesc());
		ewbLcEntity
				.setFunctionStatus(!Strings.isNullOrEmpty(resp.getErrorCode()));
		ewbLcEntity.setFunction(APIIdentifiers.GENERATE_CONSOLIDATED_EWB);
		ewbLcEntity.setActive(true);
		ewbLCeRepo.save(ewbLcEntity);

	}

	@Override
	public void rejectEwbDbUpdate(RejectEwbReqDto req,
			RejectEwbResponseDto resp) {

		EwbLifecycleEntity ewbLcEntity = new EwbLifecycleEntity();
		ewbLcEntity.setEwbNum(req.getEwbNo());
		ewbLcEntity.setRejectDate(resp.getEwbRejectedDate());
		ewbLcEntity.setErrorCode(resp.getErrorCode());
		ewbLcEntity.setErrorDesc(resp.getErrorMessage());
		ewbLcEntity.setModifiedOn(resp.getEwbRejectedDate());
		ewbLcEntity.setActive(true);
		ewbLcEntity
				.setFunctionStatus(!Strings.isNullOrEmpty(resp.getErrorCode()));
		ewbLcEntity.setFunction(APIIdentifiers.CANCEL_EWB);
		ewbLCeRepo.save(ewbLcEntity);
		if (Strings.isNullOrEmpty(resp.getErrorCode())) {

			ewbRepository.updateEwbCancelStatusByEwbNo(resp.getEwayBillNo(),
					true, resp.getEwbRejectedDate(),
					EwbStatusNew.REJECTED.getEwbNewStatusCode(),
					ewbLcEntity.getId());

			docrepo.updateEwbStatusByDocId(req.getDocHeaderId(),
					EwbStatusNew.REJECTED.getEwbNewStatusCode(),
					EwbProcessingStatus.REJECTED.getEwbProcessingStatusCode());

		} else {
			ewbRepository.updateLifeCycleIdByEwbNumber(req.getEwbNo(),
					ewbLcEntity.getId());

			docrepo.updateCancelErrorEwbResponseById(req.getDocHeaderId(),
					resp.getErrorCode(), resp.getErrorMessage(),
					AspInvoiceStatus.ASP_ERROR.getAspInvoiceStatusCode(),
					EwbProcessingStatus.ERROR_REJECTION
							.getEwbProcessingStatusCode());
		}

	}
}
