package com.ey.advisory.admin.services.dmsOnboarding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.DmsConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.ConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.ConfigQuestionRepository;
import com.ey.advisory.admin.data.repositories.client.DmsConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.DmsRuleMasterRepository;
import com.ey.advisory.admin.data.repositories.client.Get2aAutomationRepository;
import com.ey.advisory.admin.data.repositories.client.Get6aAutomationRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.InwardEinvoiceAutomationRepository;
import com.ey.advisory.admin.services.onboarding.EntityConfigPrmtService;
import com.ey.advisory.admin.services.onboarding.GroupConfigPrmtServiceImpl;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.async.domain.master.DmsUserDetails;
import com.ey.advisory.core.async.repositories.master.DmsUserDetailsRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.EntityConfigPrmtReqDto;
import com.ey.advisory.core.dto.EntityConfigPrmtResDto;
import com.ey.advisory.core.dto.ItemsAnsDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("DmsConfigPrmtServiceImpl")
public class DmsConfigPrmtServiceImpl implements EntityConfigPrmtService{
	
	@Autowired
	@Qualifier("DmsConfigPrmtRepository")
	private DmsConfigPrmtRepository dmsConfigPrmtRepository;
	
	@Autowired
	@Qualifier("DmsUserDetailsRepository")
	private DmsUserDetailsRepository userDetailsMasterRepo;
	
	@Autowired
	@Qualifier("DmsRuleMasterRepository")
	private DmsRuleMasterRepository dmsRuleMasterRepo;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GroupConfigPrmtServiceImpl.class);
	@Autowired
	@Qualifier("ConfigPrmtRepository")
	private ConfigPrmtRepository configPrmtRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("ConfigQuestionRepository")
	private ConfigQuestionRepository configQuestionRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	private static final String CONF_KEY = "gstr2a.no.of.taxPeriod";
	private static final String CONF_CATEG = "GSTR2A_AUTO_GET";

	private static final String CONF_KEY1 = "inward.einvoice.get.call.time";
	private static final String CONF_CATEG1 = "INWARD_EINVOICE";

	private static final String OUTWARD = "OUTWARD";
	private static final String INWARD = "INWARD";
	private static final String GENERAL = "GENERAL";
	private static final String RADIO = "R";
	private static final String S_RADIO = "SR";
	private static final String TEXT = "T";
	private static final String DATE = "D";
	private static final String TABLE = "TB";
	private static final String RADIOTEXT = "RT";
	private static final String RADIORADIO = "RR";

	private static final String MULTIPLE = "M";
	private static final String MULTIPLE7 = "M7";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Get2aAutomationRepository")
	private Get2aAutomationRepository get2aAutoRepo;

	@Autowired
	@Qualifier("Get6aAutomationRepository")
	private Get6aAutomationRepository get6aAutoRepo;

	@Autowired
	@Qualifier("InwardEinvoiceAutomationRepository")
	private InwardEinvoiceAutomationRepository inwardEinvoiceAutoRepo;

	public List<EntityConfigPrmtResDto> getEntityConfigPrmt(
			final EntityConfigPrmtReqDto reqDto) {

		List<EntityConfigPrmtResDto> resDtos = new LinkedList<>();

		if ("DMS".equalsIgnoreCase(reqDto.getType())) {
			getConfigPermit(resDtos, "DMS", reqDto);
		}
		return resDtos;
	}

	private void getConfigPermit(List<EntityConfigPrmtResDto> resDtos,
			final String paramtrCategory, final EntityConfigPrmtReqDto reqDto) {

		List<Object[]> configPermitEntities = getConfgParmtr(paramtrCategory);

		Map<String, Map<Triplet<Long, Long, String>, List<Pair<String, String>>>> attributeValueMap = configPermitEntities
				.stream().collect(
						Collectors
								.groupingBy(obj -> (String) obj[0],
										Collectors.groupingBy(
												obj -> new Triplet<Long, Long, String>(
														obj[1] != null
																? Long.valueOf(
																		String.valueOf(
																				obj[1]))
																: null,
														obj[2] != null
																? Long.valueOf(
																		String.valueOf(
																				obj[2]))
																: null,
														obj[3] != null
																? String.valueOf(
																		obj[3])
																: null),
												Collectors.mapping(
														obj -> new Pair<String, String>(
																(String) obj[4],
																(String) obj[5]),
														Collectors.toList()))));

		Set<String> keyTypes = attributeValueMap.keySet();
		keyTypes.forEach(keyType -> {
			if (RADIO.equalsIgnoreCase(keyType)
					|| S_RADIO.equalsIgnoreCase(keyType)) {
				Map<Triplet<Long, Long, String>, List<Pair<String, String>>> eachKeyType = attributeValueMap
						.get(keyType);

				Set<Triplet<Long, Long, String>> pairParameterKeys = eachKeyType
						.keySet();
				for (Triplet<Long, Long, String> pairParameterKey : pairParameterKeys) {
					EntityConfigPrmtResDto resDto = new EntityConfigPrmtResDto();
					resDto.setKeyType(keyType);
					resDto.setQuestId(pairParameterKey.getValue0());
					resDto.setSequenceId(pairParameterKey.getValue1());
					resDto.setQuestion(pairParameterKey.getValue2());
					String questionCode = configQuestionRepository
							.findQuestionCode(pairParameterKey.getValue0());

					resDto.setQuestionCode(questionCode);
					List<Pair<String, String>> pairParameterValues = eachKeyType
							.get(pairParameterKey);
					List<ItemsAnsDto> list = new ArrayList<>();
					List<String> answerCodes = new ArrayList<>();
					for (Pair<String, String> pairParameterValue : pairParameterValues) {
						ItemsAnsDto ansDto = new ItemsAnsDto();
						ansDto.setAnswerCode(pairParameterValue.getValue0());
						if (pairParameterValue.getValue1() != null
								&& !pairParameterValue.getValue1().trim()
										.isEmpty()) {
							ansDto.setAnswerDesc(
									pairParameterValue.getValue1());
						}
						if (pairParameterValue.getValue0() != null) {
							answerCodes.add(pairParameterValue.getValue0());
						}
						list.add(ansDto);
					}
					answerCodes.sort(Comparator.naturalOrder());
					// Find a selected Answer from Entity Config Parameter
					Long selectedId = dmsConfigPrmtRepository
							.findByGroupCodeAndConfgPrmtId(
									reqDto.getGroupCode(),
									pairParameterKey.getValue0());

					String selectedAnswer = dmsConfigPrmtRepository
							.findAnswerById(selectedId);
					resDto.setId(selectedId);
					// find a index selected Answer
					if (selectedAnswer != null && answerCodes != null
							&& !answerCodes.isEmpty()) {
						for (int i = 0; i < answerCodes.size(); i++) {

							  if ("G38".equalsIgnoreCase(questionCode)) {
										String answer = selectedAnswer.split("\\*")[0];
										if (answerCodes.get(i).equals(answer)) {
											resDto.setSelected(i);
											if (selectedAnswer
													.split("\\*").length == 2) {
												String answerDesc = selectedAnswer
														.split("\\*")[1];
												resDto.setAnswerDesc(answerDesc);
											}
										}
										// //11:12
										// //A/B/C
										else if (selectedAnswer.contains(":")) {
											resDto.setSelected(0);
											resDto.setGet2AHour(
													selectedAnswer.split(":")[0]);

											resDto.setGet2Amin(
													selectedAnswer.split(":")[1]);

										}
									} else if (answerCodes.get(i)
										.equals(selectedAnswer)) {
								resDto.setSelected(i);

							}
						}
					} else {
						resDto.setSelected(0);
					}
					list.sort(Comparator.comparing(ItemsAnsDto::getAnswerCode));
					resDto.setList(list);
					resDtos.add(resDto);
				}
			} else if (RADIOTEXT.equalsIgnoreCase(keyType)) {
				Map<Triplet<Long, Long, String>, List<Pair<String, String>>> eachKeyType = attributeValueMap
						.get(keyType);

				Set<Triplet<Long, Long, String>> pairParameterKeys = eachKeyType
						.keySet();
				for (Triplet<Long, Long, String> pairParameterKey : pairParameterKeys) {
					EntityConfigPrmtResDto resDto = new EntityConfigPrmtResDto();
					resDto.setKeyType(keyType);
					resDto.setQuestId(pairParameterKey.getValue0());
					resDto.setSequenceId(pairParameterKey.getValue1());
					resDto.setQuestion(pairParameterKey.getValue2());
					String questionCode = configQuestionRepository
							.findQuestionCode(pairParameterKey.getValue0());
					resDto.setQuestionCode(questionCode);
					List<Pair<String, String>> pairParameterValues = eachKeyType
							.get(pairParameterKey);
					List<ItemsAnsDto> list = new ArrayList<>();
					List<String> answerCodes = new ArrayList<>();
					for (Pair<String, String> pairParameterValue : pairParameterValues) {
						ItemsAnsDto ansDto = new ItemsAnsDto();
						ansDto.setAnswerCode(pairParameterValue.getValue0());
						if (pairParameterValue.getValue1() != null
								&& !pairParameterValue.getValue1().trim()
										.isEmpty()) {
							ansDto.setAnswerDesc(
									pairParameterValue.getValue1());
						}
						if (pairParameterValue.getValue0() != null) {
							answerCodes.add(pairParameterValue.getValue0());
						}
						list.add(ansDto);
					}
					answerCodes.sort(Comparator.naturalOrder());
					// Find a selected Answer from Entity Config Parameter
					Long selectedId = dmsConfigPrmtRepository
							.findByGroupCodeAndConfgPrmtId(
									reqDto.getGroupCode(),
									pairParameterKey.getValue0());
					String selectedAnswer = dmsConfigPrmtRepository
							.findAnswerById(selectedId);
					resDto.setId(selectedId);
					// find a index selected Answer
					if (selectedAnswer != null && answerCodes != null
							&& !answerCodes.isEmpty()) {
						for (int i = 0; i < answerCodes.size(); i++) {
							String answer = selectedAnswer.split("\\*")[0];
							if (answerCodes.get(i).equals(answer)) {
								resDto.setSelected(i);
								LOGGER.error(
										"EntityConfigPrmtServiceImpl -"
												+ " selectedAnswer {} ",
										selectedAnswer);
								if (selectedAnswer.length() > 1) {
									String answerDesc = selectedAnswer
											.split("\\*")[1];
									resDto.setAnswerDesc(answerDesc);
								}
							}
						}
					}
					list.sort(Comparator.comparing(ItemsAnsDto::getAnswerCode));
					resDto.setList(list);
					resDtos.add(resDto);
				}
			} else if (TEXT.equalsIgnoreCase(keyType)
					|| DATE.equalsIgnoreCase(keyType)
					|| MULTIPLE.equalsIgnoreCase(keyType)
					|| MULTIPLE7.equalsIgnoreCase(keyType)) {

				Map<Triplet<Long, Long, String>, List<Pair<String, String>>> eachKeyType = attributeValueMap
						.get(keyType);

				Set<Triplet<Long, Long, String>> pairParameterKeys = eachKeyType
						.keySet();
				for (Triplet<Long, Long, String> pairParameterKey : pairParameterKeys) {
					EntityConfigPrmtResDto resDto = new EntityConfigPrmtResDto();
					resDto.setKeyType(keyType);
					resDto.setQuestId(pairParameterKey.getValue0());
					resDto.setSequenceId(pairParameterKey.getValue1());
					resDto.setQuestion(pairParameterKey.getValue2());

					String questionCode = configQuestionRepository
							.findQuestionCode(pairParameterKey.getValue0());
					resDto.setQuestionCode(questionCode);
					Long selectedId = dmsConfigPrmtRepository
							.findByGroupCodeAndConfgPrmtId(
									reqDto.getGroupCode(),
									pairParameterKey.getValue0());
					resDto.setId(selectedId);
					
					//check userdetails for DMS
					Optional<DmsUserDetails> userDetailsOpt = userDetailsMasterRepo
							.findByGroupCode(reqDto.getGroupCode());
					
					if (!userDetailsOpt.isPresent()) {
						resDto.setAnswerDesc("No User Found");
						String errorMsg = "No user details found for groupCode: " + reqDto.getGroupCode();
						LOGGER.error(errorMsg);
					} else {

						String selectedAnswer = userDetailsOpt
								.map(user -> (user.getUserName() != null ? user.getUserName() : "") + "*"
										+ (user.getPassword() != null ? user.getPassword() : ""))
								.orElse("");

						if (!selectedAnswer.isEmpty()) {
							resDto.setAnswerDesc(selectedAnswer);
						}
					}
					resDtos.add(resDto);
				}
			} else if (TABLE.equalsIgnoreCase(keyType)) {
				Map<Triplet<Long, Long, String>, List<Pair<String, String>>> eachKeyType = attributeValueMap
						.get(keyType);

				Set<Triplet<Long, Long, String>> pairParameterKeys = eachKeyType
						.keySet();

				for (Triplet<Long, Long, String> pairParameterKey : pairParameterKeys) {
					EntityConfigPrmtResDto resDto = new EntityConfigPrmtResDto();
					resDto.setKeyType(keyType);
					resDto.setQuestId(pairParameterKey.getValue0());
					resDto.setSequenceId(pairParameterKey.getValue1());
					resDto.setQuestion(pairParameterKey.getValue2());
					String questionCode = configQuestionRepository
							.findQuestionCode(pairParameterKey.getValue0());
					resDto.setQuestionCode(questionCode);
					List<Pair<String, String>> pairParameterValues = eachKeyType
							.get(pairParameterKey);
					List<ItemsAnsDto> list = new ArrayList<>();

					for (Pair<String, String> pairParameterValue : pairParameterValues) {
						ItemsAnsDto ansDto = new ItemsAnsDto();
						ansDto.setAnswerCode(pairParameterValue.getValue0());
						if (pairParameterValue.getValue1() != null
								&& !pairParameterValue.getValue1().trim()
										.isEmpty()) {
							ansDto.setAnswerDesc(
									pairParameterValue.getValue1());
						}
						list.add(ansDto);
					}
					Long selectedId = dmsConfigPrmtRepository
							.findByGroupCodeAndConfgPrmtId(
									reqDto.getGroupCode(),
									pairParameterKey.getValue0());
					String selectedAnswer = dmsConfigPrmtRepository
							.findAnswerById(selectedId);
					resDto.setId(selectedId);
					if (selectedAnswer != null
							&& !selectedAnswer.trim().isEmpty()) {
						resDto.setAnswerDesc(selectedAnswer);
					}
					list.sort(Comparator.comparing(ItemsAnsDto::getAnswerCode));
					resDto.setList(list);
					resDtos.add(resDto);
				}
			} else if (RADIORADIO.equalsIgnoreCase(keyType)) {
				Map<Triplet<Long, Long, String>, List<Pair<String, String>>> eachKeyType = attributeValueMap
						.get(keyType);

				Set<Triplet<Long, Long, String>> pairParameterKeys = eachKeyType
						.keySet();
				for (Triplet<Long, Long, String> pairParameterKey : pairParameterKeys) {
					EntityConfigPrmtResDto resDto = new EntityConfigPrmtResDto();
					resDto.setKeyType(keyType);
					resDto.setQuestId(pairParameterKey.getValue0());
					resDto.setSequenceId(pairParameterKey.getValue1());
					resDto.setQuestion(pairParameterKey.getValue2());
					String questionCode = configQuestionRepository
							.findQuestionCode(pairParameterKey.getValue0());
					resDto.setQuestionCode(questionCode);
					List<Pair<String, String>> pairParameterValues = eachKeyType
							.get(pairParameterKey);
					List<ItemsAnsDto> list = new ArrayList<>();
					List<String> answerCodes = new ArrayList<>();
					for (Pair<String, String> pairParameterValue : pairParameterValues) {
						ItemsAnsDto ansDto = new ItemsAnsDto();
						ansDto.setAnswerCode(pairParameterValue.getValue0());
						if (pairParameterValue.getValue1() != null
								&& !pairParameterValue.getValue1().trim()
										.isEmpty()) {
							ansDto.setAnswerDesc(
									pairParameterValue.getValue1());
						}
						if (pairParameterValue.getValue0() != null) {
							answerCodes.add(pairParameterValue.getValue0());
						}
						list.add(ansDto);
					}
					answerCodes.sort(Comparator.naturalOrder());
					// Find a selected Answer from Entity Config Parameter
					Long selectedId = dmsConfigPrmtRepository
							.findByGroupCodeAndConfgPrmtId(
									reqDto.getGroupCode(),
									pairParameterKey.getValue0());
					String selectedAnswer = dmsConfigPrmtRepository
							.findAnswerById(selectedId);
					resDto.setId(selectedId);
					// find a index selected Answer
					if (selectedAnswer != null && answerCodes != null
							&& !answerCodes.isEmpty()) {
						for (int i = 0; i < answerCodes.size(); i++) {
							String answer = selectedAnswer.split("\\*")[0];
							if (answerCodes.get(i).equals(answer)) {
								resDto.setSelected(i);
								String answerDesc = selectedAnswer
										.split("\\*")[1];
								resDto.setAnswerDesc(answerDesc);
							}
						}
					}
					list.sort(Comparator.comparing(ItemsAnsDto::getAnswerCode));
					resDto.setList(list);
					resDtos.add(resDto);
				}
			}
		});
		resDtos.sort(
				Comparator.comparing(EntityConfigPrmtResDto::getSequenceId));

	}

	private List<Object[]> getConfgParmtr(final String paramtrCategory) {
		List<Object[]> obj = new ArrayList<>();
		String sql = createQueryString();
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("paramtrCategory", paramtrCategory);
		obj = query.getResultList();
		return obj;
	}

	private String createQueryString() {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"SELECT cq.question_type,cq.id,cq.SEQUENCE_ID,cq.question_description,");
		sb.append("ca.answer_options,ca.answer_options_description ");
		sb.append("FROM confg_question cq LEFT JOIN ");
		sb.append("confg_answer ca on cq.id=ca.confg_question_id ");
		sb.append(
				"WHERE question_category=:paramtrCategory AND is_active=true ");
		sb.append("order by cq.QUESTION_CODE,cq.SEQUENCE_ID asc");
		return sb.toString();
	}

	@Override
	@Transactional(value = "clientTransactionManager")
	public void updateConfigParmetr(List<EntityConfigPrmtReqDto> dtos) {

		Set<DmsConfigPrmtEntity> entities = new HashSet<>();
		List<Long> ids = new ArrayList<>();
		Integer derivedPeriod = null;

		Long groupId = null;
		for (EntityConfigPrmtReqDto dto : dtos) {
			String answer = dmsConfigPrmtRepository.findParamValByGroupCodeAndIdAndAnswer(dto.getGroupCode(),
					dto.getQuestId(), dto.getId(), dto.getAnswerCode());
			if (answer == null) {
				DmsConfigPrmtEntity entityConfigPrmtEntity = new DmsConfigPrmtEntity();
				entityConfigPrmtEntity.setGroupCode(dto.getGroupCode());
				groupId = groupInfoDetailsRepository.findByGroupId(dto.getGroupCode());
				entityConfigPrmtEntity.setGroupId(groupId);
				ids.add(dto.getId());

				// Soft delete for
				// entityConfigPrmtRepository.updateIsDeleteFlagFlase(
				// dto.getGroupCode(), dto.getEntityId(),
				// dto.getQuestId());
				String questionCode = configQuestionRepository.findQuestionCode(dto.getQuestId());
				entityConfigPrmtEntity.setConfgPrmtId(dto.getQuestId());
				entityConfigPrmtEntity.setParamValKeyId(questionCode);

				entityConfigPrmtEntity.setParamValue(dto.getAnswerCode());

				User user = SecurityContext.getUser();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User Name: {}", user.getUserPrincipalName());
				}
				entityConfigPrmtEntity.setCreatedBy(user.getUserPrincipalName());
				entityConfigPrmtEntity.setCreatedOn(EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

				if (DATE.equalsIgnoreCase(dto.getKeyType()) && dto.getAnswerCode() != null
						&& !dto.getAnswerCode().trim().isEmpty()) {
					String answerCode = dto.getAnswerCode();
					answer = dto.getAnswerCode();
					String answerDate = null;

					Date localAnswerDate;
					try {
						localAnswerDate = new SimpleDateFormat("MMyyyy").parse(answerCode);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
						answerDate = sdf.format(localAnswerDate);
					} catch (ParseException e) {
						LOGGER.error("Exception Occured:", e);
					}
					entityConfigPrmtEntity.setDerivedAnswer(Integer.valueOf(answerDate));
					derivedPeriod = Integer.valueOf(answerDate);
					dmsConfigPrmtRepository.updateDerivedAnswer(groupId, derivedPeriod, answer);
				}
				entities.add(entityConfigPrmtEntity);
			}
			Optional<DmsUserDetails> dmsUserPresent = userDetailsMasterRepo.findByGroupCode(dto.getGroupCode());
			if (!dmsUserPresent.isPresent()) {
				LOGGER.debug("New User Created for this groupCode : {}", dto.getGroupCode());
				DmsUserDetails dmsUser = new DmsUserDetails();
				dmsUser.setUserName(dto.getUserName());
				dmsUser.setPassword(dto.getPassword());
				dmsUser.setGroupCode(dto.getGroupCode());
				dmsUser.setCreatedOn(LocalDateTime.now());
				userDetailsMasterRepo.save(dmsUser);
			} else {
				LOGGER.debug("User already present for this groupCode so updating : {}", dto.getGroupCode());
				userDetailsMasterRepo.updateUserDetails(dto.getUserName(), dto.getPassword(), LocalDateTime.now(),
						dto.getGroupCode());
			}

		}
		if (!ids.isEmpty()) {
			dmsConfigPrmtRepository.deleteExitingAnswerForQuestion(ids);
		}
		if (!entities.isEmpty()) {
			dmsConfigPrmtRepository.saveAll(entities);
		}

	}

	private Integer getNoOfTaxPeriod(String groupCode) {
		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY,
				groupCode);

		if (config != null) {

			return Integer.valueOf(config.getValue());
		}
		return 2;

	}

	private String getInwardEinvoiceGetCallTime() {
		Config config = configManager.getConfig(CONF_CATEG1, CONF_KEY1,
				"DEFAULT");

		if (config != null) {

			return config.getValue().toString();
		}
		return "14:30";

	}

	private String dateChange(String dateTime) {
		DateTimeFormatter formatter = null;

		String s1 = dateTime.substring(0, 19).replace("T", " ");

		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		LocalDateTime dateTimes = LocalDateTime.parse(s1, formatter);

		LocalDateTime dateTimeFormatter = EYDateUtil
				.toISTDateTimeFromUTC(dateTimes);
		DateTimeFormatter FOMATTER = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		String newdate = FOMATTER.format(dateTimeFormatter);
		return newdate;

	}
}
