package com.ey.advisory.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * This class is responsible for checking Onboarding Configuration Parameter
 * check for Entity and make decision based on the Question and Answer
 * configured
 * 
 * @author Mohana.Dasari
 *
 */
@Component("OnboardingConfigParamsCheck")
public class OnboardingConfigParamsCheck {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OnboardingConfigParamsCheck.class);

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfPrmRepository;
	
	/**
	 * 
	 * @param entityId
	 * @param entityConfParamMap
	 * @return
	 */
	public Map<String, String> getQuestionAndAnswerMap(Long entityId,
			Map<Long, List<EntityConfigPrmtEntity>> entityConfParamMap) {
		// paramKeyValMap - Question Id is key and Answer Id is value
		Map<String,String> paramKeyValMap = new HashMap<>();
		String groupCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Checking onboarding config params for Entity "
					+ entityId + " for Group Code " + groupCode);
		}
		
		if (entityId != null) {
			List<EntityConfigPrmtEntity> entityConfigPrmts = entityConfParamMap
					.get(entityId);
			
			if (entityConfigPrmts != null && !entityConfigPrmts.isEmpty()) {
				entityConfigPrmts.forEach(entityConfigParam -> {
					paramKeyValMap.put(entityConfigParam.getParamValKeyId(),
							entityConfigParam.getParamValue());
				});
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"paramKeyValMap " + paramKeyValMap + " for Entity Id "
								+ entityId + ", Group Code " + groupCode);
			}
		}
		return paramKeyValMap;
	}
	
	public Map<String, String> getQuestionAndAnswerMap(Long entityId) {
		// paramKeyValMap - Question Id is key and Answer Id is value
		Map<String,String> paramKeyValMap = new HashMap<>();
		String groupCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Checking onboarding config params for Entity "
					+ entityId + " for Group Code " + groupCode);
		}
		
		Map<Long, List<EntityConfigPrmtEntity>> entityConfParamMap = 
				getEntityAndConfParamMap();
		
		if (entityId != null) {
			List<EntityConfigPrmtEntity> entityConfigPrmts = entityConfParamMap
					.get(entityId);
			
			if (entityConfigPrmts != null && !entityConfigPrmts.isEmpty()) {
				entityConfigPrmts.forEach(entityConfigParam -> {
					paramKeyValMap.put(entityConfigParam.getParamValKeyId(),
							entityConfigParam.getParamValue());
				});
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"paramKeyValMap " + paramKeyValMap + " for Entity Id "
								+ entityId + ", Group Code " + groupCode);
			}
		}
		return paramKeyValMap;
	}
	
	public Map<Long, List<EntityConfigPrmtEntity>> getEntityAndConfParamMap() {
		Map<Long, List<EntityConfigPrmtEntity>> map = new HashMap<>();
		List<EntityConfigPrmtEntity> entityConfigPrms = entityConfPrmRepository
				.findByGroupCode(TenantContext.getTenantId());
		if(entityConfigPrms !=null && !entityConfigPrms.isEmpty()){
			entityConfigPrms.forEach(entityConfigParam -> {
				Long entityId = entityConfigParam.getEntityId();
				map.computeIfAbsent(entityId, k -> new ArrayList<>())
						.add(entityConfigParam);
			});
		}
		return map;
	}
}
