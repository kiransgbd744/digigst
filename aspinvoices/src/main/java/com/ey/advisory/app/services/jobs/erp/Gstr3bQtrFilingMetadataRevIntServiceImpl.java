package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr3bQtrFilingApiPushRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3bQtrFilingPayloadRepository;
import com.ey.advisory.app.docs.dto.erp.Gstr3bQtrFilingDetailsPayloadMsgGstinDetailsDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bQtrFilingPayloadBusMesgDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bQtrFilingPayloadMetaDataDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bQtrFilingPayloadMsgItemDto;
import com.ey.advisory.app.services.gstr3b.qtr.filing.apipush.Gstr3bQtrFilingDetailApiPushEntity;
import com.ey.advisory.app.services.gstr3b.qtr.filing.apipush.Gstr3bQtrFilingPayloadEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("Gstr3bQtrFilingMetadataRevIntServiceImpl")
public class Gstr3bQtrFilingMetadataRevIntServiceImpl
		implements Gstr3bQtrFilingMetadataRevIntService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr3bQtrFilingPayloadRepository")
	private Gstr3bQtrFilingPayloadRepository gstr3bQtrFilingPayloadRepository;

	@Autowired
	@Qualifier("Gstr3bQtrFilingApiPushRepository")
	private Gstr3bQtrFilingApiPushRepository apiPushRepo;

	public Gstr3bQtrFilingPayloadMetaDataDto payloadErrorInfoMsg(String type,
			String payloadId) {

		try {
			Gstr3bQtrFilingPayloadMetaDataDto metaDataDto = new Gstr3bQtrFilingPayloadMetaDataDto();

			Gstr3bQtrFilingPayloadBusMesgDto busMsgDto = getPayloadBusMesg(
					payloadId);
			metaDataDto.setDto(busMsgDto);

			List<Gstr3bQtrFilingPayloadMsgItemDto> gstinValidatorPayloadItemDtos = getObjectPayload(
					payloadId);
			LOGGER.debug(" gstinValidatorPayloadItemDtos ->{} ",
					gstinValidatorPayloadItemDtos);
			if (gstinValidatorPayloadItemDtos == null
					|| gstinValidatorPayloadItemDtos.isEmpty()) {
				return metaDataDto;
			}
			Gstr3bQtrFilingDetailsPayloadMsgGstinDetailsDto gstinDetails = new Gstr3bQtrFilingDetailsPayloadMsgGstinDetailsDto();
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

	private Gstr3bQtrFilingPayloadBusMesgDto getPayloadBusMesg(
			String payloadId) {
		Gstr3bQtrFilingPayloadBusMesgDto msgDto = new Gstr3bQtrFilingPayloadBusMesgDto();

		Gstr3bQtrFilingPayloadEntity entity = gstr3bQtrFilingPayloadRepository
				.getGstr3bQtrFilingPayload(payloadId);

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

	private List<Gstr3bQtrFilingPayloadMsgItemDto> getObjectPayload(
			String payloadId) {

		List<Gstr3bQtrFilingPayloadMsgItemDto> itemDtos = new ArrayList<>();

		List<Gstr3bQtrFilingDetailApiPushEntity> objs = apiPushRepo
				.getPayloadGstinDetails(payloadId);

		objs.forEach(obj -> {
			Gstr3bQtrFilingPayloadMsgItemDto dto = new Gstr3bQtrFilingPayloadMsgItemDto();
			dto.setGstin(obj.getGstin());
			dto.setReturnPeriod(obj.getReturnPeriod());
			dto.setIsFiled(obj.getIsFiled());
			dto.setQuarter(obj.getQuarter());
			dto.setErrorMsg(obj.getErrorDiscription());

			itemDtos.add(dto);
		});

		return itemDtos;
	}

	private String convertDateFormat(String dt) {
		DateTimeFormatter originalFormatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd");

		LOGGER.debug(" dt - {}  ", dt);
		// Parse the original date string
		LocalDate originalDate = LocalDate.parse(dt, originalFormatter);

		// Define the formatter for the desired date format
		DateTimeFormatter desiredFormatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy");

		// Format the date to the desired format
		String desiredDateString = originalDate.format(desiredFormatter);
		LOGGER.debug(" desiredDateString - {}  ", desiredDateString);
		return desiredDateString;
	}
}
