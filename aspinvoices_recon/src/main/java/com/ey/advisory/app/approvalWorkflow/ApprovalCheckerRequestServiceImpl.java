package com.ey.advisory.app.approvalWorkflow;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.data.entities.client.asprecon.ApprovalRequestStatusLogEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ApprovalCheckerRequestStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ApprovalMakerRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ApprovalMappingRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ApprovalRequestStatusLogsRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ApprovalCheckerRequestServiceImpl")
public class ApprovalCheckerRequestServiceImpl
		implements ApprovalCheckerRequestService {

	@Autowired
	@Qualifier("ApprovalCheckerRequestDaoImpl")
	private ApprovalCheckerRequestDao checkerDao;

	@Autowired
	private ApprovalMappingRepository approvalRepo;

	@Autowired
	private ApprovalCheckerRequestStatusRepository checkerRepo;

	@Autowired
	private ApprovalRequestStatusLogsRepository requestLogRepo;

	@Autowired
	private ApprovalEmailUtility emailService;
	
	@Autowired
	private ApprovalMakerRequestRepository makerRepo;
	
	@Autowired
	private UserCreationRepository userRepo;

	public static final String APPROVAL = "Approval";
	public static final String REJECTION = "Rejection";

	@Override
	public List<ApprovalCheckerStatusSummaryDto> getCheckerRequestData(
			ApprovalCheckerRequestDto dto, String userName)

	{
		List<ApprovalCheckerStatusSummaryDto> respList = new ArrayList<>();

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = " Begin ApprovalCheckerRequestServiceImpl.getRequestSummary ";
				LOGGER.debug(msg);
			}

			respList = checkerDao.getRequestSummary(dto, userName);

			if (LOGGER.isDebugEnabled()) {
				String msg = " END ApprovalCheckerRequestServiceImpl.getRequestSummary ";
				LOGGER.debug(msg, respList);
			}

			if (respList == null || respList.isEmpty())
				return null;

		} catch (Exception ex) {
			if (LOGGER.isDebugEnabled()) {
				String msg = "End ApprovalWorkflowServiceImpl"
						+ ".getChekerMakerSummary";
				LOGGER.debug(msg, ex);
			}
			throw new AppException(ex);
		}

		respList.sort(Comparator
				.comparing(ApprovalCheckerStatusSummaryDto::getRequestId));
		return respList;
	}

	@Override
	public List<ApprovalCheckerGstinsDto> getCheckerGstinsData(String userName,
			Long entityId) {
		List<ApprovalCheckerGstinsDto> gstinList = new ArrayList<>();

		try {

			List<String> gstins = approvalRepo.findCheckerGstins(entityId,
					userName);

			if (gstins == null)
				return null;

			gstinList = gstins.stream()
					.map(o -> new ApprovalCheckerGstinsDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Checker gstins fectched ", gstins);
			}
		} catch (Exception ex) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Error while retrieving gstins");
			}
			throw new AppException(ex);
		}
		return gstinList;
	}

	@Override
	public ApprovalCheckerTabSummaryDto findRequestTabCounts(String userName,
			Long entityId) {
		List<ApprovalCheckerTabSummaryDto> reqTo = new ArrayList<>();

		try {
			List<Object[]> tabSummaryList = checkerRepo
					.findRequestCounts(userName, entityId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Checker tab information fectched ",
						tabSummaryList);
			}

			if (tabSummaryList == null || tabSummaryList.isEmpty()) {
				ApprovalCheckerTabSummaryDto respTo = new ApprovalCheckerTabSummaryDto();

				respTo.setTotalRequests(BigInteger.ZERO.toString());
				respTo.setTotalApproved(BigInteger.ZERO.toString());
				respTo.setTotalPending(BigInteger.ZERO.toString());
				respTo.setTotalRejected(BigInteger.ZERO.toString());
				return respTo;
			}

			reqTo = tabSummaryList.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Error while retrieving gstins");
			}
			throw new AppException(ex);
		}
		return reqTo.get(0);
	}

	private ApprovalCheckerTabSummaryDto convert(Object[] arr) {
		ApprovalCheckerTabSummaryDto reqTo = new ApprovalCheckerTabSummaryDto();

		reqTo.setTotalRequests(arr[1].toString());
		reqTo.setTotalApproved(arr[2].toString());
		reqTo.setTotalPending(arr[3].toString());
		reqTo.setTotalRejected(arr[4].toString());

		return reqTo;
	}

	@Override
	public String submitandRevertRequestAction(String userName,
			ApprovalCheckerActionRequestDto dto) {
		List<ApprovalCheckerSubmitInfoDto> subInfo = dto.getSubmitInfo();
		List<ApprovalRequestStatusLogEntity> finalList = new ArrayList<>();

		Pattern p = Pattern.compile("\\((.*?)\\)");

		boolean isSubmit = dto.isIssubmit();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside ApprovalCheckerRequestServiceImpl.submitandRevertRequestAction ");
		}

		try {
			for (ApprovalCheckerSubmitInfoDto subAction : subInfo) {
				if (isSubmit) {

					if (subAction.getStatus().equalsIgnoreCase("Rejected")) {
						throw new Exception(
								"Action on rejected requests cannot be performed");
					} else
						continue;

				} else {
					Matcher m = p.matcher(subAction.getActionTakenBy());
					String s = null;
					/*while (m.find()) {
						s = m.group(1);
					}*/
					// String s = m.group(1).toString();
					if ((s.equalsIgnoreCase(userName)) && (subAction.getStatus()
							.equalsIgnoreCase("Approved"))) {

						continue;
					} else
						throw new Exception(
								"Revert back action cannot be performed");

				}
			}
			// updating the database
			for (ApprovalCheckerSubmitInfoDto actionDto : subInfo) {
				if (isSubmit) {
					String checkComments = ((actionDto.getCheckComm() != null)
							? (StringUtils.truncate(
									actionDto.getCheckComm().toString(), 5000))
							: null);

					ApprWkflwEmailDetailsDto requestDto = convertToEmailDto(
							actionDto, userName);
					emailService.multiPartEmail(false, null, requestDto);

					checkerRepo.updateStatusActionByComments(
							actionDto.getAction(), userName, checkComments,
							Long.valueOf(actionDto.getReqId().substring(4)));
				} else {
					checkerRepo.updateStatusActionByCommentsForRevert(
							"Pending (reversed)", userName,
							Long.valueOf(actionDto.getReqId().substring(4)));
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Update has been made for request ID {}",
							actionDto.getReqId());
				}
				finalList.add(
						convertToLogEntitySave(actionDto, isSubmit, userName));
			}
			requestLogRepo.saveAll(finalList);

		} catch (Exception ex) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Error while saving the actions");
			}
			throw new AppException(ex);
		}
		return "SUCCESS";

	}

	private ApprovalRequestStatusLogEntity convertToLogEntitySave(
			ApprovalCheckerSubmitInfoDto subInfo, boolean isSubmit,
			String userName) {

		ApprovalRequestStatusLogEntity logDto = new ApprovalRequestStatusLogEntity();

		logDto.setReqId(Long.valueOf(subInfo.getReqId().substring(4)));
		logDto.setActionByName(userName);

		logDto.setCheckComm(
				(subInfo.getCheckComm() != null)
						? (StringUtils.truncate(
								subInfo.getCheckComm().toString(), 5000))
						: null);
		logDto.setActionDateTime(LocalDateTime.now());

		if (isSubmit)
			logDto.setReqStatus(subInfo.getAction());
		else
			logDto.setReqStatus("Reverted");

		return logDto;
	}

	private ApprWkflwEmailDetailsDto convertToEmailDto(
			ApprovalCheckerSubmitInfoDto actionDto, String userName) {

		ApprWkflwEmailDetailsDto emailDto = new ApprWkflwEmailDetailsDto();

		List<Object[]> checkerMakerEmail = makerRepo.findMakerAndCheckerEmails(
				Long.valueOf(actionDto.getReqId().substring(4)));

		Object[] firstObj = checkerMakerEmail.get(0);

		emailDto.setAction(firstObj[5].toString());
		emailDto.setActionEmail(
				userRepo.findEmailByUser(userName).get(0).toString());
		emailDto.setAppUrl(
				"https://digigst-y8nvcqp4f9.dispatcher.us3.hana.ondemand.com/index.html#");
		emailDto.setGstin(firstObj[0].toString());

		if (actionDto.getAction().equalsIgnoreCase("Approved"))
			emailDto.setIdentifier(APPROVAL);
		else
			emailDto.setIdentifier(REJECTION);

		// TODO do we need all the makers email or the maker who has requested
		emailDto.setMakerEmails(firstObj[3].toString());
		emailDto.setRefId(actionDto.getReqId());
		emailDto.setRetType(firstObj[2].toString());
		emailDto.setTaxPeriod(firstObj[1].toString());
		List<String> checkerEmail = new ArrayList<>();
		checkerMakerEmail.forEach(o -> {
			checkerEmail.add(o[4].toString());
		});
		emailDto.setCheckerEmails(String.join(",", checkerEmail));

		return emailDto;
	}
}
