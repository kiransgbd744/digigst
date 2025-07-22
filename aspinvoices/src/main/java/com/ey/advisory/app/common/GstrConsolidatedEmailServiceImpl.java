package com.ey.advisory.app.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Quintet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr2bAutoCommEntity;
import com.ey.advisory.app.data.entities.client.ConsolidateEmailMappingEntity;
import com.ey.advisory.app.data.entities.client.RecipientMasterUploadEntity;
import com.ey.advisory.app.data.repositories.client.ConsolidateEmailMappingRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@Component("GstrConsolidatedEmailServiceImpl")
public class GstrConsolidatedEmailServiceImpl
		implements GstrConsolidatedEmailService {

	@Autowired
	@Qualifier("ConsolidateEmailMappingRepository")
	private ConsolidateEmailMappingRepository emailMappingRepo;

	@Autowired
	@Qualifier("SendEmailServiceImpl")
	private SendEmailService sendEmailService;

	@Override
	public Quintet<List<String>, List<GstinWiseEmailDto>, String, Boolean, String> getGstinsData(
			List<RecipientMasterUploadEntity> uploadEntites,
			List<String> activeGstins) {
		List<GstinWiseEmailDto> gstinWiseList = new ArrayList<>();
		Set<String> emailSet = new HashSet<>();
		List<String> secondaryEmails = new ArrayList<>();
		String notificationCode = null;
		Boolean secondEmailEligible = false;
		int inActiveCount = 0;
		int activeCount = 0;
		String activeGstinString = null;
		List<String> gstins = new ArrayList<>();
		for (RecipientMasterUploadEntity entity : uploadEntites) {

			populateSecondaryEmails(entity, emailSet);

			GstinWiseEmailDto dto = new GstinWiseEmailDto();
			dto.setCgstin(entity.getRecipientGstin());
			dto.setScheduleDate(EYDateUtil.toISTDateTimeFromUTC(LocalDate.now())
					.toString());
			if (activeGstins.contains(entity.getRecipientGstin())) {
				dto.setGetStatus("Initiated");
				gstins.add(entity.getRecipientGstin());
				activeCount++;
			} else {
				dto.setGetStatus("Not Initiated. Auth token is Inactive.");
				inActiveCount++;
			}

			gstinWiseList.add(dto);
		}
		secondaryEmails.addAll(emailSet);
		if (inActiveCount > 0)
			notificationCode = NotificationCodes.GET_NOT_INITIATED;
		else
			notificationCode = NotificationCodes.GET_INITIATED;

		if (activeCount > 0)
			secondEmailEligible = true;

		if (!gstins.isEmpty())
			activeGstinString = String.join(",", gstins);
		return new Quintet<>(secondaryEmails, gstinWiseList, notificationCode,
				secondEmailEligible, activeGstinString);
	}

	@Override
	public Pair<List<String>, List<GstinWiseEmailDto>> getGstinsAndSectionWiseData(
			List<RecipientMasterUploadEntity> uploadEntites,
			List<Gstr2bAutoCommEntity> getCallRecords, String taxPeriod) {
		List<GstinWiseEmailDto> gstinWiseList = new ArrayList<>();
		Set<String> emailSet = new HashSet<>();
		List<String> secondaryEmails = new ArrayList<>();
		for (RecipientMasterUploadEntity entity : uploadEntites) {

			populateSecondaryEmails(entity, emailSet);
			GstinWiseEmailDto dto = new GstinWiseEmailDto();
			String gstin = entity.getRecipientGstin();
			dto.setCgstin(gstin);
			String generationDate=EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now()).toString();
			generationDate = generationDate.substring(0, 19);
			generationDate = generationDate.replace("T", " ");
			dto.setGenerationDate(generationDate);
			dto.setScheduleDate(EYDateUtil.toISTDateTimeFromUTC(LocalDate.now())
					.toString());

			SectionWiseDetailsDto secDto = new SectionWiseDetailsDto();
			secDto.setSection("SUMMARY");
			secDto.setTaxPeriod(taxPeriod);
			getCallRecords.forEach(rec -> {
				if (rec.getGstin().equals(gstin))
					secDto.setStatus(rec.getStatus()
							.equals(APIConstants.SUCCESS_WITH_NO_DATA)
									? APIConstants.SUCCESS : rec.getStatus());
			});
			dto.setSectionWiseDetails(Arrays.asList(secDto));
			gstinWiseList.add(dto);
		}
		secondaryEmails.addAll(emailSet);

		return new Pair<>(secondaryEmails, gstinWiseList);
	}

	@Override
	public void persistAndSendEmail(List<GstrEmailDetailsDto> reqDtos,
			boolean isToSave) {
		for (GstrEmailDetailsDto dto : reqDtos) {
			boolean isSent = sendEmailService.sendEmail(dto);
			if (isSent && isToSave) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Email is sent. Calling saveEmailData to save::%s",
							dto);
					LOGGER.debug(msg);
				}
				saveEmailData(dto);
			} else if (isSent && !isToSave) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Email is sent. Calling updateEmailData to update::%s",
							dto);
					LOGGER.debug(msg);
				}
				updateEmailData(dto);
			} else {
				String msg = String.format(
						"Failed to send email. IsSent: %s, for dto: %s", isSent,
						dto);
				LOGGER.error(msg);
			}
		}
	}

	private void saveEmailData(GstrEmailDetailsDto dto) {
		try {

			ConsolidateEmailMappingEntity entity = new ConsolidateEmailMappingEntity();
			entity.setPrimaryEmailId(dto.getPrimaryEmail());
			entity.setFirstEmailTriggered(true);
			entity.setSecondEmailEligible(dto.isSecondEmailEligible());
			entity.setActiveGstins(dto.getActiveGstins());
			entity.setGetCallDate(LocalDate.now());
			entity.setReturnType(dto.getReturnType());
			entity.setCreatedOn(LocalDateTime.now());
			entity.setEntityId(dto.getEntityId());
			emailMappingRepo.save(entity);
		} catch (Exception ee) {
			String msg = String.format("Exception while saving Consolidated "
					+ " Email Mapping Data : %s", dto);
			LOGGER.error(msg, ee);
		}
	}

	private void updateEmailData(GstrEmailDetailsDto dto) {
		try {
			emailMappingRepo.updateSecondEmailTriggered(LocalDateTime.now(),
					dto.getPrimaryEmail(), dto.getReturnType(),
					LocalDate.now());
		} catch (Exception ee) {
			String msg = String.format("Exception while Updating Consolidated "
					+ " Email Mapping Data : %s", dto);
			LOGGER.error(msg, ee);
		}
	}

	@Override
	public Map<String, List<RecipientMasterUploadEntity>> groupByPrimayEmail(
			List<RecipientMasterUploadEntity> uploadEntites) {
		return uploadEntites.stream().collect(Collectors.groupingBy(
				RecipientMasterUploadEntity::getRecipientPrimEmailId));
	}

	private void populateSecondaryEmails(RecipientMasterUploadEntity entity,
			Set<String> emailSet) {
		if (entity.getRecipientEmailId2() != null)
			emailSet.add(entity.getRecipientEmailId2());
		if (entity.getRecipientEmailId3() != null)
			emailSet.add(entity.getRecipientEmailId3());
		if (entity.getRecipientEmailId4() != null)
			emailSet.add(entity.getRecipientEmailId4());
		if (entity.getRecipientEmailId5() != null)
			emailSet.add(entity.getRecipientEmailId5());
	}

}
