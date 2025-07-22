package com.ey.advisory.app.data.services.drc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.common.GstinWiseEmailDto;
import com.ey.advisory.app.common.GstrEmailDetailsDto;
import com.ey.advisory.app.data.entities.client.RecipientMasterUploadEntity;
import com.ey.advisory.app.data.entities.drc.TblDrc01AutoGetCallDetails;
import com.ey.advisory.app.data.entities.drc.TblDrcCommRequestDetails;
import com.ey.advisory.app.data.repositories.client.DongleMappingRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.RecipientMasterUploadRepository;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.data.repositories.client.drc.Drc01AutoGetCallDetailsRepository;
import com.ey.advisory.app.data.repositories.client.drc.Drc01RequestCommDetailsRepository;
import com.ey.advisory.app.services.jobs.gstr1.GetBatchPayloadHandler;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("DRC01EmailTrigerringHandler")
public class DRC01EmailTrigerringHandler {

	@Autowired
	private DrcGstnServiceImpl drcGstnGetCall;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("Drc01AutoGetCallDetailsRepository")
	private Drc01AutoGetCallDetailsRepository drc01AutoGetCallDetailsRepo;

	@Autowired
	@Qualifier("RecipientMasterUploadRepository")
	private RecipientMasterUploadRepository masterUploadRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("Drc01RequestCommDetailsRepository")
	private Drc01RequestCommDetailsRepository drc01RequestCommDetailsRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Drc01CommEmailServiceImpl")
	private Drc01CommEmailServiceImpl drc01CommEmailService;

	@Autowired
	private GetBatchPayloadHandler batchPayloadHelper;

	@Autowired
	private GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Autowired
	private DongleMappingRepository dongleRepo;

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm");

	public void triggerEmails(TblDrc01AutoGetCallDetails entity,
			int currentDateOfMonth, int reminderNumber) {

		JsonObject jobParams = new JsonObject();

		RecipientMasterUploadEntity drc01bOptedEntry = null;

		RecipientMasterUploadEntity drc01cOptedEntry = null;
		List<GstrEmailDetailsDto> drc01bList = new ArrayList<>();
		List<GstrEmailDetailsDto> drc01cList = new ArrayList<>();
		String gstin = entity.getGstin();
		String taxPeriod = entity.getTaxPeriod();
		String fy = GenUtil.getFinancialYearByTaxperiod(taxPeriod);

		String emailType = entity.getEmailType();
		
		switch(reminderNumber)
		{
		case 0:
			emailType = "Original";
			break;
		case 1:
			emailType = "Reminder 1";
			break;
		case 2:
			emailType = "Reminder 2";
			break;
		case 3:
			emailType = "Reminder 3";
			break;
		}
		
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Starting the execution for emailTriggerMethod in Handler for triggering emails for entity {} ",
					entity));
		}

		if ("DRC01B".equalsIgnoreCase(entity.getCommType())) {
			drc01bOptedEntry = masterUploadRepo
					.findByRecipientGstinAndIsDeleteFalseAndIsDRC01BEmailTrue(
							entity.getGstin());
			// WHETHER IT IS ORIGINAL OR REMINDER
			// if recipient gstin is null for particular email type
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						"Starting the execution in Handler for by taking drc01bOptedEntry {} ",
						drc01bOptedEntry));
			}

			if (drc01bOptedEntry == null) {
				LOGGER.error(
						"Not opted for email triggering for gstin {} and taxperiod {}",
						entity.getGstin(), entity.getTaxPeriod());
				drc01AutoGetCallDetailsRepo.updateRemarksAndIsOpted(
						"DRC01B email not opted", "N", "N",entity.getId());
				return;
			}

			if (drc01bOptedEntry != null) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Starting the execution in Handler incase drc01bOptedEntry {} is not null",
							drc01bOptedEntry));
				}

				List<String> primaryEmails = extractEmails(drc01bOptedEntry,
						true);

				int primaryEmailsCount = primaryEmails.size();
				String primaryEmail = String.join(",", primaryEmails);

				List<String> secondaryEmails = extractEmails(drc01bOptedEntry,
						false);

				int secondaryEmailsCount = secondaryEmails.size();

				if ((primaryEmailsCount ==0 && secondaryEmailsCount == 0)) {
					saveDrcCommRequestDetails(taxPeriod, gstin, null, 0,
							emailType, entity.getEntityId(),
							entity.getCommType());

					drc01AutoGetCallDetailsRepo.updateRemarksAndIsOpted(
							"emails not provided", "Y", "N", entity.getId());
				}

				List<GstinWiseEmailDto> cgstinsList = new ArrayList<>();
				GstinWiseEmailDto gstinDto = new GstinWiseEmailDto();
				gstinDto.setCgstin(entity.getGstin());
				cgstinsList.add(gstinDto);

				GstrEmailDetailsDto dto = new GstrEmailDetailsDto();
				dto.setReturnType(APIConstants.DRC01B.toUpperCase());
				dto.setFromTaxPeriod(taxPeriod);
				dto.setToTaxPeriod(taxPeriod);
				dto.setFy(fy);
				// when primary and secondary email is null have to add
				// conditions

				if (primaryEmail != null) {
					dto.setPrimaryEmail(primaryEmail);
				}
				if (secondaryEmails != null) {
					dto.setSecondaryEmail(secondaryEmails);
				}
				dto.setGstins(cgstinsList);
				if (reminderNumber == 0) {
					dto.setNotfnCode("600");
				} else if (reminderNumber == 1) {
					dto.setNotfnCode("601");
				} else if (reminderNumber == 2) {
					dto.setNotfnCode("602");
				} else if (reminderNumber == 3) {
					dto.setNotfnCode("603");
				}

				drc01bList.add(dto);

				String status = drc01CommEmailService.persistAndSendDrc01Email(
						drc01bList, entity, reminderNumber);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Email trigger completed for DRC01B status {} ", status));
				}

				int totalCount = primaryEmailsCount + secondaryEmailsCount;

				TblDrcCommRequestDetails requestEntity = saveDrcCommRequestDetails(
						taxPeriod, gstin, status, totalCount, emailType,
						entity.getEntityId(), entity.getCommType());

				// have to save the requestEntity first

				requestEntity = drc01RequestCommDetailsRepo.save(requestEntity);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Saved the data in request comm table requestEntity {} ",
							requestEntity));
				}

				if (reminderNumber == 0) {

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Starting the job Post for DRC01B reminderNumber {} ",
								reminderNumber));
					}

					Long requestId = requestEntity.getRequestId();

					jobParams.addProperty("requestId", requestId);

					jobParams.addProperty("id", entity.getId());
					
					User user = SecurityContext.getUser();
					String userName = user.getUserPrincipalName() != null
							? user.getUserPrincipalName() : "SYSTEM";
					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.DRC01B_COMM_REPORT_DOWNLOAD,
							jobParams.toString(), userName, 1L, null, null);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Completed the job Post for DRC01B",
								reminderNumber));
					}
				} else {

					Long requestId = requestEntity.getRequestId();
					Optional<TblDrcCommRequestDetails> reqStatusEntity = drc01RequestCommDetailsRepo
							.findByGstinAndTaxPeriodAndCommTypeAndEmailType(
									entity.getGstin(), entity.getTaxPeriod(),
									"DRC01B", "Original");

					if (reqStatusEntity.isPresent()
							&& reqStatusEntity.get().getReportStatus()
									.equalsIgnoreCase("REPORT_GENERATED")) {

						drc01RequestCommDetailsRepo.updateStatus(requestId,
								reqStatusEntity.get().getReportStatus(),
								reqStatusEntity.get().getFilePath(),
								LocalDateTime.now(),
								reqStatusEntity.get().getDocId());

					} else {

						jobParams.addProperty("requestId", requestId);

						jobParams.addProperty("id", entity.getId());

						User user = SecurityContext.getUser();
						String userName = user.getUserPrincipalName() != null
								? user.getUserPrincipalName() : "SYSTEM";
						asyncJobsService.createJob(TenantContext.getTenantId(),
								JobConstants.DRC01B_COMM_REPORT_DOWNLOAD,
								jobParams.toString(), userName, 1L, null, null);

					}

				}

			}

		} else {
			drc01cOptedEntry = masterUploadRepo
					.findByRecipientGstinAndIsDeleteFalseAndIsDRC01CEmailTrue(
							entity.getGstin());

			if (drc01cOptedEntry == null) {
				LOGGER.error(
						"Not opted for email triggering for gstin {} and taxperiod {}",
						entity.getGstin(), entity.getTaxPeriod());
				drc01AutoGetCallDetailsRepo.updateRemarksAndIsOpted(
						"DRC01C email not opted", "N", "N",entity.getId());

				return;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						"Starting the execution in Handler for by taking drc01cOptedEntry {} ",
						drc01cOptedEntry));
			}

			// WHETHER IT IS ORIGINAL OR REMINDER
			if (drc01cOptedEntry != null) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Starting the execution in Handler incase drc01cOptedEntry {} is not null",
							drc01cOptedEntry));
				}

				List<String> primaryEmails = extractEmails(drc01cOptedEntry,
						true);
				int primaryEmailsCount = primaryEmails.size();
				String primaryEmail = String.join(",", primaryEmails);
				List<String> secondaryEmails = extractEmails(drc01cOptedEntry,
						false);
				int secondaryEmailsCount = secondaryEmails.size();

				if ((primaryEmailsCount == 0 && secondaryEmailsCount == 0)) {
					saveDrcCommRequestDetails(taxPeriod, gstin, null, 0,
							emailType, entity.getEntityId(),
							entity.getCommType());
					drc01AutoGetCallDetailsRepo.updateRemarksAndIsOpted(
							"emails not provided", "N", "Y",entity.getId());

				}

				List<GstinWiseEmailDto> cgstinsList = new ArrayList<>();
				GstinWiseEmailDto gstinDto = new GstinWiseEmailDto();
				gstinDto.setCgstin(entity.getGstin());
				cgstinsList.add(gstinDto);

				GstrEmailDetailsDto dto = new GstrEmailDetailsDto();
				dto.setReturnType(APIConstants.DRC01C.toUpperCase());
				dto.setFromTaxPeriod(taxPeriod);
				dto.setToTaxPeriod(taxPeriod);
				dto.setFy(fy);
				if (primaryEmail != null) {
					dto.setPrimaryEmail(primaryEmail);
				}
				if (secondaryEmails != null) {
					dto.setSecondaryEmail(secondaryEmails);
				}
				dto.setGstins(cgstinsList);
				if (reminderNumber == 0) {
					dto.setNotfnCode("600");
				} else if (reminderNumber == 1) {
					dto.setNotfnCode("601");
				} else if (reminderNumber == 2) {
					dto.setNotfnCode("602");
				} else if (reminderNumber == 3) {
					dto.setNotfnCode("603");
				}

				drc01cList.add(dto);

				String status = drc01CommEmailService.persistAndSendDrc01Email(
						drc01cList, entity, reminderNumber);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Email trigger completed for DRC01C status {} ", status));
				}

				int totalCount = primaryEmailsCount + secondaryEmailsCount;

				TblDrcCommRequestDetails requestEntity = saveDrcCommRequestDetails(
						taxPeriod, gstin, status, totalCount, emailType,
						entity.getEntityId(), entity.getCommType());

				requestEntity = drc01RequestCommDetailsRepo.save(requestEntity);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Saved the data in request comm table for DRC01C requestEntity {} ",
							requestEntity));
				}

				if (reminderNumber == 0) {

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Starting the job Post for DRC01C reminderNumber {} ",
								reminderNumber));
					}

					Long requestId = requestEntity.getRequestId();

					jobParams.addProperty("requestId", requestId);
					jobParams.addProperty("id", entity.getId());

					User user = SecurityContext.getUser();
					String userName = user.getUserPrincipalName() != null
							? user.getUserPrincipalName() : "SYSTEM";
					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.DRC01C_COMM_REPORT_DOWNLOAD,
							jobParams.toString(), userName, 1L, null, null);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Completed the job Post for DRC01C reminderNumber {} ",
								reminderNumber));
					}
				} else {

					Long requestId = requestEntity.getRequestId();
					Optional<TblDrcCommRequestDetails> reqStatusEntity = drc01RequestCommDetailsRepo
							.findByGstinAndTaxPeriodAndCommTypeAndEmailType(
									entity.getGstin(), entity.getTaxPeriod(),
									"DRC01C", "Original");

					if (reqStatusEntity.isPresent()
							&& reqStatusEntity.get().getReportStatus()
									.equalsIgnoreCase("REPORT_GENERATED")) {

						drc01RequestCommDetailsRepo.updateStatus(requestId,
								reqStatusEntity.get().getReportStatus(),
								reqStatusEntity.get().getFilePath(),
								LocalDateTime.now(),
								reqStatusEntity.get().getDocId());

					} else {

						jobParams.addProperty("requestId", requestId);

						jobParams.addProperty("id", entity.getId());
						
						User user = SecurityContext.getUser();
						String userName = user.getUserPrincipalName() != null
								? user.getUserPrincipalName() : "SYSTEM";
						asyncJobsService.createJob(TenantContext.getTenantId(),
								JobConstants.DRC01C_COMM_REPORT_DOWNLOAD,
								jobParams.toString(), userName, 1L, null, null);

					}

				}
			}

		}

	}

	private String getStandardTime(LocalDateTime dateStr) {
		LocalDateTime ist = EYDateUtil.toISTDateTimeFromUTC(dateStr);
		String formattedDate = formatter1.format(ist).toString();
		return formattedDate;
	}

	private List<String> extractEmails(RecipientMasterUploadEntity entry,
			boolean isPrimary) {
		List<String> emails = new ArrayList<>();
		if (isPrimary) {
			if (entry.getRecipientPrimEmailId() != null) {
				emails.add(entry.getRecipientPrimEmailId());
			}
			if (entry.getRecipientEmailId2() != null) {
				emails.add(entry.getRecipientEmailId2());
			}
			if (entry.getRecipientEmailId3() != null) {
				emails.add(entry.getRecipientEmailId3());
			}
			if (entry.getRecipientEmailId4() != null) {
				emails.add(entry.getRecipientEmailId4());
			}
			if (entry.getRecipientEmailId5() != null) {
				emails.add(entry.getRecipientEmailId5());
			}
		} else {
			if (entry.getCceEmailId1() != null) {
				emails.add(entry.getCceEmailId1());
			}
			if (entry.getCceEmailId2() != null) {
				emails.add(entry.getCceEmailId2());
			}
			if (entry.getCceEmailId3() != null) {
				emails.add(entry.getCceEmailId3());
			}
			if (entry.getCceEmailId4() != null) {
				emails.add(entry.getCceEmailId4());
			}
			if (entry.getCceEmailId5() != null) {
				emails.add(entry.getCceEmailId5());
			}
		}
		return emails;
	}

	private TblDrcCommRequestDetails saveDrcCommRequestDetails(String taxPeriod,
			String gstin, String status, int totalCount, String emailType,
			Long entityId, String commType) {
		TblDrcCommRequestDetails requestEntity = new TblDrcCommRequestDetails();
		String formattedTime = getStandardTime(LocalDateTime.now());
		requestEntity.setCreatedOn(formattedTime);
		requestEntity.setReportStatus(ReportStatusConstants.INITIATED);
		requestEntity.setTaxPeriod(taxPeriod);
		requestEntity.setGstin(gstin);
		requestEntity.setCommType(commType);
		requestEntity.setEmailType(emailType);
		requestEntity.setEntityId(entityId);
		requestEntity.setCreatedBy("SYSTEM");
		requestEntity.setDerivedReturnPeriod(
				Integer.valueOf(GenUtil.getDerivedTaxPeriod(taxPeriod)));

		if ("Success".equalsIgnoreCase(status)) {
			requestEntity.setEmailStatus(totalCount + "/" + totalCount);
		} else if ("Failed".equalsIgnoreCase(status)) {
			requestEntity.setEmailStatus(0 + "/" + totalCount);
		}

		if (totalCount == 0) {
			requestEntity.setEmailStatus(0 + "/" + 0);
		}

		return drc01RequestCommDetailsRepo.save(requestEntity);
	}
}