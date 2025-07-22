package com.ey.advisory.app.services.feedback;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.FeedbackConfigAnswerEntity;
import com.ey.advisory.core.async.domain.master.FeedbackConfigQuestionEntity;
import com.ey.advisory.core.async.domain.master.FeedbackUserConfigPrmtEntity;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.repositories.master.FeedbackConfigAnswerRepository;
import com.ey.advisory.core.async.repositories.master.FeedbackConfigQuestionRepository;
import com.ey.advisory.core.async.repositories.master.FeedbackUserConfigPrmtRepository;
import com.ey.advisory.core.async.repositories.master.GroupRepository;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("FeedbackSurveyServiceServiceImpl")
public class FeedbackSurveyServiceServiceImpl implements FeedbackSurveyService {

	@Autowired
	FeedbackConfigQuestionRepository feedbackConfigRepo;

	@Autowired
	FeedbackConfigAnswerRepository feedbackConfigAnswerRepo;

	@Autowired
	FeedbackUserConfigPrmtRepository userPrmtRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	private GroupRepository groupRepository;

	@PersistenceContext(unitName = "masterDataUnit")
	private EntityManager entityManager;

	private static final String RADIO = "R";
	private static final String TEXTAREA = "TA";
	private static final String TEXTAREAFILE = "TAF";
	private static final String RATING = "RA";
	private static final String MULTIPLE = "M";
	private static final String RATINGTXTAREA = "RAT";
	private static final String FEEDBACK_SURVEY_FILES = "FeedbackSurveyFiles";
	private static final String Q1 = "Q1";
	private static final String Q3 = "Q3";

	private static Map<String, String> getMultipleMap() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("A", "ITC-04 return filing");
		map.put("B", "Role based access for E-invoice / Return Module");
		map.put("C",
				"TAX/NIL/NON/EXT bifurcation to respective tables of GSTR 1 basis the supply type against line items");
		map.put("D",
				"Report for Exports transactions without shipping bill details");
		map.put("E", "Late reported invoices");
		map.put("F",
				"GSTR-2B attributes in GSTR 2A vs PR reconciliation reports");
		map.put("G", "Imports reconciliation");
		map.put("H", "Exclude vendors / records from reconciliation");
		map.put("I", "Daily recon module with payment blocking");
		map.put("J", "E-invoice QR code validator for vendor invoices");
		return map;
	}

	@Override
	public List<FDSurveyGetRespDto> fetchSurveyRecords(String userName) {
		try {
			List<FDSurveyGetRespDto> resDtos = new ArrayList<>();

			getConfigPermits(resDtos, userName);
			Comparator<FDSurveyGetRespDto> byQuesCode = Comparator
					.comparing(FDSurveyGetRespDto::getQuesCode);
			List<FDSurveyGetRespDto> sortedList = resDtos.stream()
					.sorted(byQuesCode).collect(Collectors.toList());
			return sortedList;
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while Fetching the Feedback for Username %s",
					userName);
			LOGGER.error(errMsg);
			throw new AppException(errMsg, e);
		}
	}

	private void getConfigPermits(List<FDSurveyGetRespDto> resDtos,
			String userName) {
		try {
			List<Object[]> configPermitEntities = getConfgParmtr();
			Map<String, Map<KeyType, List<AttributeValue>>> attributeValueMap = configPermitEntities
					.stream()
					.collect(Collectors.groupingBy(obj -> (String) obj[0],
							Collectors.groupingBy(obj -> createKeyType(obj),
									Collectors.mapping(
											obj -> createAttributeValue(
													obj),
											Collectors.toList()))));

			attributeValueMap.forEach((keyType, attributeMap) -> {
				switch (keyType) {
				case RATING:
					handleRatingKey(attributeMap, resDtos, userName);
					break;
				case TEXTAREA:
				case TEXTAREAFILE:
					handleTextareaKey(attributeMap, resDtos, userName, keyType);
					break;
				case RADIO:
				case MULTIPLE:
					handleRadioOrMultipleKey(attributeMap, resDtos, userName,
							keyType);
					break;
				default:
					String errMsg = String.format(
							"UnKnown Key Type Found, Key Type is %s", keyType);
					LOGGER.error(errMsg);
					break;
				}
			});

		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while Fetching the Feedback for Username %s",
					userName);
			LOGGER.error(errMsg);
			throw new AppException(errMsg, e);
		}
	}

	private KeyType createKeyType(Object[] obj) {
		Long value1 = obj[1] != null ? Long.valueOf(String.valueOf(obj[1]))
				: null;
		Long value2 = obj[2] != null ? Long.valueOf(String.valueOf(obj[2]))
				: null;
		String value3 = obj[3] != null ? String.valueOf(obj[3]) : null;
		return new KeyType(value1, value2, value3);
	}

	private AttributeValue createAttributeValue(Object[] obj) {
		String value4 = (String) obj[4];
		String value5 = (String) obj[5];
		return new AttributeValue(value4, value5);
	}

	private void handleRatingKey(Map<KeyType, List<AttributeValue>> keyTypeMap,
			List<FDSurveyGetRespDto> resDtos, String userName) {
		keyTypeMap.forEach((pairParameterKey, pairParameterValues) -> {
			FDSurveyGetRespDto resDto = new FDSurveyGetRespDto();
			resDto.setKeyType(RATING);
			resDto.setQuesId(pairParameterKey.getValue0());
			resDto.setSequenceId(pairParameterKey.getValue1().intValue());
			resDto.setQues(pairParameterKey.getValue2());
			String questionCode = feedbackConfigRepo
					.findQuestionCode(pairParameterKey.getValue0());
			resDto.setQuesCode(questionCode);
			List<FDSurveyItemsRespDto> list = new ArrayList<>();
			List<FeedbackConfigQuestionEntity> ratingTxtList = feedbackConfigRepo
					.findByQuestionTypeAndIsActiveTrue(RATINGTXTAREA);
			ratingTxtList.forEach(ratingTxt -> {
				String answerDesc = userPrmtRepo
						.findByGroupCodeAndUserIdAndConfgPrmtId(
								TenantContext.getTenantId(), userName,
								ratingTxt.getId());
				FDSurveyItemsRespDto ansDto = new FDSurveyItemsRespDto();
				ansDto.setAnswerDesc(answerDesc);
				ansDto.setQuesId(ratingTxt.getId());
				ansDto.setQues(ratingTxt.getQuestionDescription());
				list.add(ansDto);
			});
			resDto.setItems(list);
			resDtos.add(resDto);
		});
	}

	private void handleTextareaKey(
			Map<KeyType, List<AttributeValue>> keyTypeMap,
			List<FDSurveyGetRespDto> resDtos, String userName, String keyType) {
		keyTypeMap.forEach((pairParameterKey, pairParameterValues) -> {
			FDSurveyGetRespDto resDto = new FDSurveyGetRespDto();
			resDto.setKeyType(keyType);
			resDto.setQuesId(pairParameterKey.getValue0());
			resDto.setSequenceId(pairParameterKey.getValue1().intValue());
			resDto.setQues(pairParameterKey.getValue2());
			String questionCode = feedbackConfigRepo
					.findQuestionCode(pairParameterKey.getValue0());
			resDto.setQuesCode(questionCode);
			Optional<FeedbackUserConfigPrmtEntity> userPrmEntity = userPrmtRepo
					.findByGroupCodeAndUserNameAndConfgPrmtIdAndIsDeleteFalse(
							TenantContext.getTenantId(), userName,
							pairParameterKey.getValue0());
			userPrmEntity.ifPresent(entity -> {
				if (!Strings.isNullOrEmpty(entity.getAnswer())) {
					resDto.setAnswerDesc(entity.getAnswer());
				}
				if (TEXTAREAFILE.equalsIgnoreCase(keyType)) {
					resDto.setFileUpload(true);
				}
			});
			resDtos.add(resDto);
		});
	}

	private void handleRadioOrMultipleKey(
			Map<KeyType, List<AttributeValue>> keyTypeMap,
			List<FDSurveyGetRespDto> resDtos, String userName, String keyType) {
		keyTypeMap.forEach((pairParameterKey, pairParameterValues) -> {
			FDSurveyGetRespDto resDto = new FDSurveyGetRespDto();
			resDto.setKeyType(keyType);
			resDto.setQuesId(pairParameterKey.getValue0());
			resDto.setSequenceId(pairParameterKey.getValue1().intValue());
			resDto.setQues(pairParameterKey.getValue2());
			String questionCode = feedbackConfigRepo
					.findQuestionCode(pairParameterKey.getValue0());
			resDto.setQuesCode(questionCode);
			List<FDSurveyItemsRespDto> list = new ArrayList<>();
			List<String> answerCodes = new ArrayList<>();
			pairParameterValues.forEach(pairParameterValue -> {
				FDSurveyItemsRespDto ansDto = new FDSurveyItemsRespDto();
				ansDto.setAnswerCode(pairParameterValue.getValue0());
				if (!Strings.isNullOrEmpty(pairParameterValue.getValue1())) {
					ansDto.setAnswerDesc(pairParameterValue.getValue1());
				}
				if (pairParameterValue.getValue0() != null) {
					answerCodes.add(pairParameterValue.getValue0());
				}
				list.add(ansDto);
			});
			answerCodes.sort(Comparator.naturalOrder());
			String selectedAnswer = userPrmtRepo
					.findByGroupCodeAndUserIdAndConfgPrmtId(
							TenantContext.getTenantId(), userName,
							pairParameterKey.getValue0());
			if (!Strings.isNullOrEmpty(selectedAnswer)) {
				resDto.setAnswerDesc(selectedAnswer);
			}
			list.sort(
					Comparator.comparing(FDSurveyItemsRespDto::getAnswerCode));
			resDto.setItems(list);
			resDtos.add(resDto);
		});
	}

	private List<Object[]> getConfgParmtr() {
		List<Object[]> obj = new ArrayList<>();
		String sql = createQueryString();
		Query query = entityManager.createNativeQuery(sql);
		obj = query.getResultList();
		return obj;
	}

	private String createQueryString() {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"SELECT cq.question_type,cq.id,cq.SEQUENCE_ID,cq.question_description,");
		sb.append("ca.answer_options,ca.answer_options_description ");
		sb.append("FROM FD_CONFG_QUESTION cq LEFT JOIN ");
		sb.append("FD_CONFG_ANSWER ca on cq.id=ca.fd_confg_question_id ");
		sb.append("WHERE is_active=true ");
		sb.append("order by cq.QUESTION_CODE,cq.SEQUENCE_ID asc");
		return sb.toString();
	}

	@Override
	public String saveSurveyRecords(FDSurveyRespDto respDto, MultipartFile file,
			File tempDir) {
		try {
			List<FeedbackUserConfigPrmtEntity> userPrmtList = new ArrayList<>();
			String tenantId = TenantContext.getTenantId();
			String userName = respDto.getUserName();

			for (FDSurveyGetRespDto resultDto : respDto.getResults()) {
				String keyType = resultDto.getKeyType();

				if (RATING.equalsIgnoreCase(keyType)) {
					createRAUserPrmtEntities(resultDto, tenantId, userName,
							userPrmtList);
				} else {
					FeedbackUserConfigPrmtEntity userPrmt = createUserPrmtEntity(
							resultDto, keyType, file, tempDir, tenantId,
							userName);
					if (userPrmt != null) {
						userPrmtList.add(userPrmt);
					}

				}
			}
			if (!userPrmtList.isEmpty()) {
				userPrmtRepo.updateFeedbackDtls(tenantId, userName);
				userPrmtRepo.saveAll(userPrmtList);
			}
			String groupCode = TenantContext.getTenantId();
			Group group = groupRepository
					.findByGroupCodeAndIsActiveTrue(groupCode);
			String groupName = group.getGroupName();
			LocalDateTime ldt = EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now());
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd.MM.yyyy| HH:mm:ss");
			String emailTriggeredOn = (formatter.format(ldt)).toString();

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("groupCode", groupCode);
			jobParams.addProperty("groupName", groupName);
			jobParams.addProperty("emailTriggeredOn", emailTriggeredOn);

//			asyncJobsService.createJob(TenantContext.getTenantId(),
//					JobConstants.SAP_EMAIL_NOTIFICATION, jobParams.toString(),
//					userName, 5L, 0L, 0L);
			
			return "Success";
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while saving the Feedback for Username %s",
					respDto.getUserName());
			LOGGER.error(errMsg, e);
			return "Failed";
		}
	}

	private void createRAUserPrmtEntities(FDSurveyGetRespDto resultDto,
			String tenantId, String userName,
			List<FeedbackUserConfigPrmtEntity> userPrmtList) {
		for (FDSurveyItemsRespDto itemDto : resultDto.getItems()) {
			FeedbackUserConfigPrmtEntity userPrmt = createUserPrmtEntity(
					resultDto, null, null, null, tenantId, userName);
			userPrmt.setConfgPrmtId(itemDto.getQuesId());
			userPrmt.setAnswer(itemDto.getAnswerDesc());
			userPrmtList.add(userPrmt);
		}
	}

	private FeedbackUserConfigPrmtEntity createUserPrmtEntity(
			FDSurveyGetRespDto resultDto, String keyType, MultipartFile file,
			File tempDir, String tenantId, String userName) {
		FeedbackUserConfigPrmtEntity userPrmt = new FeedbackUserConfigPrmtEntity();
		userPrmt.setDelete(false);
		userPrmt.setQuestionCode(resultDto.getQuesCode());
		userPrmt.setGroupCode(tenantId);
		userPrmt.setCreatedOn(LocalDateTime.now());
		userPrmt.setCreatedBy(userName);
		userPrmt.setUserName(userName);
		userPrmt.setConfgPrmtId(resultDto.getQuesId());

		if (TEXTAREAFILE.equalsIgnoreCase(keyType)) {
			if (file != null) {
				String filePath = GenUtil.uploadFile(file, tempDir,
						FEEDBACK_SURVEY_FILES);
				userPrmt.setFileName(file.getOriginalFilename());
				userPrmt.setFilePath(filePath);
			}
		}
		userPrmt.setAnswer(resultDto.getAnswerDesc());
		return userPrmt;
	}

	@Override
	public List<FDSurveyRespDto> getAllUserFeedbackSvyDtls(String groupCode) {
		List<FeedbackUserConfigPrmtEntity> userDtls = userPrmtRepo
				.findByIsDeleteFalse();

		Map<String, String> getMultipleMap = feedbackConfigAnswerRepo
				.findByQuestionType(MULTIPLE).stream()
				.collect(Collectors.toMap(
						FeedbackConfigAnswerEntity::getAnswerOptions,
						FeedbackConfigAnswerEntity::getAnswerOptionsDesc));

		Map<String, Map<String, List<FeedbackUserConfigPrmtEntity>>> groupedEntities = userDtls
				.stream()
				.collect(Collectors.groupingBy(
						FeedbackUserConfigPrmtEntity::getUserName,
						Collectors.groupingBy(
								FeedbackUserConfigPrmtEntity::getGroupCode)));

		List<FDSurveyRespDto> surveyDetailsList = groupedEntities.entrySet()
				.stream()
				.flatMap(userEntry -> userEntry.getValue().entrySet().stream()
						.map(groupEntry -> createSurveyDetails(
								groupEntry.getKey(), userEntry.getKey(),
								groupEntry.getValue(), getMultipleMap)))
				.collect(Collectors.toList());

		return surveyDetailsList;
	}

	private FDSurveyRespDto createSurveyDetails(String groupCode,
			String userName, List<FeedbackUserConfigPrmtEntity> entities,
			Map<String, String> getMultipleMap) {
		Map<String, List<FeedbackUserConfigPrmtEntity>> groupedEntities = entities
				.stream().collect(Collectors.groupingBy(
						FeedbackUserConfigPrmtEntity::getQuestionCode));

		List<FDSurveyGetRespDto> surveyResponses = new ArrayList<>();
		groupedEntities.forEach((questionCode, questionEntities) -> {
			FDSurveyGetRespDto response = new FDSurveyGetRespDto();
			response.setQuesCode(questionCode);
			if (Q3.equalsIgnoreCase(questionCode)) {
				String[] multiAnswers = questionEntities.get(0).getAnswer()
						.split("\\*");
				List<String> answers = new ArrayList<>();
				for (String part : multiAnswers) {
					answers.add(getMultipleMap.get(part));
				}
				response.setAnswerDesc(String.join(",", answers));
			} 
//			else if (Q1.equalsIgnoreCase(questionCode)) {
//				List<String> answers = questionEntities.stream()
//						.map(FeedbackUserConfigPrmtEntity::getAnswer)
//						.collect(Collectors.toList());
//				response.setAnswerDesc(String.join(",", answers));
//			} 
			else {
				response.setQuesId(questionEntities.get(0).getId());
				response.setAnswerDesc(questionEntities.get(0).getAnswer());
				response.setFileReq(Strings.isNullOrEmpty(
						questionEntities.get(0).getFilePath()) ? false : true);
			}
			surveyResponses.add(response);
		});

		FDSurveyRespDto surveyDetails = new FDSurveyRespDto();
		surveyDetails.setGroupCode(groupCode);
		surveyDetails.setUserName(userName);
		surveyDetails.setResults(surveyResponses);
		surveyDetails.setSubmittedOn(
				EYDateUtil.fmtDateOnly(entities.get(0).getCreatedOn()));

		return surveyDetails;
	}

}
