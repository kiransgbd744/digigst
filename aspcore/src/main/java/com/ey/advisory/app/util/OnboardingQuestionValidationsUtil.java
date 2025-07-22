package com.ey.advisory.app.util;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.common.StaticContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("OnboardingQuestionValidationsUtil")
public class OnboardingQuestionValidationsUtil {
	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamsCheck;

	public String valid(String paramkryId, String gstin) {
		String paramtrvalue = null;

		List<Long> entityIds = clientGroupService
				.findEntityDetailsForGroupCode();

		Map<String, Long> gstinAndEntityMap = clientGroupService
				.getGstinAndEntityMapForGroupCode(entityIds);

		Long entityId = gstinAndEntityMap.get(gstin);
		if (entityId != null) {
			Map<String, String> questionAnsMap = onboardingConfigParamsCheck
					.getQuestionAndAnswerMap(entityId);
			if (questionAnsMap != null && !questionAnsMap.isEmpty()) {
				paramtrvalue = questionAnsMap.get(paramkryId);

			}
		}
		return paramtrvalue;

	}

	public String valid(
			Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap,
			String paramkeyId, Long entityId) {
		String paramtrvalue = null;

		if (entityId != null && entityConfigParamMap != null
				&& !entityConfigParamMap.isEmpty()) {

			onboardingConfigParamsCheck = StaticContextHolder.getBean(
					"OnboardingConfigParamsCheck",
					OnboardingConfigParamsCheck.class);
			Map<String, String> questionAnsMap = onboardingConfigParamsCheck
					.getQuestionAndAnswerMap(entityId, entityConfigParamMap);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("entityConfigParamMap:", entityConfigParamMap);
				LOGGER.debug("questionAnsMap:", questionAnsMap);
			}
			if (questionAnsMap != null && !questionAnsMap.isEmpty()) {
				paramtrvalue = questionAnsMap.get(paramkeyId);

			}

		}
		return paramtrvalue;

	}
}
