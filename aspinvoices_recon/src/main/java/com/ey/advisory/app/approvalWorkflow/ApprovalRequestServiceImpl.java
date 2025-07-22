package com.ey.advisory.app.approvalWorkflow;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.data.entities.client.asprecon.ApprovalRequestEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ApprovalRequestStatusEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ApprovalCheckerRequestStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ApprovalMakerRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ApprovalMappingRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ApprovalRequestServiceImpl")
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

	@Autowired
	private ApprovalMappingRepository approvalRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private UserCreationRepository userRepo;

	@Autowired
	private ApprovalCheckerRequestStatusRepository statusRepo;

	@Autowired
	private ApprovalMakerRequestRepository requestRepo;

	@Autowired
	private ApprovalMappingRepository mappingRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	private ApprovalEmailUtility emailService;

	@Autowired
	private ApprovalWkflwPdfUtility approvalEmail;

	public static final String REQUESTED = "Requested";

	public static ImmutableMap<String, Integer> dueDateMap = ImmutableMap
			.<String, Integer>builder().put("GSTR1", 11).put("GSTR3B", 20)
			.put("GSTR6", 13).put("GSTR7", 10).put("GSTR9", 30).put("ITC04", 25)
			.build();

	@Override
	public List<ApprovalMakerDataDto> getMakerRequestData(
			ApprovalMakerRequestDto dto, String userName)

	{

		try {

			Long entityId = dto.getEntityId();
			String retType = dto.getRetType();
			List<String> gstin = dto.getGstins();

			List<Object[]> requestData = approvalRepo
					.findByEntityIdANDNameANDRetType(entityId, userName, gstin,
							retType);
			if (requestData.isEmpty()) {
				return null;
			}

			List<ApprovalMakerDataDto> respList = new ArrayList<>();

			Map<Object, List<Object[]>> objMap = requestData.stream()
					.collect(Collectors.groupingBy(o -> o[0]));

			for (String gstn : gstin) {
				List<Object[]> userInfo = objMap.get(gstn);

				if (userInfo == null || userInfo.isEmpty())
					continue;

				List<ApprovalEmailIdDto> checkList = new ArrayList<>();

				userInfo.forEach(mail -> {
					checkList.add(new ApprovalEmailIdDto(mail[1].toString(),
							mail[2].toString()));
				});
				ApprovalMakerDataDto obj = new ApprovalMakerDataDto(gstn,
						checkList);
				obj.setNoOfCheckers(Integer.toString(checkList.size()));
				
				respList.add(obj);

			}

			respList.sort(Comparator.comparing(ApprovalMakerDataDto::getGstin));
			Collections.reverse(respList);
			return respList;
		} catch (Exception ex) {
			if (LOGGER.isDebugEnabled()) {
				String msg = "End ApprovalWorkflowServiceImpl"
						+ ".getChekerMakerSummary";
				LOGGER.debug(msg, ex);
			}
			throw new AppException(ex);
		}

	}

	@Override
	public String submitRequestData(MakerRequestDto reqDto, String userName) {
		List<MakerRequestResponseDto> requestList = null;
		try {
			List<MakerRequestResponseDto> finalSubmitList = new ArrayList<>();
			Long entityId = reqDto.getEntityId();
			requestList = reqDto.getRequestList();
			List<String> gstins = requestList.stream()
					.map(MakerRequestResponseDto::getGstin)
					.collect(Collectors.toList());

			// list of approved and pending actions
			List<String> approvedPendingKeys = requestRepo.findRequestKeys(
					reqDto.getEntityId(), gstins, reqDto.getRetType(),
					reqDto.getTaxPeriod(),
					Arrays.asList("Approved", "Pending", "Pending (reversed)"),
					userName);

			/*
			 * requestList can contains ("Save To GSTN,Sign & File") OR
			 * ("Save To GSTN","Sign & File") OR("Sign & File")
			 * OR("Save To GSTN")
			 */

			if (!approvedPendingKeys.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"the approved/pending list contains the actions -> {} for gstins{},"
									+ "returnType{} and taxPeriod{}",
							approvedPendingKeys, gstins, reqDto.getRetType(),
							reqDto.getTaxPeriod());
				}

				for (MakerRequestResponseDto subDto : requestList) {
					//String reqKeySecond = null;
					String reqKeySaveSign = createReqKey(subDto.getGstin(),
							reqDto.getRetType(), reqDto.getTaxPeriod(),
							"SAVE-SIGN");

					/*String reqKeys = createReqKey(subDto.getGstin(),
							reqDto.getRetType(), reqDto.getTaxPeriod(),
							subDto.getRequestedFor().get(0).toString()
									.substring(0, 4).toUpperCase());
					if (subDto.getRequestedFor().size() == 2) {
						reqKeySecond = createReqKey(subDto.getGstin(),
								reqDto.getRetType(), reqDto.getTaxPeriod(),
								subDto.getRequestedFor().get(1).toString()
										.substring(0, 4).toUpperCase());
					}*/
					if (approvedPendingKeys.contains(reqKeySaveSign))
						continue;

				/*	else if (approvedPendingKeys.contains(reqKeys))
						continue;
					else if (subDto.getRequestedFor().size() == 2
							&& approvedPendingKeys.contains(reqKeySecond))
						continue;*/
					else
						finalSubmitList.add(subDto);
				}

			} else {
				finalSubmitList = requestList;
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("The user request for{}", finalSubmitList);
			}

			if (finalSubmitList.isEmpty()) {
				return "DUPLICATES";
			} else {

				List<ApprovalRequestEntity> saveRequestList = new ArrayList<>();
				List<ApprovalRequestStatusEntity> statusList = new ArrayList<>();
				List<ApprWkflwEmailDetailsDto> finalEmailDto = new ArrayList<>();

				finalSubmitList.forEach(obj -> {
					ApprWkflwEmailDetailsDto emailDto = new ApprWkflwEmailDetailsDto();

					ApprovalRequestEntity requestObj = new ApprovalRequestEntity();

					Long requestId = generateCustomId(entityManager);

					requestObj.setRequestId(requestId);
					emailDto.setRefId(requestId.toString());

					requestObj.setEntityId(Long.valueOf(reqDto.getEntityId()));

					requestObj.setGstin(obj.getGstin());
					emailDto.setGstin(obj.getGstin());

					requestObj.setTaxPeriod(reqDto.getTaxPeriod());
					emailDto.setTaxPeriod(reqDto.getTaxPeriod());

					requestObj.setReturnType(reqDto.getRetType());
					emailDto.setRetType(reqDto.getRetType());
					emailDto.setDueDate(dueDateMap
							.get(requestObj.getReturnType()).toString());

					requestObj.setMakerName(userName);

					requestObj.setMakerEmail(userRepo.findEmailByUser(userName)
							.get(0).toString());
					emailDto.setActionEmail(requestObj.getMakerEmail());
					emailDto.setMakerEmails(requestObj.getMakerEmail());

					requestObj.setMakComments((obj.getMakComments() != null)
							? (StringUtils.truncate(obj.getMakComments(), 5000))
							: null);
	/*				if (obj.getRequestedFor().size() == 2) {*/
					requestObj.setAction("Save To GSTN,Sign & File");
					String reqKey = createReqKey(requestObj.getGstin(),
								reqDto.getRetType(), reqDto.getTaxPeriod(),
								"SAVE-SIGN");
					requestObj.setReqKey(reqKey);
				/*	} else {
						requestObj.setAction(
								obj.getRequestedFor().get(0).toString());
						String reqKey = createReqKey(requestObj.getGstin(),
								reqDto.getRetType(), reqDto.getTaxPeriod(),
								obj.getRequestedFor().get(0).substring(0, 4)
										.toUpperCase());
						requestObj.setReqKey(reqKey);
					}*/
					emailDto.setAction(requestObj.getAction());

					requestObj.setCreatedOn(LocalDateTime.now());

					saveRequestList.add(requestObj);
					List<String> checkEmail = new ArrayList<>();
					obj.getSelCheckers().forEach(o -> {
						ApprovalRequestStatusEntity entity = new ApprovalRequestStatusEntity();
						entity.setRequestId(requestId);
						entity.setReqStatus("Pending");
						int index = o.indexOf('(');
						entity.setCheckerEmail(o.substring(0, index));
						checkEmail.add(entity.getCheckerEmail());
						entity.setCheckerName(
								o.substring(index + 1, o.length() - 1));

						statusList.add(entity);
					});

					emailDto.setCheckerEmails(String.join(",", checkEmail));
					emailDto.setIdentifier(REQUESTED);
					emailDto.setAppUrl(
							"https://digigst-y8nvcqp4f9.dispatcher.us3.hana.ondemand.com/index.html#");
					finalEmailDto.add(emailDto);
				});

				requestRepo.saveAll(saveRequestList);
				statusRepo.saveAll(statusList);
				
				
				if(LOGGER.isDebugEnabled())
				{
					LOGGER.debug("Final Email Dto ",finalEmailDto);
				}
				
				// email service for GSTR1 return type
				for (ApprWkflwEmailDetailsDto emailDto : finalEmailDto) {
					if (emailDto.getRetType().equalsIgnoreCase("GSTR1")) {
						
						if(LOGGER.isDebugEnabled())
						{
							LOGGER.debug("Email is to be sent for dto with values{}",emailDto);
						}
						
						Annexure1SummaryReqDto annexSumryRequest = new Annexure1SummaryReqDto();
						annexSumryRequest.setEntityId(Arrays.asList(entityId));
						Map<String, List<String>> dataSecMap = new HashMap<String, List<String>>();
						dataSecMap.put("GSTIN",
								Arrays.asList(emailDto.getGstin()));

						annexSumryRequest.setDataSecAttrs(dataSecMap);

						annexSumryRequest.setTaxPeriod(emailDto.getTaxPeriod());
						
						if(LOGGER.isDebugEnabled())
						{
							LOGGER.debug("The dto for uploading the snapshot path {}",annexSumryRequest);
						}
						String filePath = null;/*approvalEmail.uploadGSTR1SummPdf(annexSumryRequest,
								Long.valueOf(emailDto.getRefId()));*/
									
						File pdfFile =null;
						emailService.multiPartEmail(true, pdfFile, emailDto);
					}
				}
			}
			return "SUCCESS";
		} catch (Exception e) {
			LOGGER.error("error", e);
			throw new AppException(e);
		}

	}

	@Override
	public List<ApprovalRequestSummaryDto> getRequestSummaryData(
			ApprovalMakerRequestDto dto, String userName) {
		List<ApprovalRequestSummaryDto> respList = new ArrayList<>();

		try {
			List<String> gstin = dto.getGstins();
			List<Object[]> requestStatusList = requestRepo
					.findByNameAndTaxPeriodANDRetTypeAndEntityId(userName,
							dto.getTaxPeriod().toString(), dto.getRetType(),
							Long.valueOf(dto.getEntityId().toString()), gstin);
			if (requestStatusList.isEmpty()) {
				return null;
			} else {

				Map<Object, List<Object[]>> objMap = requestStatusList.stream()
						.collect(Collectors.groupingBy(o -> o[0]));

				for (Object reqId : objMap.keySet()) {

					ApprovalRequestSummaryDto obj = new ApprovalRequestSummaryDto();
					obj.setRequestId("RFA-" + reqId.toString());

					List<Object[]> checkerInfo = objMap.get(reqId);
					Object[] firstObj = checkerInfo.get(0);
					obj.setDateAndTimeOfReq(dateChange(
							firstObj[1].toString().substring(0, 19)));
					obj.setGstin(firstObj[2].toString());
					obj.setActionTakenFor(firstObj[5].toString());
					obj.setStatusOfReq(firstObj[6].toString());

					// EY date util - isd from UTC
					if (firstObj[7] != null
							&& firstObj[7].toString().length() > 19) {
						/* Timestamp timeStamp = (Timestamp) firstObj[7]; */
						obj.setDateAndTimeOfChecker(dateChange(
								firstObj[7].toString().substring(0, 19)));
					}
					if (firstObj[8] != null) {
						obj.setCommentsFromChec(firstObj[8].toString());
					}

					List<ApprovalEmailIdDto> fiCheckList = new ArrayList<>();
					fiCheckList = checkerInfo.stream()
							.map(o -> new ApprovalEmailIdDto(o[3].toString(),
									o[4].toString()))
							.collect(Collectors.toCollection(ArrayList::new));

					obj.setRequestMadeTo(fiCheckList);
					obj.setNoOfRequestMadeTo(fiCheckList.size());
					respList.add(obj);
				}
			}
			respList.sort(Comparator
					.comparing(ApprovalRequestSummaryDto::getRequestId));
			Collections.reverse(respList);
			return respList;

		} catch (Exception ex) {
			if (LOGGER.isDebugEnabled()) {
				String msg = "End ApprovalWorkflowServiceImpl"
						+ ".getChekerMakerSummary";
				LOGGER.debug(msg, ex);
			}
			throw new AppException(ex);
		}

	}

	@Override
	public Object[] getSaveAndSignData(ApprovalMakerRequestDto dto,
			String userName) {
		Object[] obj = new Object[3];
		obj[0] = true;
		obj[1] = true;
		obj[2] = true;

		Long entityId = dto.getEntityId();
		String gstin = dto.getGstins().get(0).toString();
		String retType = dto.getRetType();

		// opted or not opted

		boolean optForApproval = true;
		boolean returnTypeOpted = true;

		List<Long> entityIdOpt = entityConfigPemtRepo
				.getAllEntitiesOpted2B(Arrays.asList(entityId), "I39");

		List<Long> rtTypeEntityId = entityConfigPemtRepo
				.getAllEntitiesOpted2B(Arrays.asList(entityId), "I40");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"opted for checker maker entityId{} and opted for return types {} ",
					entityIdOpt, rtTypeEntityId);
		}

		if (entityIdOpt.isEmpty())
			optForApproval = false;
		if (rtTypeEntityId.isEmpty())
			returnTypeOpted = false;

		/*
		 * Object[] chdEntity = mappingRepo.findisMakerCheckerNames(entityId,
		 * gstin, retType, userName);
		 */
		Boolean isMaker = mappingRepo.findisMakerCheckerNames(entityId, gstin,
				retType, userName);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"findisMakerCheckerNames query return the values {} "
							+ "for username {} requested for gstin{} and returntype{}",
					isMaker.toString(), userName, gstin, retType);
		}

		if (optForApproval && returnTypeOpted) {
	
			if (isMaker == null) {
				obj[1] = false;
				obj[2] = false;
				return obj;
			}
	
			if (isMaker) {
				List<String> approvedKeys = requestRepo.findRequestKey(
						dto.getEntityId(), dto.getGstins(), dto.getRetType(),
						dto.getTaxPeriod(), Arrays.asList("Approved"));
				if (approvedKeys.isEmpty()) {
					obj[1] = false;
					obj[2] = false;
					return obj;
				}
				/*if (approvedKeys.size() == 1 && !approvedKeys.get(0).toString()
						.equalsIgnoreCase(createReqKey(gstin, dto.getRetType(),
								dto.getTaxPeriod(), "SAVE-SIGN"))) {

					if (approvedKeys.get(0).toString().equalsIgnoreCase(
							createReqKey(gstin, dto.getRetType(),
									dto.getTaxPeriod(), "SAVE"))) {
						obj[2] = false;
					} else {
						obj[1] = false;
					}
				}*/
			}
		} else {
			obj[0] = false;
		}
		return obj;
	}

	private static Long generateCustomId(EntityManager entityManager) {
		String reportId = "";
		String digits = "";
		Long nextSequencevalue = getNextSequencevalue(entityManager);

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		String currentDate = currentYear + (currentMonth < 10
				? ("0" + currentMonth) : String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT CHECKER_MAKER_SEQ.nextval " + " FROM DUMMY";
		Query query = entityManager.createNativeQuery(queryStr);
		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}

	private static String createReqKey(String gstin, String retType,
			String taxPeriod, String action) {
		String reqKey = String.format("%s|%s|%s|%s", gstin, taxPeriod, retType,
				action);
		return reqKey;
	}

	public String dateChange(String oldDate) {
		DateTimeFormatter formatter = null;
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		LocalDateTime dateTimes = LocalDateTime.parse(oldDate, formatter);
		LocalDateTime dateTimeFormatter = EYDateUtil
				.toISTDateTimeFromUTC(dateTimes);
		DateTimeFormatter FOMATTER = DateTimeFormatter
				.ofPattern("dd-MM-yyyy : HH:mm:ss");
		String newdate = FOMATTER.format(dateTimeFormatter);
		return newdate;

	}

}
