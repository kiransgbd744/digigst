package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_INWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service("DefaultInwardOrginalDocCheckServiceImpl")
@Slf4j
public class DefaultInwardOrginalDocCheckServiceImpl
		implements OriginalDocCheckService {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private DocKeyGenerator<InwardTransDocument, String> docKeyGen;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository docHeaderRepository;

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	private static final List<String> DOC_TYPE = ImmutableList.of(
			GSTConstants.CR, GSTConstants.DR, GSTConstants.RCR,
			GSTConstants.RDR);
	// private static final List<String> CR_DOC_TYPE =
	// ImmutableList.of(GSTConstants.CR, GSTConstants.DR);
	// private static final List<String> RCR_DOC_TYPE =
	// ImmutableList.of(GSTConstants.RCR,GSTConstants.RDR);

	@Override
	public Map<String, List<ProcessingResult>> checkForInwardCrDrOrgInvoices(
			List<InwardTransDocument> docs, ProcessingContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Function to check whether orginal invoice is present or not in the db for "
							+ "inward file.");
			LOGGER.debug(msg);
		}

		Set<String> docKeySet = new HashSet<>();
		List<String> docKeys = new ArrayList<>();
		List<Object[]> orgDocDetails = new ArrayList<>();

		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();

		try {
			List<InwardTransDocument> crDrDocs = docs.stream()
					.filter(doc -> DOC_TYPE.contains(doc.getDocType())
							&& !doc.isDeleted()
							&& !GSTConstants.CAN
									.equalsIgnoreCase(doc.getSupplyType()))
					.collect(Collectors.toList());
			crDrDocs.forEach(
					doc -> docKeySet.add(docKeyGen.generateOrgKey(doc)));
			docKeys = new ArrayList<>(docKeySet);

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"The number of dockeys recieved from the iteration is from inward file : "
								+ docKeys.size());
			}

			Config config = configManager.getConfig("EYInternal",
					"outward.save.chunksize");
			String chnkSizeStr = config != null ? config.getValue() : "2000";
			int chunkSize = Integer.parseInt(chnkSizeStr);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Generated DocKeys : %s, Size is %d,"
								+ " About to chunk the Dockeys list in org doc check....",
						docKeys, docKeys.size());
				LOGGER.debug(msg);
			}

			List<List<String>> docKeyChunks = Lists.partition(docKeys,
					chunkSize);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Generated %d Chunks of DocKeys of each Size is %d in org doc check",
						docKeyChunks.size(), chunkSize);
				LOGGER.debug(msg);
			}

			if (!docKeyChunks.isEmpty()) {
				docKeyChunks.forEach(chunk -> orgDocDetails.addAll(
						docHeaderRepository.findActiveOrgDocsByDocKeys(chunk)));
			}

			Map<String, LocalDate> orgDocKeyMap = orgDocDetails.stream()
					.collect(Collectors.toMap(obj -> String.valueOf(obj[0]),
							obj -> (LocalDate) obj[1], (obj1, obj2) -> obj1));
			
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"The number of original records fetch from db for inward file is : "
								+ orgDocKeyMap.size());
			}
			String paramkryId = CONFIG_PARAM_INWARD_QUE_KEY_ID.I2.name();
			Map<Long, String> entityQuesAnsMap = new HashMap<>();

			util = StaticContextHolder.getBean(
					"OnboardingQuestionValidationsUtil",
					OnboardingQuestionValidationsUtil.class);

			crDrDocs.forEach(doc -> {
				if (doc.getOrigDocNo() != null && !doc.getOrigDocNo().isEmpty()
						&& doc.getOrigDocDate() != null) {

					String orgDocKey = docKeyGen.generateOrgKey(doc);

					String onboardAns = "";

					if (entityQuesAnsMap.get(doc.getEntityId()) != null) {
						onboardAns = entityQuesAnsMap.get(doc.getEntityId())
								.toString();
					} else {

						Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = doc
								.getEntityConfigParamMap();
						String answer = util.valid(entityConfigParamMap,
								paramkryId, doc.getEntityId());

						if (answer != null && !answer.isEmpty()) {
							entityQuesAnsMap.put(doc.getEntityId(), answer);
							onboardAns = answer;
						} else {
							entityQuesAnsMap.put(doc.getEntityId(), "");
							onboardAns = "";
						}
					}

					if (orgDocKeyMap != null
							&& orgDocKeyMap.containsKey(orgDocKey)
							&& (doc.getOrigDocDate())
									.isEqual(orgDocKeyMap.get(orgDocKey))) {

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"The original document is present in "
									+ "the system for inward file");
						}
					} else {
						if (LOGGER.isDebugEnabled())
							LOGGER.debug(
									"The answer configured during onboarding for entity "
											+ doc.getEntityId() + " is "
											+ onboardAns);
						List<ProcessingResult> results = new ArrayList<>();
						String docKey = docKeyGen.generateKey(doc);
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.DOC_NO);
						TransDocProcessingResultLoc location 
						= new TransDocProcessingResultLoc(
								null, errorLocations.toArray());

						if (onboardAns.equalsIgnoreCase(
								CONFIG_PARAM_INWARD_ANS_KEY_ID.A.name())) {

							ProcessingResult result = new ProcessingResult(
									APP_VALIDATION, ProcessingResultType.ERROR,
									"ER1155",
									"Original Document is not available in the System",
									location);
							results.add(result);

						} else if (onboardAns.equalsIgnoreCase(
								CONFIG_PARAM_INWARD_ANS_KEY_ID.B.name())) {

							ProcessingResult result = new ProcessingResult(
									APP_VALIDATION, ProcessingResultType.INFO,
									"IN1155",
									"Original Document is not available in the System",
									location);
							results.add(result);
						}

						retResultMap.put(docKey, results);

					}
				}
			});

		} catch (Exception ex) {
			LOGGER.error(
					"An exception occured while looking for the orginal "
					+ "doccument for cr dr inward invoices : "
							+ "Exception is : ",
					ex);
		}
		return retResultMap;
	}

	@Override
	public Map<String, List<ProcessingResult>> checkForCrDrOrgInvoices(
			List<OutwardTransDocument> docs, Boolean isIntegrated,
			ProcessingContext context) {
		// TODO Auto-generated method stub
		return null;
	}

}
