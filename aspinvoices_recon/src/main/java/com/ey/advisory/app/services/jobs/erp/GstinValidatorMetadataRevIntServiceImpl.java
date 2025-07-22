package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.GetGstinMasterDetailApiPushEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GstinValidatorPayloadEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GstinValidatorApiPushRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GstinValidatorPayloadRepository;
import com.ey.advisory.app.docs.dto.erp.GstinValidatorDetailsPayloadMsgGstinDetailsDto;
import com.ey.advisory.app.docs.dto.erp.GstinValidatorDetailsPayloadMsgItemDto;
import com.ey.advisory.app.docs.dto.erp.GstinValidatorPayloadBusMesgDto;
import com.ey.advisory.app.docs.dto.erp.GstinValidatorPayloadMetaDataDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("GstinValidatorMetadataRevIntServiceImpl")
public class GstinValidatorMetadataRevIntServiceImpl
		implements GstinValidatorMetadataRevIntService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GstinValidatorPayloadRepository")
	private GstinValidatorPayloadRepository gstnValidatorPayloadRepository;

	@Autowired
	@Qualifier("GstinValidatorApiPushRepository")
	private GstinValidatorApiPushRepository apiPushRepo;

	public GstinValidatorPayloadMetaDataDto payloadErrorInfoMsg(String type,
			String payloadId) {

		try {
			GstinValidatorPayloadMetaDataDto metaDataDto = new GstinValidatorPayloadMetaDataDto();

			GstinValidatorPayloadBusMesgDto busMsgDto = getPayloadBusMesg(
					payloadId);
			metaDataDto.setDto(busMsgDto);

			List<GstinValidatorDetailsPayloadMsgItemDto> gstinValidatorPayloadItemDtos = getObjectPayload(
					payloadId);
			LOGGER.debug(" gstinValidatorPayloadItemDtos ->{} ",
					gstinValidatorPayloadItemDtos);
			if (gstinValidatorPayloadItemDtos == null
					|| gstinValidatorPayloadItemDtos.isEmpty()) {
				return metaDataDto;
			}
			GstinValidatorDetailsPayloadMsgGstinDetailsDto gstinDetails = new GstinValidatorDetailsPayloadMsgGstinDetailsDto();
			gstinDetails.setGstinDetailDto(gstinValidatorPayloadItemDtos);

			metaDataDto.setGstinDetailDto(gstinDetails);

			LOGGER.debug(" metaDataDto -> {} ", metaDataDto);
			return metaDataDto;

		} catch (Exception ex) {
			LOGGER.error(" error occured in making xml data - {} ",
					ex.getMessage());
			throw new AppException(ex);

		}

	}

	private GstinValidatorPayloadBusMesgDto getPayloadBusMesg(
			String payloadId) {
		GstinValidatorPayloadBusMesgDto msgDto = new GstinValidatorPayloadBusMesgDto();

		GstinValidatorPayloadEntity entity = gstnValidatorPayloadRepository
				.getGstinValidatorPayload(payloadId);

		if (entity != null) {
			msgDto.setErrorCount(entity.getErrorCount() != null
					? entity.getErrorCount() : 0);
			msgDto.setModifiedOn(
					EYDateUtil.toISTDateTimeFromUTC(entity.getModifiedOn()));
			msgDto.setPayloadId(entity.getPayloadId());
			int processCount = (entity.getTotalCount() != null
					? entity.getTotalCount() : 0)
					- (entity.getErrorCount() != null ? entity.getErrorCount()
							: 0);
			msgDto.setProcessCount(processCount>0?processCount:0);
			
			msgDto.setStatus(entity.getStatus());
			msgDto.setTotalCount(entity.getTotalCount());
			if (entity.getStatus().equalsIgnoreCase(APIConstants.P)
					|| entity.getStatus().equalsIgnoreCase(APIConstants.PE)) {
				LOGGER.debug("MessageInfo Not Req");
			} else {
				msgDto.setMessageInfo(entity.getJsonErrorResponse());
			}
			msgDto.setPushType(entity.getPushType());
		}
		return msgDto;
	}

	private List<GstinValidatorDetailsPayloadMsgItemDto> getObjectPayload(
			String payloadId) {

		List<GstinValidatorDetailsPayloadMsgItemDto> itemDtos = new ArrayList<>();

		List<GetGstinMasterDetailApiPushEntity> objs = apiPushRepo
				.getPayloadGstinDetails(payloadId);

		objs.forEach(obj -> {
			GstinValidatorDetailsPayloadMsgItemDto dto = new GstinValidatorDetailsPayloadMsgItemDto();
			dto.setId(String.valueOf(obj.getId()));

			dto.setGstin(obj.getGstin());
			dto.setPan(obj.getGstin().substring(2, 12));
			dto.setCustomerCode(obj.getCustomerCode());
			dto.setStatus((obj.getErrorCode() == null ? "Processed" : "Error"));
			dto.setTaxType(obj.getTaxpayerType());
			dto.setGstinStatus(obj.getGstinStatus());
			dto.setEinvApp(obj.getEinvApplicable() != null
					? ("Yes".equalsIgnoreCase(obj.getEinvApplicable()) ? "Y"
							: "N")
					: "N");
			dto.setConstOfBuss(obj.getBusinessConstitution());
			dto.setCentrJuris(obj.getCentreJurisdiction());
			dto.setLegalName(obj.getLegalName());
			dto.setTradeName(obj.getTradeName());
			dto.setNatOfBuss(obj.getBusinessNatureActivity());
			dto.setBuildingName(obj.getBuildingName());
			dto.setStreet(obj.getStreet());
			dto.setLocation(obj.getLocation());
			dto.setDoorNumber(obj.getDoorNum());
			dto.setStateName(obj.getState());
			dto.setFloorNumber(obj.getFloorNum());
			dto.setPincode(obj.getPin());
			dto.setDateRegistration(obj.getRegistrationDate() != null
					? convertDateFormat(obj.getRegistrationDate().toString()) : null);
			dto.setStateJurisdiction(obj.getStateJurisdiction() != null
					? obj.getStateJurisdiction() : null);
			dto.setDateCancellation(obj.getCancellationDate() != null
					? convertDateFormat(obj.getCancellationDate().toString()) : null);
			dto.setErrorCode(obj.getErrorCode());
			dto.setErrorDesc(obj.getErrorDiscription());
			itemDtos.add(dto);
		});

		return itemDtos;
	}
	
	private String convertDateFormat(String dt) {
		DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
		LOGGER.debug(" dt - {}  ",dt);
		// Parse the original date string
		LocalDate originalDate = LocalDate.parse(dt, originalFormatter);

        // Define the formatter for the desired date format
        DateTimeFormatter desiredFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        // Format the date to the desired format
        String desiredDateString = originalDate.format(desiredFormatter);
        LOGGER.debug(" desiredDateString - {}  ",desiredDateString);
		return desiredDateString;
	}
}
