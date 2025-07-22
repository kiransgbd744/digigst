package com.ey.advisory.app.processors.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.Get2aAutomationEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.common.GstinWiseEmailDto;
import com.ey.advisory.app.common.GstrConsolidatedEmailService;
import com.ey.advisory.app.common.GstrEmailDetailsDto;
import com.ey.advisory.app.common.SectionWiseDetailsDto;
import com.ey.advisory.app.data.entities.client.RecipientMasterUploadEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.RecipientMasterUploadRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr2aGetEmailHandler")
public class Gstr2aGetEmailHandler {

	@Autowired
	private RecipientMasterUploadRepository recipientMasterUploadRepo;

	@Autowired
	@Qualifier("GstrConsolidatedEmailServiceImpl")
	private GstrConsolidatedEmailService emailService;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	public void prepareEmailBodyContent(Get2aAutomationEntity entity,
			List<String> automatedActiveGstins, List<String> inActiveGstins) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Get2aAutomation Email part logic has initiated with params entity {},"
							+ " activeGstins {} and inactiveGstins {}",
					entity, automatedActiveGstins, inActiveGstins);
		}
		
		List<String> recipientGstinsList = new ArrayList<>();
		recipientGstinsList.addAll(automatedActiveGstins);
		recipientGstinsList.addAll(inActiveGstins);

		List<RecipientMasterUploadEntity> emailMasterData = recipientMasterUploadRepo
				.findByRecipientGstinInAndIsDeleteFalseAndIsGetGstr2AEmailTrueOrderByRecipientPrimEmailId(
						recipientGstinsList);

		Map<String, Pair<Set<String>, Set<String>>> primaryEmailWiseMap = getPrimaryEmailWiseMap(
				emailMasterData);

		if (primaryEmailWiseMap == null) {

			LOGGER.error(
					"Non of these GSTINS are part of GET2a email process {} ",
					recipientGstinsList);
			return;
		}

		EntityInfoEntity entityInfo = entityInfoRepo
				.findByIdAndIsDeleteFalse(entity.getEntityId());

		Pair<String, String> fromToTaxPeriods = getFromToTaxPeriods(
				entity.getNumOfTaxPeriods(), entity.isFinYearGet());

		List<GstrEmailDetailsDto> emailDetailsDtoList = new ArrayList<>();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Get2aAutomation first eamil primaryEmailWiseMap has formed as {} ",
					primaryEmailWiseMap);
		}

		primaryEmailWiseMap.entrySet().forEach(emailGstin -> {

			String primaryEmailId = emailGstin.getKey();
			Pair<Set<String>, Set<String>> value = emailGstin.getValue();

			String notfnCode = null;
			Long count = value.getValue1().stream()
					.filter(automatedActiveGstins::contains).count();

			if (count > 0) {
				notfnCode = "100";
			} else {
				notfnCode = "104";
			}

			GstrEmailDetailsDto emailDto = prepareRequestPayload(primaryEmailId,
					value.getValue0(), value.getValue1(),
					entityInfo.getEntityName(), fromToTaxPeriods.getValue0(),
					fromToTaxPeriods.getValue1(),
					GenUtil.getCurrentFinancialYear(), notfnCode,
					inActiveGstins);

			boolean isSecondEmailEligible = false;
			if (notfnCode.equals("100")) {
				isSecondEmailEligible = true;
			}

			emailDto.setSecondEmailEligible(isSecondEmailEligible);
			emailDto.setActiveGstins(automatedActiveGstins.toString());
			emailDto.setEntityId(entityInfo.getId());

			emailDetailsDtoList.add(emailDto);

		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Get2aAutomation first eamil calling the service method persistAndSendEmail with params dto {} ",
					emailDetailsDtoList);
		}
		emailService.persistAndSendEmail(emailDetailsDtoList, true);

	}

	public Map<String, Pair<Set<String>, Set<String>>> getPrimaryEmailWiseMap(
			List<RecipientMasterUploadEntity> emailMasterData) {

		Map<String, Pair<Set<String>, Set<String>>> primaryEmailWiseMap = new HashMap<>();

		if (emailMasterData != null) {

			String pervPrimayId = null;
			Set<String> secondaryEmailIds = new HashSet<>();
			Set<String> recipientGstins = new HashSet<>();

			for (RecipientMasterUploadEntity master : emailMasterData) {
				// emailMasterData.forEach(master -> {
				if (pervPrimayId == null) {
					pervPrimayId = master.getRecipientPrimEmailId();
				}
				String primayId = master.getRecipientPrimEmailId();

				if(primayId == null) {
					LOGGER.error("NULL Primary email Id has come skipping");
					continue;
				}
				
				if (primayId.equalsIgnoreCase(pervPrimayId)) {

					if (master.getRecipientEmailId2() != null)
					secondaryEmailIds.add(master.getRecipientEmailId2());
					if (master.getRecipientEmailId3() != null)
					secondaryEmailIds.add(master.getRecipientEmailId3());
					if (master.getRecipientEmailId4() != null)
					secondaryEmailIds.add(master.getRecipientEmailId4());
					if (master.getRecipientEmailId5() != null)
					secondaryEmailIds.add(master.getRecipientEmailId5());

					if (master.getRecipientGstin() != null)
					recipientGstins.add(master.getRecipientGstin());
				} else {

					Pair<Set<String>, Set<String>> pair = new Pair<>(
							secondaryEmailIds, recipientGstins);
					// grouping one primary email details
					primaryEmailWiseMap.put(pervPrimayId, pair);

					// accoumulating next primary email details
					secondaryEmailIds = new HashSet<>();
					recipientGstins = new HashSet<>();

					if (master.getRecipientEmailId2() != null)
					secondaryEmailIds.add(master.getRecipientEmailId2());
					if (master.getRecipientEmailId3() != null)
					secondaryEmailIds.add(master.getRecipientEmailId3());
					if (master.getRecipientEmailId4() != null)
					secondaryEmailIds.add(master.getRecipientEmailId4());
					if (master.getRecipientEmailId5() != null)
					secondaryEmailIds.add(master.getRecipientEmailId5());

					if (master.getRecipientGstin() != null)
					recipientGstins.add(master.getRecipientGstin());

				}

				if (emailMasterData.indexOf(master) == emailMasterData.size()
						- 1) {
					Pair<Set<String>, Set<String>> pair = new Pair<>(
							secondaryEmailIds, recipientGstins);
					// grouping last primary email details as one
					primaryEmailWiseMap.put(primayId, pair);
				}

			}

			return primaryEmailWiseMap;

		} else {

			return null;
		}
	}

	public GstrEmailDetailsDto prepareRequestPayload(String primaryEmail,
			Set<String> secondaryEmails, Set<String> gstins, String entityName,
			String fromPeriod, String toPeriod, String fy, String notfnCode,
			List<String> inActiveGstins) {

		GstrEmailDetailsDto emailDto = new GstrEmailDetailsDto();
		emailDto.setReturnType(APIConstants.GSTR2A.toUpperCase());
		emailDto.setEntityName(entityName);
		emailDto.setFromTaxPeriod(fromPeriod);
		emailDto.setToTaxPeriod(toPeriod);
		emailDto.setFy(fy);
		emailDto.setPrimaryEmail(primaryEmail);
		emailDto.setSecondaryEmail(
				secondaryEmails.stream().collect(Collectors.toList()));
		emailDto.setNotfnCode(notfnCode);

		List<GstinWiseEmailDto> gstinWiseDto = new ArrayList<GstinWiseEmailDto>();
		gstins.forEach(cgstin -> {

			GstinWiseEmailDto gstinDto = new GstinWiseEmailDto();
			gstinDto.setCgstin(cgstin);

			gstinDto.setScheduleDate(EYDateUtil.toISTDateTimeFromUTC(LocalDate.now())
					.toString());
			
			String generationDate=EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now()).toString();
			generationDate = generationDate.substring(0, 19);
			generationDate = generationDate.replace("T", " ");
			gstinDto.setGenerationDate(generationDate);
			String get2aStatus = null;
			if (notfnCode.equals("100") || notfnCode.equals("104")) {
				// Primary email
				get2aStatus = "Initiated";
				if (inActiveGstins.contains(cgstin)) {
					get2aStatus = "Not Initiated. Auth token is Inactive";
				}

				gstinDto.setGetStatus(get2aStatus);
			} else {
				// secondary email

				List<Object[]> batchEntries = batchRepo
						.getBatchesByByGstinAndTaxPerFromToAndReturnType(cgstin,
								fromPeriod, toPeriod,
								APIConstants.GSTR2A.toUpperCase());

				List<SectionWiseDetailsDto> sectionWiseDetails = new ArrayList<SectionWiseDetailsDto>();
				batchEntries.forEach(batch -> {
					SectionWiseDetailsDto sectionDto = new SectionWiseDetailsDto();
					sectionDto.setTaxPeriod(
							batch[0] != null ? String.valueOf(batch[0]) : null);
					sectionDto.setStatus(
							batch[1] != null ? String.valueOf(batch[1]) : null);
					sectionDto.setSection(
							batch[2] != null ? String.valueOf(batch[2]) : null);

					sectionWiseDetails.add(sectionDto);
				});

				gstinDto.setSectionWiseDetails(sectionWiseDetails);

			}

			gstinWiseDto.add(gstinDto);
		});

		emailDto.setGstins(gstinWiseDto);

		return emailDto;

	}

	public Pair<String, String> getFromToTaxPeriods(Integer numOfTaxPeriods,
			boolean isFinYearGet) {

		/**
		 * If Fin Year GET is chosen by the client then the difference in the
		 * months calculation is written below based the functional requirement
		 * documentation as for Oct, Nov and Dec months Get call to initiate
		 * from current finyear and for rest of the months Get call to initiate
		 * from Previous finyear
		 */
		if (isFinYearGet || numOfTaxPeriods == null) {
			int monthValue = LocalDate.now().getMonthValue();
			if (monthValue == 1) {
				numOfTaxPeriods = 10;
			} else if (monthValue == 2) {
				numOfTaxPeriods = 11;
			} else if (monthValue == 3) {
				numOfTaxPeriods = 12;
			} else if (monthValue == 4) {
				numOfTaxPeriods = 13;
			} else if (monthValue == 5) {
				numOfTaxPeriods = 14;
			} else if (monthValue == 6) {
				numOfTaxPeriods = 15;
			} else if (monthValue == 7) {
				numOfTaxPeriods = 16;
			} else if (monthValue == 8) {
				numOfTaxPeriods = 17;
			} else if (monthValue == 9) {
				numOfTaxPeriods = 18;
			} else if (monthValue == 10) {
				numOfTaxPeriods = 7;
			} else if (monthValue == 11) {
				numOfTaxPeriods = 8;
			} else if (monthValue == 12) {
				numOfTaxPeriods = 9;
			}
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");

		String minMonth = LocalDate.now().minusMonths(numOfTaxPeriods - 1)
				.format(formatter);
		String maxMonth = LocalDate.now().format(formatter);

		Pair<String, String> pair = new Pair<>(minMonth, maxMonth);

		return pair;

	}
}
