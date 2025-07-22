/**
* 
 */
package com.ey.advisory.app.services.doc.gstr1a;

import java.math.BigDecimal;
import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.EwbEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAdditionalDocDetails;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAttributeDetails;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AContractDetails;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1APreceedingDocDetails;
import com.ey.advisory.app.data.gstr1A.repositories.client.DocRepositoryGstr1A;
import com.ey.advisory.app.data.repositories.client.EinvoiceRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceDocSaveRespDto;
import com.ey.advisory.common.EwbStatus;
import com.ey.advisory.einv.client.EinvoiceEntity;
import com.ey.advisory.einv.dto.GenerateEWBByIrnERPReqDto;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.client.domain.EwbLifecycleEntity;
import com.ey.advisory.ewb.client.repositories.EwbLifecycleRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("Gstr1AEinvoiceDocSaveUtility")
public class Gstr1AEinvoiceDocSaveUtility {

	private static DateTimeFormatter FORMATTER1 = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DocRepositoryGstr1A")
	private DocRepositoryGstr1A docRepository;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	@Autowired
	@Qualifier("EinvoiceRepository")
	private EinvoiceRepository einvoiceRepository;

	@Autowired
	@Qualifier("EwbLifecycleRepository")
	private EwbLifecycleRepository ewbLCeRepo;

	// Only for GenerateEwbByIrn
	public void saveEwbLifeCycleEntity(GenerateEWBByIrnERPReqDto savedoc,
			Long id) {

		if (savedoc.getEwbDetails() != null) {
			EwbLifecycleEntity ewbLcEntity = new EwbLifecycleEntity();
			ewbLcEntity.setEwbNum(savedoc.getEwbDetails().getEwayBillNo());
			ewbLcEntity.setVehicleNum(savedoc.getVehNo());
			ewbLcEntity.setVehicleType(savedoc.getVehType());
			ewbLcEntity.setTransporterId(savedoc.getTransId());
			ewbLcEntity.setTransMode(savedoc.getTransMode());
			ewbLcEntity.setTransDocNo(savedoc.getTrnDocNo());
			if (savedoc.getTrnDocDt() != null) {
				ewbLcEntity.setTransDocDate(
						LocalDate.parse(savedoc.getTrnDocDt(), FORMATTER1));
			}
			if (savedoc.getDispDtls() != null) {
				ewbLcEntity.setFromPlace(savedoc.getDispDtls().getLoc());
				ewbLcEntity.setFromState(savedoc.getDispDtls().getStcd());
				ewbLcEntity.setFromPincode(
						String.valueOf(savedoc.getDispDtls().getPin()));
			}
			ewbLcEntity.setValidUpto(savedoc.getEwbDetails().getValidUpto());
			ewbLcEntity.setAspDistance(savedoc.getDistance());
			// ewbLcEntity.setCancelDate(savedoc.getEwbDetails().getCancelDate());
			ewbLcEntity.setCreatedBy("SYSTEM");
			ewbLcEntity.setCreatedOn(LocalDateTime.now());
			ewbLcEntity.setModifiedBy("SYSTEM");
			ewbLcEntity.setModifiedOn(LocalDateTime.now());
			ewbLcEntity.setActive(true);
			ewbLcEntity.setFunctionStatus(true);
			ewbLcEntity.setFunction(APIIdentifiers.GENERATE_EWB);
			ewbLCeRepo.save(ewbLcEntity);
			saveEwbEntity(savedoc, id, ewbLcEntity.getId());
		}
	}

	private void saveEwbEntity(GenerateEWBByIrnERPReqDto savedoc, Long id,
			Long lifeCycleId) {

		EwbEntity ewb = new EwbEntity();
		ewb.setDocHeaderId(id);
		ewb.setEwbNum(savedoc.getEwbDetails().getEwayBillNo());
		ewb.setEwbDate(savedoc.getEwbDetails().getEwayBillDate());
		ewb.setValidUpto(savedoc.getEwbDetails().getValidUpto());
		ewb.setAlert(savedoc.getEwbDetails().getAlert());

		ewb.setVehicleNum(savedoc.getVehNo());
		ewb.setVehicleType(savedoc.getVehType());
		ewb.setTransporterId(savedoc.getTransId());
		ewb.setTransMode(savedoc.getTransMode());
		ewb.setTransDocNum(savedoc.getTrnDocNo());

		if (savedoc.getTrnDocDt() != null) {
			ewb.setTransDocDate(
					LocalDate.parse(savedoc.getTrnDocDt(), FORMATTER1));
		}
		if (savedoc.getDispDtls() != null) {
			ewb.setFromPlace(savedoc.getDispDtls().getLoc());
			ewb.setFromState(savedoc.getDispDtls().getStcd());
			ewb.setFromPincode(savedoc.getDispDtls().getPin());
		}
		ewb.setAspDistance(savedoc.getDistance());
		ewb.setStatus(savedoc.getEwbDetails().getValidUpto() != null
				? EwbStatus.EWB_ACTIVE.getEwbStatusCode()
				: EwbStatus.PARTA_GENERATED.getEwbStatusCode());
		ewb.setCreatedBy("SYSTEM");
		ewb.setCreatedOn(LocalDateTime.now());
		ewb.setModifiedBy("SYSTEM");
		ewb.setModifiedOn(LocalDateTime.now());
		ewb.setLifeCycleId(lifeCycleId);
		ewbRepository.save(ewb);

	}

	public void updateDocwithGenIrnReq(Gstr1AOutwardTransDocument doc,
			GenerateEWBByIrnERPReqDto reqDto, Long docHeaderId,
			EInvoiceDocSaveRespDto docSaveRespDto) {

		doc.setEwbStatus(reqDto.getEwbDetails().getValidUpto() != null
				? EwbStatus.EWB_ACTIVE.getEwbStatusCode()
				: EwbStatus.PARTA_GENERATED.getEwbStatusCode());
		doc.setEwbDateResp(reqDto.getEwbDetails().getEwayBillDate());
		doc.setEwbNoresp(Long.valueOf(reqDto.getEwbDetails().getEwayBillNo()));
		doc.setSupplyType(reqDto.getSupplyType());
		doc.setDocCategory(reqDto.getDocCategory());
		doc.setVehicleNo(reqDto.getVehNo());
		doc.setVehicleType(reqDto.getVehType());
		doc.setTransporterID(reqDto.getTransId());

		if (reqDto.getTrnDocDt() != null) {
			doc.setTransportDocDate(
					LocalDate.parse(reqDto.getTrnDocDt(), FORMATTER1));
		}
		doc.setTransportDocNo(reqDto.getTrnDocNo());
		doc.setTransporterName(reqDto.getTransName());
		doc.setTransportMode(reqDto.getTransMode());
		doc.setCustomerPincode(reqDto.getCustPincd());
		doc.setShipToPincode(reqDto.getShipToPincd());
		doc.setSupplierPincode(reqDto.getSuppPincd());
		doc.setDispatcherPincode(reqDto.getDispatcherPincd());
		doc.setDistance(
				new BigDecimal(reqDto.getEwbDetails().getNicDistance()));
		if (reqDto.getDispDtls() != null) {
			doc.setDispatcherBuildingNumber(reqDto.getDispDtls().getAddr1());
			doc.setDispatcherBuildingName(reqDto.getDispDtls().getAddr2());
			doc.setDispatcherLocation(reqDto.getDispDtls().getLoc());
			doc.setDispatcherPincode(reqDto.getDispDtls().getPin());
			doc.setDispatcherStateCode(reqDto.getDispDtls().getStcd());
			doc.setDispatcherTradeName(reqDto.getDispDtls().getNm());
		}
		if (reqDto.getExpShipDtls() != null) {
			doc.setShipToBuildingNumber(reqDto.getExpShipDtls().getAddr1());
			doc.setShipToBuildingName(reqDto.getExpShipDtls().getAddr2());
			doc.setShipToLocation(reqDto.getExpShipDtls().getLoc());
			doc.setShipToPincode(reqDto.getExpShipDtls().getPin());
			doc.setShipToState(reqDto.getExpShipDtls().getStcd());
		}
		doc.setDeleted(false);

		docRepository.updateDocDeletion(Arrays.asList(docHeaderId),
				LocalDateTime.now(), "BCAPI");

		doc.setId(null);
		for (Gstr1AOutwardTransDocLineItem lineItems : doc.getLineItems()) {
			lineItems.setId(null);

			if (!lineItems.getAttribDtls().isEmpty()) {
				for (Gstr1AAttributeDetails attDtls : lineItems
						.getAttribDtls()) {
					attDtls.setId(null);
				}
			}
		}

		for (Gstr1APreceedingDocDetails preDoc : doc.getPreDocDtls()) {
			preDoc.setId(null);
		}
		for (Gstr1AContractDetails conDtls : doc.getContrDtls()) {
			conDtls.setId(null);
		}
		for (Gstr1AAdditionalDocDetails addDtls : doc.getAddlDocDtls()) {
			addDtls.setId(null);
		}

		docRepository.save(doc);
		saveEwbLifeCycleEntity(reqDto, doc.getId());
		docSaveRespDto.setDocNo(doc.getDocNo());
		docSaveRespDto.setId(doc.getId());
		docSaveRespDto.setDocType(doc.getDocType());
		docSaveRespDto.setSupplierGstin(doc.getSgstin());
		docSaveRespDto.setAccountVoucherNo(doc.getAccountingVoucherNumber());
		docSaveRespDto.setDocDate(doc.getDocDate());
		docSaveRespDto.setEwbNo(reqDto.getEwbDetails().getEwayBillNo());
	}

	// Below method are used for Generate Einvoice,Ewb,CancelEinvoice
	public void saveEinvoiceEntity(Gstr1AOutwardTransDocument savedoc,
			Long id) {

		if (savedoc.geteInvDetails() != null) {
			if (savedoc.geteInvDetails().getCancelDate() != null) {
				einvoiceRepository.updateEinvCanStatusByIrn(
						savedoc.geteInvDetails().getIrn(), true,
						savedoc.geteInvDetails().getCancelDate(),
						savedoc.geteInvDetails().getCancelReason(),
						savedoc.geteInvDetails().getCancelRemarks());
			} else {
				Optional<EinvoiceEntity> einvRecord = einvoiceRepository
						.getEinvDetails(savedoc.geteInvDetails().getIrn());
				if (!einvRecord.isPresent()) {
					EinvoiceEntity einv = new EinvoiceEntity();
					Clob signedInv = null;
					try {
						signedInv = new javax.sql.rowset.serial.SerialClob(
								savedoc.geteInvDetails().getSignedInvoice()
										.toCharArray());
					} catch (Exception ex) {
						LOGGER.error(
								"exception occured while converting into clob");
					}
					einv.setDocHeaderId(id);
					einv.setIrn(savedoc.geteInvDetails().getIrn());
					einv.setSignedInv(signedInv);
					einv.setSignedQR(
							savedoc.geteInvDetails().getSignedQRCode());
					einv.setAckNo(savedoc.geteInvDetails().getAckNo());
					einv.setAckDt(savedoc.geteInvDetails().getAckDate());
					einv.setStatusDesc(savedoc.geteInvDetails().getStatus());
					einv.setFormattedQRCode(
							savedoc.geteInvDetails().getFormattedQRCode());
					einv.setCreatedBy("SYSTEM");
					einv.setCreatedOn(LocalDateTime.now());
					einv.setModifiedBy("SYSTEM");
					einv.setModifiedOn(LocalDateTime.now());
					einvoiceRepository.save(einv);
				}
			}
		}
	}

	private void saveEwbEntity(Gstr1AOutwardTransDocument savedoc, Long id,
			Long lifeCycleId) {

		Optional<EwbEntity> isEwbAva = ewbRepository
				.findEwbDtls(savedoc.getEwbDetails().getEwayBillNo());

		if (!isEwbAva.isPresent()) {
			EwbEntity ewb = new EwbEntity();
			ewb.setDocHeaderId(id);
			ewb.setEwbNum(savedoc.getEwbDetails().getEwayBillNo());
			ewb.setEwbDate(savedoc.getEwbDetails().getEwayBillDate());
			ewb.setValidUpto(savedoc.getEwbDetails().getValidUpto());
			ewb.setAlert(savedoc.getEwbDetails().getAlert());
			ewb.setCancelDate(savedoc.getEwbDetails().getCancelDate());
			ewb.setTransporterId(savedoc.getEwbDetails().getTransporterId());
			ewb.setFromPincode(savedoc.getEwbDetails().getFromPincode());
			ewb.setFromPlace(savedoc.getEwbDetails().getFromPlace());
			ewb.setFromState(savedoc.getEwbDetails().getFromState());
			ewb.setVehicleNum(savedoc.getEwbDetails().getVehicleNum());
			ewb.setVehicleType(savedoc.getEwbDetails().getVehicleType());
			ewb.setTransMode(savedoc.getEwbDetails().getTransportMode());
			ewb.setTransDocNum(savedoc.getEwbDetails().getTransDocNum());
			ewb.setTransDocDate(savedoc.getEwbDetails().getTransDocDate());
			ewb.setAspDistance(savedoc.getEwbDetails().getAspDistance());
			ewb.setStatus(savedoc.getEwbDetails().getValidUpto() != null
					? EwbStatus.EWB_ACTIVE.getEwbStatusCode()
					: EwbStatus.PARTA_GENERATED.getEwbStatusCode());
			ewb.setCreatedBy("SYSTEM");
			ewb.setCreatedOn(LocalDateTime.now());
			ewb.setModifiedBy("SYSTEM");
			ewb.setModifiedOn(LocalDateTime.now());
			ewb.setLifeCycleId(lifeCycleId);
			ewbRepository.save(ewb);
		}
	}

	public void saveEwbLifeCycleEntity(Gstr1AOutwardTransDocument savedoc,
			Long id) {

		if (savedoc.getEwbDetails() != null) {
			EwbLifecycleEntity ewbLcEntity = new EwbLifecycleEntity();
			ewbLcEntity.setEwbNum(savedoc.getEwbDetails().getEwayBillNo());
			ewbLcEntity.setVehicleNum(savedoc.getEwbDetails().getVehicleNum());
			ewbLcEntity
					.setVehicleType(savedoc.getEwbDetails().getVehicleType());
			ewbLcEntity.setTransporterId(
					savedoc.getEwbDetails().getTransporterId());
			ewbLcEntity
					.setTransMode(savedoc.getEwbDetails().getTransportMode());
			ewbLcEntity.setTransDocNo(savedoc.getEwbDetails().getTransDocNum());
			ewbLcEntity
					.setTransDocDate(savedoc.getEwbDetails().getTransDocDate());
			ewbLcEntity.setFromPlace(savedoc.getEwbDetails().getFromPlace());
			ewbLcEntity.setFromState(savedoc.getEwbDetails().getFromState());
			ewbLcEntity.setFromPincode(
					String.valueOf(savedoc.getEwbDetails().getFromPincode()));
			ewbLcEntity.setValidUpto(savedoc.getEwbDetails().getValidUpto());
			ewbLcEntity
					.setAspDistance(savedoc.getEwbDetails().getAspDistance());
			ewbLcEntity.setCancelDate(savedoc.getEwbDetails().getCancelDate());
			ewbLcEntity.setCreatedBy("SYSTEM");
			ewbLcEntity.setCreatedOn(LocalDateTime.now());
			ewbLcEntity.setModifiedBy("SYSTEM");
			ewbLcEntity.setModifiedOn(LocalDateTime.now());
			ewbLcEntity.setActive(true);
			ewbLcEntity.setFunctionStatus(true);
			ewbLcEntity.setFunction(APIIdentifiers.GENERATE_EWB);
			ewbLCeRepo.save(ewbLcEntity);
			saveEwbEntity(savedoc, id, ewbLcEntity.getId());
		}
	}

}
