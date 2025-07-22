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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GroupConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.ImsAutomationEntity;
import com.ey.advisory.admin.data.entities.client.ImsSaveAutomationEntity;
import com.ey.advisory.admin.data.entities.client.InwardEinvoiceAutomationEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.ConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.ConfigQuestionRepository;
import com.ey.advisory.admin.data.repositories.client.Get2aAutomationRepository;
import com.ey.advisory.admin.data.repositories.client.Get6aAutomationRepository;
import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ImsAutoSaveRepository;
import com.ey.advisory.admin.data.repositories.client.ImsAutomationRepository;
import com.ey.advisory.admin.data.repositories.client.InwardEinvoiceAutomationRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.EntityConfigPrmtReqDto;
import com.ey.advisory.core.dto.EntityConfigPrmtResDto;
import com.ey.advisory.core.dto.ItemsAnsDto;

@Component("GroupConfigPrmtServiceImpl")
public class GroupConfigPrmtServiceImpl implements EntityConfigPrmtService {

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

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

	@Autowired
	@Qualifier("ImsAutomationRepository")
	private ImsAutomationRepository imsAutoRepo;

	@Autowired
	@Qualifier("ImsAutoSaveRepository")
	private ImsAutoSaveRepository imsAutoSaveRepo;

	public List<EntityConfigPrmtResDto> getEntityConfigPrmt(
			final EntityConfigPrmtReqDto reqDto) {

		List<EntityConfigPrmtResDto> resDtos = new LinkedList<>();

		if ("GROUP".equalsIgnoreCase(reqDto.getType())) {
			getConfigPermit(resDtos, "GROUP", reqDto);
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

					if ("Select GetCall Frequency?"
							.equalsIgnoreCase(pairParameterKey.getValue2())
							&& "G38".equalsIgnoreCase(questionCode)) {
						GroupConfigPrmtEntity entity = new GroupConfigPrmtEntity();

						entity = groupConfigPrmtRepository
								.findEntityByEntityAutoImsGetCallFrequency();

						GroupConfigPrmtEntity entity1 = new GroupConfigPrmtEntity();

						entity1 = groupConfigPrmtRepository
								.findEntityByEntityAutoImsGetCallTimestampFrequency();

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

					if ("Select GetCall Frequency?"
							.equalsIgnoreCase(pairParameterKey.getValue2())
							&& "G40".equalsIgnoreCase(questionCode)) {
						GroupConfigPrmtEntity entity = new GroupConfigPrmtEntity();

						entity = groupConfigPrmtRepository
								.findEntityByEntityAutoImsGetCallFrequency();

						GroupConfigPrmtEntity entity1 = new GroupConfigPrmtEntity();

						entity1 = groupConfigPrmtRepository
								.findEntityByEntityAutoImsGetCallTimestampFrequency();

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
					/*
					 * if ("Select GetCall Frequency?"
					 * .equalsIgnoreCase(pairParameterKey.getValue2()) &&
					 * "I50".equalsIgnoreCase(questionCode)) {
					 * EntityConfigPrmtEntity entity = new
					 * EntityConfigPrmtEntity();
					 * 
					 * entity = entityConfigPrmtRepository
					 * .findEntityByEntityAutoInwardEinvoiceGetCallFrequency(
					 * reqDto.getEntityId());
					 * 
					 * EntityConfigPrmtEntity entity1 = new
					 * EntityConfigPrmtEntity();
					 * 
					 * entity1 = entityConfigPrmtRepository
					 * .findEntityByEntityAutoInwardEinvoiceGetCallTimestampFrequency(
					 * reqDto.getEntityId());
					 * 
					 * if (entity != null && entity1 != null) {
					 * 
					 * if (entity1.getCreatedOn()
					 * .compareTo(entity.getCreatedOn()) >= 0)
					 * 
					 * { resDto.setQuestion(pairParameterKey.getValue2() +
					 * " ( Last updated timestamp: " +
					 * dateChange(entity1.getCreatedOn() .toString()) + " ) ");
					 * } else { resDto.setQuestion(pairParameterKey.getValue2()
					 * + " ( Last updated timestamp: " +
					 * dateChange(entity.getCreatedOn() .toString()) + " ) "); }
					 * } else { resDto.setQuestion(pairParameterKey.getValue2()
					 * + " ( Last updated timestamp: " + ") "); } }
					 */
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
					Long selectedId = groupConfigPrmtRepository
							.findByGroupCodeAndConfgPrmtId(
									reqDto.getGroupCode(),
									pairParameterKey.getValue0());

					String selectedAnswer = groupConfigPrmtRepository
							.findAnswerById(selectedId);
					resDto.setId(selectedId);
					// find a index selected Answer
					if (selectedAnswer != null && answerCodes != null
							&& !answerCodes.isEmpty()) {
						for (int i = 0; i < answerCodes.size(); i++) {

							if ("I26".equalsIgnoreCase(questionCode)) {
								/*
								 * String answer =
								 * selectedAnswer.split("\\*")[0]; // D*1,12,23
								 * // //11:12 // //A/B/C if
								 * (answerCodes.get(i).equals(answer)) {
								 * resDto.setSelected(i); if (selectedAnswer
								 * .split("\\*").length == 2) { String
								 * answerDesc = selectedAnswer .split("\\*")[1];
								 * resDto.setAnswerDesc(answerDesc); } } else if
								 * (selectedAnswer.contains(":")) {
								 * resDto.setSelected(0); resDto.setGet2AHour(
								 * selectedAnswer.split(":")[0]);
								 * resDto.setGet2Amin(
								 * selectedAnswer.split(":")[1]); }
								 */} else if ("I36"
										.equalsIgnoreCase(questionCode)) {
								/*
								 * if (LOGGER.isDebugEnabled()) { LOGGER.debug(
								 * "Selected answer for Auto 6A get call frequency"
								 * ); }
								 * 
								 * resDto.setSelected(i);
								 * resDto.setAnswerDesc(selectedAnswer);
								 */} else if ("I50"
										.equalsIgnoreCase(questionCode)) {
								/*
								 * String answer =
								 * selectedAnswer.split("\\*")[0]; if
								 * (answerCodes.get(i).equals(answer)) {
								 * resDto.setSelected(i); if (selectedAnswer
								 * .split("\\*").length == 2) { String
								 * answerDesc = selectedAnswer .split("\\*")[1];
								 * resDto.setAnswerDesc(answerDesc); } } //
								 * //11:12 // //A/B/C else if
								 * (selectedAnswer.contains(":")) {
								 * resDto.setSelected(0); resDto.setGet2AHour(
								 * selectedAnswer.split(":")[0]);
								 * 
								 * resDto.setGet2Amin(
								 * selectedAnswer.split(":")[1]);
								 * 
								 * }
								 */} else if ("G38"
										.equalsIgnoreCase(questionCode)) {
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
							} else if ("G40".equalsIgnoreCase(questionCode)) {
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
					Long selectedId = groupConfigPrmtRepository
							.findByGroupCodeAndConfgPrmtId(
									reqDto.getGroupCode(),
									pairParameterKey.getValue0());
					String selectedAnswer = groupConfigPrmtRepository
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
					Long selectedId = groupConfigPrmtRepository
							.findByGroupCodeAndConfgPrmtId(
									reqDto.getGroupCode(),
									pairParameterKey.getValue0());
					String selectedAnswer = groupConfigPrmtRepository
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
					Long selectedId = groupConfigPrmtRepository
							.findByGroupCodeAndConfgPrmtId(
									reqDto.getGroupCode(),
									pairParameterKey.getValue0());
					String selectedAnswer = groupConfigPrmtRepository
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
					Long selectedId = groupConfigPrmtRepository
							.findByGroupCodeAndConfgPrmtId(
									reqDto.getGroupCode(),
									pairParameterKey.getValue0());
					String selectedAnswer = groupConfigPrmtRepository
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

		// insert into ims automation table
		String ims = groupConfigPrmtRepository.findByEntityAutoInitiateGetCall(
				"Whether Auto Get Call of IMS data is to be enabled?", "G38");

		String imsGetCallFreq = groupConfigPrmtRepository
				.findAutoImsGetCallFrequency();

		String imsGetCallTimestamp = groupConfigPrmtRepository
				.getTimeStampForEntityAutoIms();

		imsGetCallTimestamp = imsGetCallTimestamp == null ? "12:00"
				: imsGetCallTimestamp;
		YearMonth dateMonth = YearMonth.now();
		User user = SecurityContext.getUser();
		String endOfMonth = String
				.valueOf(dateMonth.atEndOfMonth().getDayOfMonth());
		List<ImsAutomationEntity> imsAutomationEntities = new ArrayList<>();
		if (ims != null) {
			if ("A".equalsIgnoreCase(ims)) {
				imsAutoRepo.updateInActiveIms();

				if ("A".equalsIgnoreCase(imsGetCallFreq)) {
					// Daily
					String time = imsGetCallTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					StringBuilder uniqueKey = new StringBuilder();
					uniqueKey.append(0);
					uniqueKey.append("|");
					uniqueKey.append(dailyTimePeriod);
					uniqueKey.append("|");
					uniqueKey.append(1);
					uniqueKey.append("|");
					uniqueKey.append("D");
					uniqueKey.append("|");
					uniqueKey.append(false);

					ImsAutomationEntity imsAutoEntity = new ImsAutomationEntity();
					imsAutoEntity.setCalendarTime(dailyTimePeriod);
					imsAutoEntity.setEntityId(Long.valueOf(0));
					imsAutoEntity.setUniqueKey(uniqueKey.toString());
					imsAutoEntity.setDelete(false);
					imsAutoEntity.setCreatedBy(user.getUserPrincipalName());
					imsAutoEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					imsAutoEntity.setGetEvent("D");
					imsAutoEntity.setLastPostedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					imsAutomationEntities.add(imsAutoEntity);

				} else if ("D".equalsIgnoreCase(imsGetCallFreq)) {
					// Only Montly
					String time = imsGetCallTimestamp + ":" + "00";
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

					ImsAutomationEntity imsAutoEntity = new ImsAutomationEntity();
					imsAutoEntity.setCalendarTime(dailyTimePeriod);
					imsAutoEntity.setEntityId(Long.valueOf(0));
					imsAutoEntity.setUniqueKey(uniqueKey2.toString());
					imsAutoEntity.setDelete(false);
					imsAutoEntity.setCreatedBy(user.getUserPrincipalName());
					imsAutoEntity.setCalendarDateAsString(montlyDate);
					imsAutoEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					imsAutoEntity.setGetEvent("M");
					imsAutoEntity.setLastPostedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					imsAutomationEntities.add(imsAutoEntity);

				} else if ("B".equalsIgnoreCase(imsGetCallFreq)) {
					// Weekly
					String time = imsGetCallTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

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

						ImsAutomationEntity imsAutoEntity = new ImsAutomationEntity();
						imsAutoEntity.setCalendarTime(dailyTimePeriod);
						imsAutoEntity.setEntityId(Long.valueOf(0));
						imsAutoEntity.setUniqueKey(weeklyUniqueKey.toString());
						imsAutoEntity.setCalendarDateAsString(date);
						imsAutoEntity.setDelete(false);
						imsAutoEntity.setCreatedBy(user.getUserPrincipalName());
						imsAutoEntity.setCreatedOn(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						imsAutoEntity.setGetEvent("W");
						imsAutoEntity.setLastPostedDate(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						imsAutomationEntities.add(imsAutoEntity);
					}
				} else if ("C".equalsIgnoreCase(imsGetCallFreq)) {
					// fortnight
					String time = imsGetCallTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					List<String> forthNightDates = Arrays.asList("14", "28");

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

						ImsAutomationEntity imsAutoEntity = new ImsAutomationEntity();
						imsAutoEntity.setCalendarTime(dailyTimePeriod);
						imsAutoEntity.setCalendarDateAsString(date);
						imsAutoEntity.setEntityId(Long.valueOf(0));
						imsAutoEntity
								.setUniqueKey(fortNightUniqueKey.toString());
						imsAutoEntity.setDelete(false);
						imsAutoEntity.setCreatedBy(user.getUserPrincipalName());
						imsAutoEntity.setCreatedOn(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						imsAutoEntity.setGetEvent("F");
						imsAutoEntity.setLastPostedDate(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						imsAutomationEntities.add(imsAutoEntity);
					}
				} else {

					// Custom dates only
					String time = imsGetCallTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					String dates = imsGetCallFreq.split("\\*")[1];

					if (dates == null) {
						LOGGER.error(
								"No dates selected by user for inward einvoice entity : {}",
								reqDto.getEntityId());
						return;
					}
					List<String> userSelectedDates = Arrays
							.asList(dates.split(","));
					List<String> maxEightDates = userSelectedDates.stream()
							.limit(8).collect(Collectors.toList());
					maxEightDates.replaceAll(String::trim);
					List<Integer> userDates = maxEightDates.stream()
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

						ImsAutomationEntity imsAutoEntity = new ImsAutomationEntity();
						imsAutoEntity.setCalendarTime(dailyTimePeriod);
						imsAutoEntity.setCalendarDateAsString(dt);
						imsAutoEntity.setEntityId(Long.valueOf(0));
						imsAutoEntity
								.setUniqueKey(fortNightUniqueKey.toString());
						imsAutoEntity.setDelete(false);
						imsAutoEntity.setCreatedBy(user.getUserPrincipalName());
						imsAutoEntity.setCreatedOn(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						imsAutoEntity.setGetEvent("C");
						imsAutoEntity.setLastPostedDate(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						imsAutomationEntities.add(imsAutoEntity);
					}
				}
			}
		} else if ("B".equalsIgnoreCase(ims)) {
			imsAutoRepo.updateInActiveIms();
		}

		if (!imsAutomationEntities.isEmpty())

		{
			imsAutoRepo.saveAll(imsAutomationEntities);
		}

		// insert into ims Auto Save table
		String imsAutoSave = groupConfigPrmtRepository.findByEntityAutoInitiateGetCall(
				"Whether auto save of IMS Action Response is to be enabled?", "G40");

		String imsAutoSaveFreq = groupConfigPrmtRepository
				.findAutoImsAutoSaveFrequency();

		String imsAutoSaveTimestamp = groupConfigPrmtRepository
				.getTimeStampForEntityAutoSaveIms();

		imsAutoSaveTimestamp = imsAutoSaveTimestamp == null ? "12:00"
				: imsAutoSaveTimestamp;

		List<ImsSaveAutomationEntity> imsAutoSaveEntities = new ArrayList<>();
		if (imsAutoSave != null) {
			if ("A".equalsIgnoreCase(imsAutoSave)) {
				imsAutoSaveRepo.updateInActiveIms();

				if ("A".equalsIgnoreCase(imsAutoSaveFreq)) {
					// Daily
					String time = imsAutoSaveTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					StringBuilder uniqueKey = new StringBuilder();
					uniqueKey.append(0);
					uniqueKey.append("|");
					uniqueKey.append(dailyTimePeriod);
					uniqueKey.append("|");
					uniqueKey.append(1);
					uniqueKey.append("|");
					uniqueKey.append("D");
					uniqueKey.append("|");
					uniqueKey.append(false);

					ImsSaveAutomationEntity imsAutoEntity = new ImsSaveAutomationEntity();
					imsAutoEntity.setCalendarTime(dailyTimePeriod);
					imsAutoEntity.setEntityId(Long.valueOf(0));
					imsAutoEntity.setUniqueKey(uniqueKey.toString());
					imsAutoEntity.setDelete(false);
					imsAutoEntity.setCreatedBy(user.getUserPrincipalName());
					imsAutoEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					imsAutoEntity.setGetEvent("D");
					imsAutoEntity.setLastPostedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					imsAutoSaveEntities.add(imsAutoEntity);

				} else if ("E".equalsIgnoreCase(imsAutoSaveFreq)) {
					// Only Montly
					String time = imsAutoSaveTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					String montlyDate = "14";

					StringBuilder uniqueKey2 = new StringBuilder();
					uniqueKey2.append(0);
					uniqueKey2.append("|");
					uniqueKey2.append(dailyTimePeriod);
					uniqueKey2.append("|");
					uniqueKey2.append(montlyDate);
					uniqueKey2.append("|");
					uniqueKey2.append("M");
					uniqueKey2.append("|");
					uniqueKey2.append(true);

					ImsSaveAutomationEntity imsAutoEntity = new ImsSaveAutomationEntity();
					imsAutoEntity.setCalendarTime(dailyTimePeriod);
					imsAutoEntity.setEntityId(Long.valueOf(0));
					imsAutoEntity.setUniqueKey(uniqueKey2.toString());
					imsAutoEntity.setDelete(false);
					imsAutoEntity.setCreatedBy(user.getUserPrincipalName());
					imsAutoEntity.setCalendarDateAsString(montlyDate);
					imsAutoEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					imsAutoEntity.setGetEvent("M");
					imsAutoEntity.setLastPostedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					imsAutoSaveEntities.add(imsAutoEntity);

				} else if ("B".equalsIgnoreCase(imsAutoSaveFreq)) {
					// Weekly
					String time = imsAutoSaveTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					List<String> weeklyDates = Arrays.asList("5", "14", "21",
							"28");

					for (String date : weeklyDates) {
						StringBuilder weeklyUniqueKey = new StringBuilder();
						weeklyUniqueKey.append(0);
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append(dailyTimePeriod);
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append(date);
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append("W");
						weeklyUniqueKey.append("|");
						weeklyUniqueKey.append(true);

						ImsSaveAutomationEntity imsAutoEntity = new ImsSaveAutomationEntity();
						imsAutoEntity.setCalendarTime(dailyTimePeriod);
						imsAutoEntity.setEntityId(Long.valueOf(0));
						imsAutoEntity.setUniqueKey(weeklyUniqueKey.toString());
						imsAutoEntity.setCalendarDateAsString(date);
						imsAutoEntity.setDelete(false);
						imsAutoEntity.setCreatedBy(user.getUserPrincipalName());
						imsAutoEntity.setCreatedOn(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						imsAutoEntity.setGetEvent("W");
						imsAutoEntity.setLastPostedDate(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						imsAutoSaveEntities.add(imsAutoEntity);
					}
				} else if ("D".equalsIgnoreCase(imsAutoSaveFreq)) {
					// fortnight
					String time = imsAutoSaveTimestamp + ":" + "00";
					LocalTime dailyTimePeriod = LocalTime.parse(time,
							DateTimeFormatter.ISO_TIME);

					List<String> forthNightDates = Arrays.asList("14", "28");

					for (String date : forthNightDates) {
						StringBuilder fortNightUniqueKey = new StringBuilder();
						fortNightUniqueKey.append(0);
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append(dailyTimePeriod);
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append(date);
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append("F");
						fortNightUniqueKey.append("|");
						fortNightUniqueKey.append(true);

						ImsSaveAutomationEntity imsAutoEntity = new ImsSaveAutomationEntity();
						imsAutoEntity.setCalendarTime(dailyTimePeriod);
						imsAutoEntity.setCalendarDateAsString(date);
						imsAutoEntity.setEntityId(Long.valueOf(0));
						imsAutoEntity
								.setUniqueKey(fortNightUniqueKey.toString());
						imsAutoEntity.setDelete(false);
						imsAutoEntity.setCreatedBy(user.getUserPrincipalName());
						imsAutoEntity.setCreatedOn(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						imsAutoEntity.setGetEvent("F");
						imsAutoEntity.setLastPostedDate(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						imsAutoSaveEntities.add(imsAutoEntity);
					}
				} else {
					String time = imsAutoSaveTimestamp + ":" + "00";
				    LocalTime dailyTimePeriod = LocalTime.parse(time, DateTimeFormatter.ISO_TIME);

				    List<String> twiceAWeekDates = Arrays.asList("1", "5", "12", "14", "18", "21", "25", "28");

				    for (String date : twiceAWeekDates) {
				        StringBuilder twiceAWeekUniqueKey = new StringBuilder();
				        twiceAWeekUniqueKey.append(0);
				        twiceAWeekUniqueKey.append("|");
				        twiceAWeekUniqueKey.append(dailyTimePeriod);
				        twiceAWeekUniqueKey.append("|");
				        twiceAWeekUniqueKey.append(date);
				        twiceAWeekUniqueKey.append("|");
				        twiceAWeekUniqueKey.append("T");
				        twiceAWeekUniqueKey.append("|");
				        twiceAWeekUniqueKey.append(true);

				        ImsSaveAutomationEntity imsAutoEntity = new ImsSaveAutomationEntity();
				        imsAutoEntity.setCalendarTime(dailyTimePeriod);
				        imsAutoEntity.setCalendarDateAsString(date);
				        imsAutoEntity.setEntityId(Long.valueOf(0));
				        imsAutoEntity.setUniqueKey(twiceAWeekUniqueKey.toString());
				        imsAutoEntity.setDelete(false);
				        imsAutoEntity.setCreatedBy(user.getUserPrincipalName());
				        imsAutoEntity.setCreatedOn(EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				        imsAutoEntity.setGetEvent("T");
				        imsAutoEntity.setLastPostedDate(EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				        imsAutoSaveEntities.add(imsAutoEntity);
				    }
				}
			}
		} else if ("B".equalsIgnoreCase(imsAutoSave)) {
			imsAutoSaveRepo.updateInActiveIms();
		}

		if (!imsAutoSaveEntities.isEmpty())

		{
			imsAutoSaveRepo.saveAll(imsAutoSaveEntities);
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

		Set<GroupConfigPrmtEntity> entities = new HashSet<>();
		List<Long> ids = new ArrayList<>();
		Integer derivedPeriod = null;

		Long groupId = null;
		for (EntityConfigPrmtReqDto dto : dtos) {
			String answer = groupConfigPrmtRepository
					.findParamValByGroupCodeAndIdAndAnswer(dto.getGroupCode(),
							dto.getQuestId(), dto.getId(), dto.getAnswerCode());
			if (answer == null) {
				GroupConfigPrmtEntity entityConfigPrmtEntity = new GroupConfigPrmtEntity();
				entityConfigPrmtEntity.setGroupCode(dto.getGroupCode());
				groupId = groupInfoDetailsRepository
						.findByGroupId(dto.getGroupCode());
				entityConfigPrmtEntity.setGroupId(groupId);
				ids.add(dto.getId());

				// Soft delete for
				// entityConfigPrmtRepository.updateIsDeleteFlagFlase(
				// dto.getGroupCode(), dto.getEntityId(),
				// dto.getQuestId());
				String questionCode = configQuestionRepository
						.findQuestionCode(dto.getQuestId());
				entityConfigPrmtEntity.setConfgPrmtId(dto.getQuestId());
				entityConfigPrmtEntity.setParamValKeyId(questionCode);

				entityConfigPrmtEntity.setParamValue(dto.getAnswerCode());

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
					groupConfigPrmtRepository.updateDerivedReturnPeriod(groupId,
							derivedPeriod, answer);
				}
				entities.add(entityConfigPrmtEntity);
			}
		}
		if (!ids.isEmpty()) {
			groupConfigPrmtRepository.deleteExitingAnswerForQuestion(ids);
		}
		if (!entities.isEmpty()) {
			groupConfigPrmtRepository.saveAll(entities);
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
