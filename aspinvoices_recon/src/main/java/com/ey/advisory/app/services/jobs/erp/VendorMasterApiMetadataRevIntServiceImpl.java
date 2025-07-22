package com.ey.advisory.app.services.jobs.erp;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterApiPayloadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterErrorReportEntityRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.docs.dto.erp.VendorMasterApiDetailsPayloadMsgGstinDetailsDto;
import com.ey.advisory.app.docs.dto.erp.VendorMasterApiPayloadBusMesgDto;
import com.ey.advisory.app.docs.dto.erp.VendorMasterApiPayloadMetaDataDto;
import com.ey.advisory.app.docs.dto.erp.VendorMasterApiPayloadMsgItemDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterErrorReportEntity;
import com.ey.advisory.app.services.vendor.master.apipush.VendorMasterApiPayloadEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("VendorMasterApiMetadataRevIntServiceImpl")
public class VendorMasterApiMetadataRevIntServiceImpl
		implements VendorMasterApiMetadataRevIntService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("VendorMasterApiPayloadRepository")
	private VendorMasterApiPayloadRepository vendorMasterApiPayloadRepository;

	@Autowired
	@Qualifier("VendorMasterUploadEntityRepository")
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	@Qualifier("VendorMasterErrorReportEntityRepository")
	private VendorMasterErrorReportEntityRepository vendorMasterErrorReportEntityRepository;

	public VendorMasterApiPayloadMetaDataDto payloadErrorInfoMsg(String type,
			String payloadId) {
		try {
			VendorMasterApiPayloadMetaDataDto metaDataDto = new VendorMasterApiPayloadMetaDataDto();
			VendorMasterApiPayloadBusMesgDto busMsgDto = getPayloadBusMesg(
					payloadId);
			metaDataDto.setDto(busMsgDto);
			List<VendorMasterApiPayloadMsgItemDto> vendorDetailDtoList = getObjectPayload(
					payloadId);
			LOGGER.debug(" vendorDetailDtoList ->{} ", vendorDetailDtoList);
			if (vendorDetailDtoList == null || vendorDetailDtoList.isEmpty()) {
				return metaDataDto;
			}
			VendorMasterApiDetailsPayloadMsgGstinDetailsDto vendorDetails = new VendorMasterApiDetailsPayloadMsgGstinDetailsDto();
			vendorDetails.setVendorDetailDtoList(vendorDetailDtoList);
			metaDataDto.setVendorDetailDto(vendorDetails);
			LOGGER.debug(" metaDataDto -> {} ", metaDataDto);
			return metaDataDto;
		} catch (Exception ex) {
			LOGGER.error(" error occured in making xml data - {} ",
					ex.getMessage());
			throw new AppException(ex);
		}
	}

	private VendorMasterApiPayloadBusMesgDto getPayloadBusMesg(
			String payloadId) {
		VendorMasterApiPayloadBusMesgDto msgDto = new VendorMasterApiPayloadBusMesgDto();
		VendorMasterApiPayloadEntity entity = vendorMasterApiPayloadRepository
				.getVendorMaterApiPayload(payloadId);
		if (entity != null) {
			msgDto.setPayloadId(entity.getPayloadId());
			if (entity.getStatus().equalsIgnoreCase(APIConstants.P)
					|| entity.getStatus().equalsIgnoreCase(APIConstants.PE)) {
				LOGGER.debug("MessageInfo Not Req");
			} else {
				msgDto.setMessageInfo(entity.getJsonErrorResponse());
			}
		}
		return msgDto;
	}

	private List<VendorMasterApiPayloadMsgItemDto> getObjectPayload(
			String payloadId) {
		List<VendorMasterApiPayloadMsgItemDto> itemDtos = new ArrayList<>();

		List<VendorMasterErrorReportEntity> errorEntities = vendorMasterErrorReportEntityRepository
				.findByPayloadId(payloadId);

		errorEntities.forEach(obj -> {
			VendorMasterApiPayloadMsgItemDto dto = new VendorMasterApiPayloadMsgItemDto();
			dto.setRecipientPAN(obj.getRecipientPAN());
			dto.setVendorPAN(obj.getVendorPAN());
			dto.setVendorGstin(obj.getVendorGstin());
			dto.setSupplierCode(obj.getVendorCode());
			dto.setVendorName(obj.getVendorName());
			dto.setVendPrimEmailId(obj.getVendPrimEmailId());
			dto.setVendorContactNumber(obj.getVendorContactNumber());
			dto.setVendorEmailId1(obj.getVendorEmailId1());
			dto.setVendorEmailId2(obj.getVendorEmailId2());
			dto.setVendorEmailId3(obj.getVendorEmailId3());
			dto.setVendorEmailId4(obj.getVendorEmailId4());
			dto.setRecipientEmailId1(obj.getRecipientEmailId1());
			dto.setRecipientEmailId2(obj.getRecipientEmailId2());
			dto.setRecipientEmailId3(obj.getRecipientEmailId3());
			dto.setRecipientEmailId4(obj.getRecipientEmailId4());
			dto.setRecipientEmailId5(obj.getRecipientEmailId5());
			dto.setVendorType(obj.getVendorType());
			dto.setHsn(obj.getHsn());
			dto.setVendorRiskCategory(obj.getVendorRiskCategory());
			dto.setVendorPaymentTerms(obj.getVendorPaymentTerms().toString());
			dto.setVendorRemarks(obj.getVendorRemarks());
			dto.setApprovalStatus(obj.getApprovalStatus());
			dto.setExcludeVendorRemarks(obj.getExcludeVendorRemarks());
			dto.setIsVendorCom(obj.getIsVendorCom());
			dto.setIsExcludeVendor(obj.getIsExcludeVendor());
			dto.setIsNonComplaintCom(obj.getIsNonComplaintCom());
			dto.setIsCreditEligibility(obj.getIsCreditEligibility());
			dto.setIsDelete(obj.getIsDelete());
			dto.setErrorMsg(obj.getError());
			dto.setMsg(obj.getInformation());

			itemDtos.add(dto);
		});

		return itemDtos;
	}
}
