/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishla.verma
 *
 */

@Slf4j
@Component("OnboardingConfigParmUtil")
public class OnboardingConfigParmUtil {
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	public String getConfigAnswer(Long entityId) {

		String queryStr = "SELECT MAX(ANSWER) FROM ENTITY_CONFG_PRMTR "
				+ " WHERE QUESTION_CODE='O26' AND ENTITY_ID =:entityId AND "
				+ " IS_DELETE=FALSE";

		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter("entityId", entityId);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing query to get onboarding Answer :: entityId {}",
					entityId);
		}
		
		Object varAns = q.getSingleResult();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("onboarding Answer :: varAns {}",
					varAns.toString());
		}
		
		return varAns.toString();

	}

}
