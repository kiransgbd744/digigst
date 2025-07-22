package com.ey.advisory.admin.onboarding.controller;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.ConfigQuestionEntity;
import com.ey.advisory.admin.data.entities.client.EntityAtConfigEntity;
import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.ConfigQuestionRepository;
import com.ey.advisory.admin.data.repositories.client.EntityAtConfiRepository;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.GstinElDetailsObjArrToEntityConverter;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.OnboardingFileTraverserFactory;
import com.ey.advisory.app.util.HeaderCheckerUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Sasidhar Reddy
 *
 */

@Service("EntityDetailsFileUploadHelper")
public class EntityDetailsFileUploadHelper {

	protected static final String[] EXPECTED_HEADERS = { "EntityName", "PAN",
			"CompanyCode" };

	protected static final String[] HEADERS_ARR = { "Plant Code", "Division",
			"Sub Division", "Location", "Sales Organization",
			"Distribution Channel", "Purchase Organization", "Profit Centre 1",
			"Profit Centre 2", "Profit Centre 3", "Profit Centre 4",
			"Profit Centre 5", "Profit Centre 6", "Profit Centre 7",
			"Profit Centre 8", "Source ID" };

	protected static final String[] AT_CODE_ARR = { "Plant", "D", "SD", "L",
			"SO", "DC", "PO", "PC", "PC2", "UD1", "UD2", "UD3", "UD4", "UD5",
			"UD6", "SI" };

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EntityDetailsFileUploadHelper.class);

	private static final String OUTWARD = "OUTWARD";
	private static final String INWARD = "INWARD";
	private static final String GENERAL = "GENERAL";
	private static final String RADIO = "R";
	private static final String TEXT = "T";
	private static final String RADIOTEXT = "RT";
	private static final String MULTI_SELECT = "M";
	private static final String SUBRADIO = "SR";

	@Autowired
	@Qualifier("OnboardingDefaultTraverserFactoryImpl")
	private OnboardingFileTraverserFactory onboardingFileTraverserFactory;

	@Autowired
	@Qualifier("GstinElDetailsObjArrToEntityConverter")
	private GstinElDetailsObjArrToEntityConverter gstinElDetailsObjArrToEntityConverter;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepository;

	@Autowired
	@Qualifier("HeaderCheckerUtil")
	private HeaderCheckerUtil headerCheckerUtil;

	@Autowired
	@Qualifier("entityAtConfiRepository")
	private EntityAtConfiRepository entityAtConfiRepository;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPrmtRepository;

	@Autowired
	@Qualifier("ConfigQuestionRepository")
	private ConfigQuestionRepository configQuestionRepository;

	public ResponseEntity<String> gstinEntitlementEntityUpload(
			@RequestParam("file") MultipartFile[] files, String groupCode,
			Long groupId) throws Exception {

		try {
		    // Assuming that the multi-part request will have only one part.
		    // Hence, directly accessing the first element.
		    MultipartFile file = files[0];

		    // Get the uploaded file name and a reference to the input stream of
		    // the uploaded file.
		    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		    InputStream stream = file.getInputStream();

		    TabularDataSourceTraverser traverser = onboardingFileTraverserFactory.getTraverser(fileName);
		    TabularDataLayout layout = new DummyTabularDataLayout(3);

		    // Add a dummy row handler that will keep counting the rows.
		    @SuppressWarnings("rawtypes")
		    FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
		    traverser.traverse(stream, layout, rowHandler, null);
		    Object[] getHeaders = rowHandler.getHeaderRow();
		    List<Object[]> list = rowHandler.getFileUploadList();
		    removeNullRecords(list, 3);

		    Pair<Boolean, String> pair = headerCheckerUtil.validateHeaders(EXPECTED_HEADERS, getHeaders);

		    if (pair != null && Boolean.TRUE.equals(pair.getValue0())) { // Check for null and use safe comparison
		        Pair<List<String>, List<EntityInfoEntity>> filePairs = gstinElDetailsObjArrToEntityConverter
		                .convertDataIntoRecords(list, groupCode, groupId);
		        List<String> errorsAddressed = filePairs.getValue0();

		        if (errorsAddressed != null && !errorsAddressed.isEmpty()) {
		            return createGstinRegFailureResp(errorsAddressed);
		        } else {
		            List<EntityInfoEntity> recordsToBeInserted = filePairs.getValue1();
		            Iterable<EntityInfoEntity> entities = entityInfoRepository.saveAll(recordsToBeInserted);
		            List<EntityConfigPrmtEntity> configPrmEntities = new ArrayList<>();
		            saveEntityConfigParam(entities, configPrmEntities);
		            entityConfigPrmtRepository.saveAll(configPrmEntities);
		            createAtConfigsForOrganization(groupCode, groupId, recordsToBeInserted);
		            return createGstinRegSuccessResp();
		        }
		    } else if (pair != null) { // Handle case where pair is not null but getValue0() is false
		        List<String> list1 = new ArrayList<>();
		        list1.add(pair.getValue1());
		        return createGstinRegFailureResp(list1);
		    } else {
		        throw new IllegalArgumentException("Header validation failed: pair is null");
		    }

			
			/*
			// Assuming that the multi-part request will have only one part.
			// Hence, directly accessing the first element.
			MultipartFile file = files[0];

			// Get the uploaded file name and a reference to the input stream of
			// the uploaded file.
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());

			InputStream stream = file.getInputStream();

			TabularDataSourceTraverser traverser = onboardingFileTraverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(3);

			// Add a dummy row handler that will keep counting the rows.
			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(stream, layout, rowHandler, null);
			Object[] getHeaders = rowHandler.getHeaderRow();
			List<Object[]> list = rowHandler.getFileUploadList();
			removeNullRecords(list, 3);

			Pair<Boolean, String> pair = headerCheckerUtil
					.validateHeaders(EXPECTED_HEADERS, getHeaders);

			if (pair != null && pair.getValue0()) {

				Pair<List<String>, List<EntityInfoEntity>> filePairs = gstinElDetailsObjArrToEntityConverter
						.convertDataIntoRecords(list, groupCode, groupId);
				List<String> errorsAddressed = filePairs.getValue0();
				if ((errorsAddressed != null && !errorsAddressed.isEmpty())) {
					return createGstinRegFailureResp(errorsAddressed);
				} else {
					List<EntityInfoEntity> reordsToBeInserted = filePairs
							.getValue1();
					Iterable<EntityInfoEntity> entities = entityInfoRepository
							.saveAll(reordsToBeInserted);
					List<EntityConfigPrmtEntity> configPrmEntities = new ArrayList<>();
					saveEntityConfigParam(entities, configPrmEntities);
					entityConfigPrmtRepository.saveAll(configPrmEntities);
					createAtConfigsForOrganization(groupCode, groupId,
							reordsToBeInserted);
					return createGstinRegSuccessResp();
				}
			} else {
				List<String> list1 = new ArrayList<>();
				list1.add(pair.getValue1());
				return createGstinRegFailureResp(list1);
			}
		*/} catch (Exception e) {

		    Gson gson = GsonUtil.newSAPGsonInstance();
		    APIRespDto dto = new APIRespDto("Failed", e.getMessage());
		    JsonObject resp = new JsonObject();
		    JsonElement respBody = gson.toJsonTree(dto);
		    resp.add("resp", respBody);
		    LOGGER.error("Exception Occurred:", e);
		    return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			
			/*
			Gson gson = GsonUtil.newSAPGsonInstance();
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("resp", respBody);
			LOGGER.error("Exption Occred:", e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);*/
		}
	}

	private void saveEntityConfigParam(Iterable<EntityInfoEntity> entities,
			List<EntityConfigPrmtEntity> configPrmEntities) {
		if (entities != null) {
			entities.forEach(entity -> {
				List<ConfigQuestionEntity> quetionEntities = configQuestionRepository
						.getAllInwardAndOutwardQuestions();
				quetionEntities.forEach(quetionEntity -> {
					EntityConfigPrmtEntity entityConfigPrmtEntity = new EntityConfigPrmtEntity();
					entityConfigPrmtEntity.setEntityId(entity.getId());
					entityConfigPrmtEntity.setGroupCode(entity.getGroupCode());
					entityConfigPrmtEntity.setGroupId(entity.getGroupId());
					entityConfigPrmtEntity
							.setConfgPrmtId(quetionEntity.getId());
					entityConfigPrmtEntity
							.setParamValKeyId(quetionEntity.getQuestionCode());

					// If Parameter Category is Outward or Inward then we to
					// update answer based on question code
					if (OUTWARD.equalsIgnoreCase(
							quetionEntity.getQuestionCategory())
							|| INWARD.equalsIgnoreCase(
									quetionEntity.getQuestionCategory())) {
						if ("O6".equalsIgnoreCase(
								quetionEntity.getQuestionCode())
								|| "I1".equalsIgnoreCase(
										quetionEntity.getQuestionCode())
								|| "I2".equalsIgnoreCase(
										quetionEntity.getQuestionCode())
								|| "I5".equalsIgnoreCase(
										quetionEntity.getQuestionCode())
								|| "I7".equalsIgnoreCase(
										quetionEntity.getQuestionCode())
								|| "O3".equalsIgnoreCase(
										quetionEntity.getQuestionCode())) {
							entityConfigPrmtEntity.setParamValue("C");
						} else if (RADIO.equalsIgnoreCase(
								quetionEntity.getQuestionType())
								&& ("I13".equalsIgnoreCase(
										quetionEntity.getQuestionCode())
										|| "I4".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I6".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I10".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I11".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I12".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I20".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "O12".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I15".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "O13".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I19".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "O19".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I25".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "O23".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "O20".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "O26".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "O27".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I29".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I24".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I30".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I31".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I33".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "O16".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I37".equalsIgnoreCase(quetionEntity
												.getQuestionCode()))) {
							entityConfigPrmtEntity.setParamValue("A");
						} else if ((SUBRADIO.equalsIgnoreCase(
								quetionEntity.getQuestionType()))
								&& "I26".equalsIgnoreCase(
										quetionEntity.getQuestionCode())) {
							entityConfigPrmtEntity.setParamValue("A");
						} else if ("O8".equalsIgnoreCase(
								quetionEntity.getQuestionCode())
									|| "I9".equalsIgnoreCase(
										quetionEntity.getQuestionCode())) {
							entityConfigPrmtEntity.setParamValue("D");
						} else if (TEXT.equalsIgnoreCase(
								quetionEntity.getQuestionType())
								&& ("I13".equalsIgnoreCase(
										quetionEntity.getQuestionCode())
										|| "O24".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "O27".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I14".equalsIgnoreCase(quetionEntity
												.getQuestionCode()))) {
							entityConfigPrmtEntity.setParamValue("10");

							/*
							 * } else if (TEXT.equalsIgnoreCase(
							 * quetionEntity.getQuestionType()) &&
							 * ("I13".equalsIgnoreCase(
							 * quetionEntity.getQuestionCode()))) {
							 * entityConfigPrmtEntity.setParamValue("100");
							 */
						} else if (TEXT.equalsIgnoreCase(
								quetionEntity.getQuestionType())
								&& ("I38".equalsIgnoreCase(
										quetionEntity.getQuestionCode()))) {
							entityConfigPrmtEntity.setParamValue("70");

						} else if (TEXT.equalsIgnoreCase(
								quetionEntity.getQuestionType())
								&& ("I56".equalsIgnoreCase(
										quetionEntity.getQuestionCode())
										|| "I57".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I58".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I59".equalsIgnoreCase(quetionEntity
												.getQuestionCode())
										|| "I60".equalsIgnoreCase(quetionEntity
												.getQuestionCode())
										|| "I61".equalsIgnoreCase(quetionEntity
												.getQuestionCode())
										|| "I62".equalsIgnoreCase(quetionEntity
												.getQuestionCode()))) {
							entityConfigPrmtEntity.setParamValue("0");

							/*
							 * } else if (TEXT.equalsIgnoreCase(
							 * quetionEntity.getQuestionType()) &&
							 * ("I13".equalsIgnoreCase(
							 * quetionEntity.getQuestionCode()))) {
							 * entityConfigPrmtEntity.setParamValue("100");
							 */
						}else if (TEXT.equalsIgnoreCase(
								quetionEntity.getQuestionType())
								&& ("O18".equalsIgnoreCase(
										quetionEntity.getQuestionCode())
										|| "I23".equalsIgnoreCase(
												quetionEntity.getQuestionCode())
										|| "I22".equalsIgnoreCase(quetionEntity
												.getQuestionCode()))) {
							entityConfigPrmtEntity.setParamValue("0");
						} else if (RADIOTEXT.equalsIgnoreCase(
								quetionEntity.getQuestionType())
								&& "I17".equalsIgnoreCase(
										quetionEntity.getQuestionCode())) {
							entityConfigPrmtEntity.setParamValue("A*0");
						} else if (MULTI_SELECT.equalsIgnoreCase(
								quetionEntity.getQuestionType())
								&& "O11".equalsIgnoreCase(
										quetionEntity.getQuestionCode())) {
							entityConfigPrmtEntity.setParamValue("A*B*C");
						} else {
							entityConfigPrmtEntity.setParamValue("B");
						}
						// If Parameter Category is General then we to update
						// answer based on question code
					} else if (GENERAL.equalsIgnoreCase(
							quetionEntity.getQuestionCategory())) {

						// If Question code is G9 then we have to find answer
						// and Derived Answer
						if ("G9".equalsIgnoreCase(
								quetionEntity.getQuestionCode())) {
							String answer = entityConfigPrmtRepository
									.findMaxAnswer(entity.getGroupId(),
											entity.getId());
							Integer derivedAnswer = entityConfigPrmtRepository
									.findMaxDerivedAnswer(entity.getGroupId(),
											entity.getId());
							// If answer and Derived Answer is not null then we
							// have to update answer and Deriver Answer
							if (answer != null && derivedAnswer != null) {
								entityConfigPrmtEntity.setParamValue(answer);
								entityConfigPrmtEntity
										.setDerivedAnswer(derivedAnswer);
								// If answer and Derived Answer is null then we
								// have to update like
								// 042020 in answer and like 202004 in Deriver
								// Answer
							} else {
								entityConfigPrmtEntity.setParamValue("042020");
								entityConfigPrmtEntity.setDerivedAnswer(202004);
							}
						} else if ("G10".equalsIgnoreCase(
								quetionEntity.getQuestionCode())) {
							entityConfigPrmtEntity.setParamValue("C");
						} else if ("G11".equalsIgnoreCase(
								quetionEntity.getQuestionCode())
								|| "G12".equalsIgnoreCase(
										quetionEntity.getQuestionCode())
								|| "G13".equalsIgnoreCase(
										quetionEntity.getQuestionCode())) {
							entityConfigPrmtEntity.setParamValue("B");
						} else {
							// If expect G9 we have all question for answer
							// update as an 'A' by default.
							entityConfigPrmtEntity.setParamValue("A");
						}
					}

					User user = SecurityContext.getUser();
					entityConfigPrmtEntity
							.setCreatedBy(user.getUserPrincipalName());
					entityConfigPrmtEntity.setCreatedOn(LocalDateTime.now());
					configPrmEntities.add(entityConfigPrmtEntity);
				});
			});
		}

	}

	private void createAtConfigsForOrganization(String groupCode, Long groupId,
			List<EntityInfoEntity> reordsToBeInserted) {

		List<Long> entityIds = new ArrayList<Long>();
		reordsToBeInserted.forEach(entity -> {
			Long entityId = entityInfoRepository
					.findEntityIdByEntityNameAndPanNumber(groupCode,
							entity.getEntityName(), entity.getPan());
			entityIds.add(entityId);
		});

		if (entityIds != null && entityIds.size() > 0) {
			entityIds.forEach(entityId -> {

				List<EntityAtConfigEntity> updateEntityAtConfigEntities = new ArrayList<>();

				for (int i = 0; i < HEADERS_ARR.length; i++) {
					EntityAtConfigEntity atConfigEntity = new EntityAtConfigEntity();
					atConfigEntity.setGroupId(groupId);
					atConfigEntity.setGroupcode(groupCode);
					atConfigEntity.setEntityId(entityId);
					atConfigEntity.setAtCode(AT_CODE_ARR[i]);
					atConfigEntity.setAtName(HEADERS_ARR[i]);
					atConfigEntity.setAtInward("N");
					atConfigEntity.setAtOutward("N");
					atConfigEntity.setDelete(false);
					atConfigEntity
							.setCreatedBy(System.getProperty("user.name"));
					atConfigEntity.setCreatedOn(LocalDateTime.now());
					atConfigEntity
							.setModifiedBy(System.getProperty("user.name"));
					atConfigEntity.setModifiedOn(LocalDateTime.now());

					updateEntityAtConfigEntities.add(atConfigEntity);
				}

				entityAtConfiRepository.saveAll(updateEntityAtConfigEntities);
			});
		}
	}

	private void removeNullRecords(List<Object[]> list, int count) {
		List<Object[]> matchedNullList = new ArrayList<Object[]>();
		list.forEach(obj -> {
			List<Object> nullList = Arrays.asList(obj).stream()
					.filter(val -> val == null).collect(Collectors.toList());
			if (Arrays.asList(obj).contains(null) && nullList.size() == count) {
				matchedNullList.add(obj);
			}
		});

		list.removeAll(matchedNullList);
	}

	public ResponseEntity<String> createGstinRegSuccessResp() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIRespDto dto = new APIRespDto("Success",
				"File uploaded successfully check your status in ELDetails Tab.");
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> createGstinRegFailureResp(List<String> info) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIRespDto dto = new APIRespDto("Failed",
				"File uploaded Failed -> " + info);
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		String msg = "Unexpected error while uploading  files.";
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		resp.add("resp", respBody);
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}
