package com.ey.advisory.admin.services.onboarding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.Get2aAutomationEntity;
import com.ey.advisory.admin.data.entities.client.Get6aAutomationEntity;
import com.ey.advisory.admin.data.entities.client.InwardEinvoiceAutomationEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.ConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.ConfigQuestionRepository;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.Get2aAutomationRepository;
import com.ey.advisory.admin.data.repositories.client.Get6aAutomationRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.InwardEinvoiceAutomationRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.EntityConfigPrmtReqDto;
import com.ey.advisory.core.dto.EntityConfigPrmtResDto;
import com.ey.advisory.core.dto.ItemsAnsDto;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("entityConfigPrmtService")
public class EntityConfigPrmtServiceImpl implements EntityConfigPrmtService {

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPrmtRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EntityConfigPrmtServiceImpl.class);
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

		if (OUTWARD.equalsIgnoreCase(reqDto.getType())) {
			getConfigPermit(resDtos, OUTWARD, reqDto);
		} else if (INWARD.equalsIgnoreCase(reqDto.getType())) {
			getConfigPermit(resDtos, INWARD, reqDto);
		} else if (GENERAL.equalsIgnoreCase(reqDto.getType())) {
			getConfigPermit(resDtos, GENERAL, reqDto);
		}
		return resDtos;
	}

	private void getConfigPermit(List<EntityConfigPrmtResDto> resDtos,
			final String paramtrCategory, final EntityConfigPrmtReqDto reqDto) {

		List<Object[]> configPermitEntities = getConfgParmtr(paramtrCategory);

		Map<String, Map<Triplet<Long, Long, String>, List<Pair<String, String>>>> attributeValueMap = configPermitEntities
				.stream()
				.collect(Collectors.groupingBy(obj -> (String) obj[0],
						Collectors.groupingBy(
								obj -> new Triplet<Long, Long, String>(
										obj[1] != null
												? Long.valueOf(
														String.valueOf(obj[1]))
												: null,
										obj[2] != null
												? Long.valueOf(
														String.valueOf(obj[2]))
												: null,
										obj[3] != null ? String.valueOf(obj[3])
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

					if ("Select GetCall Frequency?"
							.equalsIgnoreCase(pairParameterKey.getValue2())
							&& "I50".equalsIgnoreCase(questionCode)) {
						EntityConfigPrmtEntity entity = new EntityConfigPrmtEntity();

						entity = entityConfigPrmtRepository
								.findEntityByEntityAutoInwardEinvoiceGetCallFrequency(
										reqDto.getEntityId());

						EntityConfigPrmtEntity entity1 = new EntityConfigPrmtEntity();

						entity1 = entityConfigPrmtRepository
								.findEntityByEntityAutoInwardEinvoiceGetCallTimestampFrequency(
										reqDto.getEntityId());

						if (entity != null && entity1 != null) {

							if (entity1.getCreatedOn()
									.compareTo(entity.getCreatedOn()) >= 0)

							{
								resDto.setQuestion(pairParameterKey.getValue2()
										+ " ( Last updated timestamp: "
										+ dateChange(entity1.getCreatedOn()
												.toString())
										+ " ) ");
							} else {
								resDto.setQuestion(pairParameterKey.getValue2()
										+ " ( Last updated timestamp: "
										+ dateChange(entity.getCreatedOn()
												.toString())
										+ " ) ");
							}
						} else {
							resDto.setQuestion(pairParameterKey.getValue2()
									+ " ( Last updated timestamp: " + ") ");
						}
					}
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
					Long selectedId = entityConfigPrmtRepository
							.findByGroupCodeAndEntityIdAndConfgPrmtId(
									reqDto.getGroupCode(), reqDto.getEntityId(),
									pairParameterKey.getValue0());
					String selectedAnswer = entityConfigPrmtRepository
							.findAnswerById(selectedId);
					resDto.setId(selectedId);
					// find a index selected Answer
					if (selectedAnswer != null && answerCodes != null
							&& !answerCodes.isEmpty()) {
						for (int i = 0; i < answerCodes.size(); i++) {

							if ("I26".equalsIgnoreCase(questionCode)) {
								String answer = selectedAnswer.split("\\*")[0]; // D*1,12,23
																				// //11:12
																				// //A/B/C
								if (answerCodes.get(i).equals(answer)) {
									resDto.setSelected(i);
									if (selectedAnswer
											.split("\\*").length == 2) {
										String answerDesc = selectedAnswer
												.split("\\*")[1];
										resDto.setAnswerDesc(answerDesc);
									}
								} else if (selectedAnswer.contains(":")) {
									resDto.setSelected(0);
									resDto.setGet2AHour(
											selectedAnswer.split(":")[0]);
									resDto.setGet2Amin(
											selectedAnswer.split(":")[1]);
								}
							} else if ("I36".equalsIgnoreCase(questionCode)) {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Selected answer for Auto 6A get call frequency");
								}

								resDto.setSelected(i);
								resDto.setAnswerDesc(selectedAnswer);
							} else if ("I50".equalsIgnoreCase(questionCode)) {
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
							}
							else if ("O31".equalsIgnoreCase(questionCode)) {
								String answer = selectedAnswer.split("\\*")[0]; 
								if (answerCodes.get(i).equals(answer)) {
									resDto.setSelected(i);
									if (selectedAnswer
											.split("\\*").length == 2) {
										String answerDesc = selectedAnswer
												.split("\\*")[1];
										resDto.setAnswerDesc(answerDesc);
									}
								} else if (selectedAnswer.contains(":")) {
									resDto.setSelected(0);
									resDto.setGet2AHour(
											selectedAnswer.split(":")[0]);
									resDto.setGet2Amin(
											selectedAnswer.split(":")[1]);
								}
							} 
							else if (answerCodes.get(i)
									.equals(selectedAnswer)) {
								resDto.setSelected(i);

							}
						}
					} else if ("I34".equalsIgnoreCase(questionCode)
							|| "I35".equalsIgnoreCase(questionCode)) {

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Setting default answer as 'No' for "
									+ "Auto 6A get call and Auto 6A Recon");
						}
						resDto.setSelected(1);
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
					Long selectedId = entityConfigPrmtRepository
							.findByGroupCodeAndEntityIdAndConfgPrmtId(
									reqDto.getGroupCode(), reqDto.getEntityId(),
									pairParameterKey.getValue0());
					String selectedAnswer = entityConfigPrmtRepository
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
					Long selectedId = entityConfigPrmtRepository
							.findByGroupCodeAndEntityIdAndConfgPrmtId(
									reqDto.getGroupCode(), reqDto.getEntityId(),
									pairParameterKey.getValue0());
					String selectedAnswer = entityConfigPrmtRepository
							.findAnswerById(selectedId);
					resDto.setId(selectedId);
					if (selectedAnswer != null
							&& !selectedAnswer.trim().isEmpty()) {
						resDto.setAnswerDesc(selectedAnswer);
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
					Long selectedId = entityConfigPrmtRepository
							.findByGroupCodeAndEntityIdAndConfgPrmtId(
									reqDto.getGroupCode(), reqDto.getEntityId(),
									pairParameterKey.getValue0());
					String selectedAnswer = entityConfigPrmtRepository
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
					Long selectedId = entityConfigPrmtRepository
							.findByGroupCodeAndEntityIdAndConfgPrmtId(
									reqDto.getGroupCode(), reqDto.getEntityId(),
									pairParameterKey.getValue0());
					String selectedAnswer = entityConfigPrmtRepository
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
		User user = SecurityContext.getUser();

		// Insert auto Gstr2A Monthly or Daily get call
		String gstr2A = entityConfigPrmtRepository
				.findByEntityAutoGet2ACall(reqDto.getEntityId());

		String geGstr2aGetCall = entityConfigPrmtRepository
				.findByEntityAutoGet2AGetCall(reqDto.getEntityId());
		String get2ATimeStamp = entityConfigPrmtRepository
				.getTimeStampForEntityAutoGet2AGetCall(reqDto.getEntityId());
		get2ATimeStamp = get2ATimeStamp == null ? "12:00" : get2ATimeStamp;
		List<Get2aAutomationEntity> get2aAutoEntities = new ArrayList<>();
		if (gstr2A != null) {
			if ("A".equalsIgnoreCase(gstr2A)) {
				get2aAutoRepo.updateInActiveGet2aBasedOnEntityId(
						reqDto.getEntityId());
				if ("B".equalsIgnoreCase(geGstr2aGetCall)) {
					// Daily and Montly
					String time = get2ATimeStamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					StringBuilder uniqueKey = new StringBuilder();
					uniqueKey.append(reqDto.getEntityId());
					uniqueKey.append("|");
					uniqueKey.append(dailyTimePeriod);
					uniqueKey.append("|");
					uniqueKey.append(
							getNoOfTaxPeriod(TenantContext.getTenantId()));
					uniqueKey.append("|");
					uniqueKey.append("D");
					uniqueKey.append("|");
					uniqueKey.append(false);

					Get2aAutomationEntity get2aAutoEntity = new Get2aAutomationEntity();
					get2aAutoEntity.setCalendarTime(dailyTimePeriod);
					get2aAutoEntity.setEntityId(reqDto.getEntityId());

					get2aAutoEntity.setNumOfTaxPeriods(
							getNoOfTaxPeriod(TenantContext.getTenantId()));// HARDCODED
																			// 2
					get2aAutoEntity.setFinYearGet(false);

					get2aAutoEntity.setUniqueKey(uniqueKey.toString());
					get2aAutoEntity.setDelete(false);
					get2aAutoEntity.setCreatedBy(user.getUserPrincipalName());
					get2aAutoEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					get2aAutoEntity.setGetEvent("D");
					get2aAutoEntity.setLastPostedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					get2aAutoEntities.add(get2aAutoEntity);

					String montlyDate = "14"; // hardcoded
					StringBuilder uniqueKey2 = new StringBuilder();
					uniqueKey2.append(reqDto.getEntityId());
					uniqueKey2.append("|");
					uniqueKey2.append(dailyTimePeriod);
					uniqueKey2.append("|");
					uniqueKey2.append(montlyDate);
					uniqueKey2.append("|");
					uniqueKey2.append("M");
					uniqueKey2.append("|");
					uniqueKey2.append(true);
					Get2aAutomationEntity get2aAutoEntity2 = new Get2aAutomationEntity();
					get2aAutoEntity2.setCalendarTime(dailyTimePeriod);
					get2aAutoEntity2.setCalendarDateAsString(montlyDate);
					get2aAutoEntity2.setEntityId(reqDto.getEntityId());
					get2aAutoEntity2.setFinYearGet(true);

					get2aAutoEntity2.setUniqueKey(uniqueKey2.toString());
					get2aAutoEntity2.setDelete(false);
					get2aAutoEntity2.setCreatedBy(user.getUserPrincipalName());
					get2aAutoEntity2.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					get2aAutoEntity2.setGetEvent("M");
					get2aAutoEntity2.setLastPostedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					get2aAutoEntities.add(get2aAutoEntity2);

				} else if ("C".equalsIgnoreCase(geGstr2aGetCall)) {
					// Only Montly
					String time = get2ATimeStamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					String montlyDate = "14";

					StringBuilder uniqueKey2 = new StringBuilder();
					uniqueKey2.append(reqDto.getEntityId());
					uniqueKey2.append("|");
					uniqueKey2.append(dailyTimePeriod);
					uniqueKey2.append("|");
					uniqueKey2.append(montlyDate);
					uniqueKey2.append("|");
					uniqueKey2.append("M");
					uniqueKey2.append("|");
					uniqueKey2.append(true);
					Get2aAutomationEntity get2aAutoEntity2 = new Get2aAutomationEntity();
					get2aAutoEntity2.setCalendarTime(dailyTimePeriod);
					get2aAutoEntity2.setCalendarDateAsString(montlyDate);
					get2aAutoEntity2.setEntityId(reqDto.getEntityId());
					get2aAutoEntity2.setFinYearGet(true);

					get2aAutoEntity2.setUniqueKey(uniqueKey2.toString());
					get2aAutoEntity2.setDelete(false);
					get2aAutoEntity2.setCreatedBy(user.getUserPrincipalName());
					get2aAutoEntity2.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					get2aAutoEntity2.setGetEvent("M");
					get2aAutoEntity2.setLastPostedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					get2aAutoEntities.add(get2aAutoEntity2);
				} else if ("A".equalsIgnoreCase(geGstr2aGetCall)) {
					// Daily and Weekly
					String time = get2ATimeStamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					StringBuilder uniqueKey = new StringBuilder();
					uniqueKey.append(reqDto.getEntityId());
					uniqueKey.append("|");
					uniqueKey.append(dailyTimePeriod);
					uniqueKey.append("|");
					uniqueKey.append(
							getNoOfTaxPeriod(TenantContext.getTenantId())); // need
																			// to
																			// change
																			// 2
																			// from
																			// config
					uniqueKey.append("|");
					uniqueKey.append("D");
					uniqueKey.append("|");
					uniqueKey.append(false);
					Get2aAutomationEntity get2aAutoEntity = new Get2aAutomationEntity();
					get2aAutoEntity.setCalendarTime(dailyTimePeriod);
					get2aAutoEntity.setEntityId(reqDto.getEntityId());

					get2aAutoEntity.setNumOfTaxPeriods(
							getNoOfTaxPeriod(TenantContext.getTenantId()));
					get2aAutoEntity.setFinYearGet(false);

					get2aAutoEntity.setUniqueKey(uniqueKey.toString());
					get2aAutoEntity.setDelete(false);
					get2aAutoEntity.setCreatedBy(user.getUserPrincipalName());
					get2aAutoEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					get2aAutoEntity.setGetEvent("D");
					get2aAutoEntity.setLastPostedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					get2aAutoEntities.add(get2aAutoEntity);

					List<String> weeklyDates = Arrays.asList("5", "14", "21",
							"28");

					for (String date : weeklyDates) {
						StringBuilder weeklyUniqueKey = new StringBuilder();
						weeklyUniqueKey.append(reqDto.getEntityId());
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append(dailyTimePeriod);
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append(date);
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append("W");
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append(true);
						Get2aAutomationEntity get2aAutoEntity2 = new Get2aAutomationEntity();
						get2aAutoEntity2.setCalendarTime(dailyTimePeriod);
						get2aAutoEntity2.setCalendarDateAsString(date);
						get2aAutoEntity2.setEntityId(reqDto.getEntityId());
						get2aAutoEntity2.setFinYearGet(true);

						get2aAutoEntity2
								.setUniqueKey(weeklyUniqueKey.toString());
						get2aAutoEntity2.setDelete(false);
						get2aAutoEntity2
								.setCreatedBy(user.getUserPrincipalName());
						get2aAutoEntity2.setCreatedOn(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						get2aAutoEntity2.setGetEvent("W");
						get2aAutoEntity2.setLastPostedDate(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						get2aAutoEntities.add(get2aAutoEntity2);
					}
				} else {
					// Custom dates only
					String time = get2ATimeStamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					String dates = geGstr2aGetCall.split("\\*")[1];

					if (dates == null) {
						LOGGER.error(
								"No dates selected by user for entity : {}",
								reqDto.getEntityId());
						return;
					}
					List<String> userSelectedDates = Arrays
							.asList(dates.split(","));
					List<String> maxFourDates = userSelectedDates.stream()
							.limit(4).collect(Collectors.toList());
					maxFourDates.replaceAll(String::trim);
					List<Integer> userDates = maxFourDates.stream()
							.map(Integer::parseInt)
							.collect(Collectors.toList());
					Collections.sort(userDates);

					for (int i = 0; i < userDates.size(); i++) {
						String dt = String.valueOf(userDates.get(i));

						String event = i == 0 ? "M" : "D";
						StringBuilder uniqueKey2 = new StringBuilder();
						uniqueKey2.append(reqDto.getEntityId());
						uniqueKey2.append("|");
						uniqueKey2.append(dailyTimePeriod);
						uniqueKey2.append("|");
						uniqueKey2.append(dt); // user selected date
						uniqueKey2.append("|");
						uniqueKey2.append(event);
						uniqueKey2.append("|");
						uniqueKey2.append(true);

						Get2aAutomationEntity get2aAutoEntity2 = new Get2aAutomationEntity();
						get2aAutoEntity2.setCalendarTime(dailyTimePeriod);
						get2aAutoEntity2.setCalendarDateAsString(dt); // user
																		// selected
																		// date
						get2aAutoEntity2.setEntityId(reqDto.getEntityId());
						get2aAutoEntity2.setFinYearGet(true);

						get2aAutoEntity2.setUniqueKey(uniqueKey2.toString());
						get2aAutoEntity2.setDelete(false);
						get2aAutoEntity2
								.setCreatedBy(user.getUserPrincipalName());
						get2aAutoEntity2.setCreatedOn(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						get2aAutoEntity2.setGetEvent(event);
						get2aAutoEntity2.setLastPostedDate(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						get2aAutoEntities.add(get2aAutoEntity2);
					}
				}
			} else if ("B".equalsIgnoreCase(gstr2A)) {
				get2aAutoRepo.updateInActiveGet2aBasedOnEntityId(
						reqDto.getEntityId());
			}
		}
		if (!get2aAutoEntities.isEmpty())

		{
			get2aAutoRepo.saveAll(get2aAutoEntities);
		}

		// insert into 6a automation table

		String gstr6A = entityConfigPrmtRepository
				.findByEntityAutoGet6ACall(reqDto.getEntityId());

		String geGstr6aGetCall = entityConfigPrmtRepository
				.findByEntityAutoGet6AGetCall(reqDto.getEntityId());

		List<Get6aAutomationEntity> get6aAutoEntities = new ArrayList<>();
		if (gstr6A != null) {
			if ("A".equalsIgnoreCase(gstr6A)) {
				get6aAutoRepo.updateInActiveGet6aBasedOnEntityId(
						reqDto.getEntityId());

				// Custom dates only
				String time = "14:00";
				LocalTime dailyTimePeriod6A = LocalTime.parse(time,
						DateTimeFormatter.ISO_TIME);
				if (Strings.isNullOrEmpty(geGstr6aGetCall)) {
					geGstr6aGetCall = "14,30";
				}
				List<String> userSelectedDates = Arrays
						.asList(geGstr6aGetCall.split(","));
				List<String> maxTwoDates = userSelectedDates.stream().limit(4)
						.collect(Collectors.toList());
				maxTwoDates.replaceAll(String::trim);
				List<Integer> userDates = maxTwoDates.stream()
						.map(Integer::parseInt).collect(Collectors.toList());
				Collections.sort(userDates);

				for (int i = 0; i < userDates.size(); i++) {
					String dt = String.valueOf(userDates.get(i));

					StringBuilder uniqueKey2 = new StringBuilder();
					uniqueKey2.append(reqDto.getEntityId());
					uniqueKey2.append("|");
					uniqueKey2.append(dailyTimePeriod6A);
					uniqueKey2.append("|");
					uniqueKey2.append(dt); // user selected date
					uniqueKey2.append("|");
					uniqueKey2.append("M");
					uniqueKey2.append("|");
					uniqueKey2.append(true);

					Get6aAutomationEntity get6aAutoEntity2 = new Get6aAutomationEntity();
					get6aAutoEntity2.setCalendarTime(dailyTimePeriod6A);
					get6aAutoEntity2.setCalendarDateAsString(dt); // user
																	// selected
																	// date
					get6aAutoEntity2.setEntityId(reqDto.getEntityId());

					get6aAutoEntity2.setUniqueKey(uniqueKey2.toString());
					get6aAutoEntity2.setDelete(false);
					get6aAutoEntity2.setCreatedBy(user.getUserPrincipalName());
					get6aAutoEntity2.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					get6aAutoEntity2.setGetEvent("M");
					get6aAutoEntity2.setLastPostedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					get6aAutoEntities.add(get6aAutoEntity2);
				}
			} else if ("B".equalsIgnoreCase(gstr6A)) {
				get6aAutoRepo.updateInActiveGet6aBasedOnEntityId(
						reqDto.getEntityId());
			}
		}
		if (!get6aAutoEntities.isEmpty())

		{
			get6aAutoRepo.saveAll(get6aAutoEntities);
		}

		// insert into inward Einvoice automation table

		String inwrdEinv = entityConfigPrmtRepository
				.findByEntityAutoInitiateGetCall(reqDto.getEntityId(),
						"Do you want Auto fetch of Inward E-Invoices ?", "I50");

		String inwardEinvGetCallFreq = entityConfigPrmtRepository
				.findByEntityAutoInwardEinvoiceGetCallFrequency(
						reqDto.getEntityId());

		String inwardEinvGetCallTimestamp = entityConfigPrmtRepository
				.getTimeStampForEntityAutoInwardEinvoice(reqDto.getEntityId());

		inwardEinvGetCallTimestamp = inwardEinvGetCallTimestamp == null
				? "12:00"
				: inwardEinvGetCallTimestamp;
		YearMonth dateMonth = YearMonth.now();
		String endOfMonth = String
				.valueOf(dateMonth.atEndOfMonth().getDayOfMonth());
		List<InwardEinvoiceAutomationEntity> inwardEinvoiceAutomationEntities = new ArrayList<>();
		if (inwrdEinv != null) {
			if ("A".equalsIgnoreCase(inwrdEinv)) {
				inwardEinvoiceAutoRepo
						.updateInActiveInwardEinvoiceBasedOnEntityId(
								reqDto.getEntityId());

				if ("A".equalsIgnoreCase(inwardEinvGetCallFreq)) {
					// Daily
					String time = inwardEinvGetCallTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					StringBuilder uniqueKey = new StringBuilder();
					uniqueKey.append(reqDto.getEntityId());
					uniqueKey.append("|");
					uniqueKey.append(dailyTimePeriod);
					uniqueKey.append("|");
					uniqueKey.append(1);
					uniqueKey.append("|");
					uniqueKey.append("D");
					uniqueKey.append("|");
					uniqueKey.append(false);

					InwardEinvoiceAutomationEntity inwardEinvAutoEntity = new InwardEinvoiceAutomationEntity();
					inwardEinvAutoEntity.setCalendarTime(dailyTimePeriod);
					inwardEinvAutoEntity.setEntityId(reqDto.getEntityId());
					inwardEinvAutoEntity.setUniqueKey(uniqueKey.toString());
					inwardEinvAutoEntity.setDelete(false);
					inwardEinvAutoEntity
							.setCreatedBy(user.getUserPrincipalName());
					inwardEinvAutoEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					inwardEinvAutoEntity.setGetEvent("D");
					inwardEinvAutoEntity.setLastPostedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					inwardEinvoiceAutomationEntities.add(inwardEinvAutoEntity);

				} else if ("D".equalsIgnoreCase(inwardEinvGetCallFreq)) {
					// Only Montly
					String time = inwardEinvGetCallTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					String montlyDate = String
							.valueOf(dateMonth.atEndOfMonth().getDayOfMonth());

					StringBuilder uniqueKey2 = new StringBuilder();
					uniqueKey2.append(reqDto.getEntityId());
					uniqueKey2.append("|");
					uniqueKey2.append(dailyTimePeriod);
					uniqueKey2.append("|");
					uniqueKey2.append(montlyDate);
					uniqueKey2.append("|");
					uniqueKey2.append("M");
					uniqueKey2.append("|");
					uniqueKey2.append(true);

					InwardEinvoiceAutomationEntity inwardEinvAutoEntity = new InwardEinvoiceAutomationEntity();
					inwardEinvAutoEntity.setCalendarTime(dailyTimePeriod);
					inwardEinvAutoEntity.setEntityId(reqDto.getEntityId());
					inwardEinvAutoEntity.setUniqueKey(uniqueKey2.toString());
					inwardEinvAutoEntity.setDelete(false);
					inwardEinvAutoEntity
							.setCreatedBy(user.getUserPrincipalName());
					inwardEinvAutoEntity.setCalendarDateAsString(montlyDate);
					inwardEinvAutoEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					inwardEinvAutoEntity.setGetEvent("M");
					inwardEinvAutoEntity.setLastPostedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					inwardEinvoiceAutomationEntities.add(inwardEinvAutoEntity);

				} else if ("B".equalsIgnoreCase(inwardEinvGetCallFreq)) {
					// Weekly
					String time = inwardEinvGetCallTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					List<String> weeklyDates = Arrays.asList("5", "13", "21",
							endOfMonth);

					for (String date : weeklyDates) {
						StringBuilder weeklyUniqueKey = new StringBuilder();
						weeklyUniqueKey.append(reqDto.getEntityId());
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append(dailyTimePeriod);
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append(date);
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append("W");
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append(true);

						InwardEinvoiceAutomationEntity inwardEinvAutoEntity = new InwardEinvoiceAutomationEntity();
						inwardEinvAutoEntity.setCalendarTime(dailyTimePeriod);
						inwardEinvAutoEntity.setEntityId(reqDto.getEntityId());
						inwardEinvAutoEntity
								.setUniqueKey(weeklyUniqueKey.toString());
						inwardEinvAutoEntity.setCalendarDateAsString(date);
						inwardEinvAutoEntity.setDelete(false);
						inwardEinvAutoEntity
								.setCreatedBy(user.getUserPrincipalName());
						inwardEinvAutoEntity.setCreatedOn(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						inwardEinvAutoEntity.setGetEvent("W");
						inwardEinvAutoEntity.setLastPostedDate(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						inwardEinvoiceAutomationEntities
								.add(inwardEinvAutoEntity);
					}
				} else if ("C".equalsIgnoreCase(inwardEinvGetCallFreq)) {
					// fortnight
					String time = inwardEinvGetCallTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					List<String> forthNightDates = Arrays.asList("15",
							endOfMonth);

					for (String date : forthNightDates) {
						StringBuilder fortNightUniqueKey = new StringBuilder();
						fortNightUniqueKey.append(reqDto.getEntityId());
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append(dailyTimePeriod);
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append(date);
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append("F");
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append(true);

						InwardEinvoiceAutomationEntity inwardEinvAutoEntity = new InwardEinvoiceAutomationEntity();
						inwardEinvAutoEntity.setCalendarTime(dailyTimePeriod);
						inwardEinvAutoEntity.setCalendarDateAsString(date);
						inwardEinvAutoEntity.setEntityId(reqDto.getEntityId());
						inwardEinvAutoEntity
								.setUniqueKey(fortNightUniqueKey.toString());
						inwardEinvAutoEntity.setDelete(false);
						inwardEinvAutoEntity
								.setCreatedBy(user.getUserPrincipalName());
						inwardEinvAutoEntity.setCreatedOn(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						inwardEinvAutoEntity.setGetEvent("F");
						inwardEinvAutoEntity.setLastPostedDate(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						inwardEinvoiceAutomationEntities
								.add(inwardEinvAutoEntity);
					}
				} else {

					// Custom dates only
					String time = inwardEinvGetCallTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					String dates = inwardEinvGetCallFreq.split("\\*")[1];

					if (dates == null) {
						LOGGER.error(
								"No dates selected by user for inward einvoice entity : {}",
								reqDto.getEntityId());
						return;
					}
					List<String> userSelectedDates = Arrays
							.asList(dates.split(","));
					List<String> maxFourDates = userSelectedDates.stream()
							.limit(4).collect(Collectors.toList());
					maxFourDates.replaceAll(String::trim);
					List<Integer> userDates = maxFourDates.stream()
							.map(Integer::parseInt)
							.collect(Collectors.toList());
					Collections.sort(userDates);

					for (int i = 0; i < userDates.size(); i++) {
						String dt = String.valueOf(userDates.get(i));

						StringBuilder fortNightUniqueKey = new StringBuilder();
						fortNightUniqueKey.append(reqDto.getEntityId());
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append(dailyTimePeriod);
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append(dt);
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append("C");
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append(true);

						InwardEinvoiceAutomationEntity inwardEinvAutoEntity = new InwardEinvoiceAutomationEntity();
						inwardEinvAutoEntity.setCalendarTime(dailyTimePeriod);
						inwardEinvAutoEntity.setCalendarDateAsString(dt);
						inwardEinvAutoEntity.setEntityId(reqDto.getEntityId());
						inwardEinvAutoEntity
								.setUniqueKey(fortNightUniqueKey.toString());
						inwardEinvAutoEntity.setDelete(false);
						inwardEinvAutoEntity
								.setCreatedBy(user.getUserPrincipalName());
						inwardEinvAutoEntity.setCreatedOn(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						inwardEinvAutoEntity.setGetEvent("C");
						inwardEinvAutoEntity.setLastPostedDate(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						inwardEinvoiceAutomationEntities
								.add(inwardEinvAutoEntity);
					}
				}
			}
		} else if ("B".equalsIgnoreCase(inwrdEinv)) {
			inwardEinvoiceAutoRepo.updateInActiveInwardEinvoiceBasedOnEntityId(
					reqDto.getEntityId());
		}

		if (!inwardEinvoiceAutomationEntities.isEmpty())

		{
			inwardEinvoiceAutoRepo.saveAll(inwardEinvoiceAutomationEntities);
		}

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

		Set<EntityConfigPrmtEntity> entities = new HashSet<>();
		List<Long> ids = new ArrayList<>();
		Integer derivedPeriod = null;

		Long groupId = null;
		Long entityId = null;

		Set<EntityConfigPrmtEntity> textEntities = new HashSet<>();
		List<EntityConfigPrmtReqDto> textList = dtos.stream()
				.filter(o -> o.getKeyType().equalsIgnoreCase(TEXT))
				.collect(Collectors.toList());

		dtos.removeAll(textList);
		for (EntityConfigPrmtReqDto dto : dtos) {

			entityId = dto.getEntityId();
			String answer = entityConfigPrmtRepository
					.findParamValByGroupCodeAndEntityIdAndIdAndAnswer(
							dto.getGroupCode(), dto.getEntityId(),
							dto.getQuestId(), dto.getId(), dto.getAnswerCode());
			if (answer == null) {
				EntityConfigPrmtEntity entityConfigPrmtEntity = new EntityConfigPrmtEntity();
				entityConfigPrmtEntity.setGroupCode(dto.getGroupCode());
				groupId = groupInfoDetailsRepository
						.findByGroupId(dto.getGroupCode());
				entityConfigPrmtEntity.setGroupId(groupId);
				entityConfigPrmtEntity.setEntityId(dto.getEntityId());
				ids.add(dto.getId());

				// Soft delete for
				// entityConfigPrmtRepository.updateIsDeleteFlagFlase(
				// dto.getGroupCode(), dto.getEntityId(),
				// dto.getQuestId());
				String questionCode = configQuestionRepository
						.findQuestionCode(dto.getQuestId());
				entityConfigPrmtEntity.setConfgPrmtId(dto.getQuestId());
				entityConfigPrmtEntity.setParamValKeyId(questionCode);

				if ("O27".equalsIgnoreCase(questionCode)) {
					entityConfigPrmtEntity
							.setParamValue(dto.getAnswerCode().isEmpty() ? "0"
									: dto.getAnswerCode());
				} else if ("I36".equalsIgnoreCase(questionCode)) {
					entityConfigPrmtEntity.setParamValue(
							Strings.isNullOrEmpty(dto.getAnswerCode()) ? "14,30"
									: dto.getAnswerCode());
				} else {
					entityConfigPrmtEntity.setParamValue(dto.getAnswerCode());
				}

				User user = SecurityContext.getUser();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User Name: {}", user.getUserPrincipalName());
				}
				entityConfigPrmtEntity
						.setCreatedBy(user.getUserPrincipalName());
				entityConfigPrmtEntity.setCreatedOn(
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

				if (DATE.equalsIgnoreCase(dto.getKeyType())
						&& dto.getAnswerCode() != null
						&& !dto.getAnswerCode().trim().isEmpty()) {
					String answerCode = dto.getAnswerCode();
					answer = dto.getAnswerCode();
					String answerDate = null;

					Date localAnswerDate;
					try {
						localAnswerDate = new SimpleDateFormat("MMyyyy")
								.parse(answerCode);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
						answerDate = sdf.format(localAnswerDate);
					} catch (ParseException e) {
						LOGGER.error("Exception Occured:", e);
					}
					entityConfigPrmtEntity
							.setDerivedAnswer(Integer.valueOf(answerDate));
					derivedPeriod = Integer.valueOf(answerDate);
					entityConfigPrmtRepository.updateDerivedReturnPeriod(
							groupId, derivedPeriod, answer);
				}
				entities.add(entityConfigPrmtEntity);
			}
		}

		for (EntityConfigPrmtReqDto dto : textList) {

			entityId = dto.getEntityId();
			String answer = entityConfigPrmtRepository
					.findParamValByGroupCodeAndEntityIdAndIdAndAnswer(
							dto.getGroupCode(), dto.getEntityId(),
							dto.getQuestId(), dto.getId(), dto.getAnswerCode());
			if (answer == null) {
				EntityConfigPrmtEntity entityConfigPrmtEntity = new EntityConfigPrmtEntity();
				entityConfigPrmtEntity.setGroupCode(dto.getGroupCode());
				groupId = groupInfoDetailsRepository
						.findByGroupId(dto.getGroupCode());
				entityConfigPrmtEntity.setGroupId(groupId);
				entityConfigPrmtEntity.setEntityId(dto.getEntityId());
				ids.add(dto.getId());

				String questionCode = configQuestionRepository
						.findQuestionCode(dto.getQuestId());
				entityConfigPrmtEntity.setConfgPrmtId(dto.getQuestId());
				entityConfigPrmtEntity.setParamValKeyId(questionCode);

				if ("O27".equalsIgnoreCase(questionCode)) {
					entityConfigPrmtEntity
							.setParamValue(dto.getAnswerCode().isEmpty() ? "0"
									: dto.getAnswerCode());
				} else if ("I36".equalsIgnoreCase(questionCode)) {
					entityConfigPrmtEntity.setParamValue(
							Strings.isNullOrEmpty(dto.getAnswerCode()) ? "14,30"
									: dto.getAnswerCode());
				} else {
					entityConfigPrmtEntity.setParamValue(dto.getAnswerCode());
				}
				User user = SecurityContext.getUser();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User Name: {}", user.getUserPrincipalName());
				}
				entityConfigPrmtEntity
						.setCreatedBy(user.getUserPrincipalName());
				entityConfigPrmtEntity.setCreatedOn(
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

				textEntities.add(entityConfigPrmtEntity);
			}
		}
		boolean isElegibleForSave = true;

		if (!ids.isEmpty()) {
			int cnt = entityConfigPrmtRepository
					.deleteExitingAnswerForQuestion(ids, entityId);

			if (cnt == 0)
				isElegibleForSave = false;
		}
		if (!entities.isEmpty() && isElegibleForSave) {
			entityConfigPrmtRepository.saveAll(entities);
		}

		if (!textEntities.isEmpty()) {
			entityConfigPrmtRepository.saveAll(textEntities);
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
