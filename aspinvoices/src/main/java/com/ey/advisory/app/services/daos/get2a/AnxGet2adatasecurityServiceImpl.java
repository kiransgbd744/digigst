package com.ey.advisory.app.services.daos.get2a;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.docs.dto.Anx2GetProcessedRequestDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.SecurityContext;

@Service("AnxGet2adatasecurityServiceImpl")
public class AnxGet2adatasecurityServiceImpl
		implements AnxGet2adatasecurityService {

private static final Logger LOGGER = LoggerFactory
		.getLogger(AnxGet2adatasecurityServiceImpl.class);

@Override
public List<String> getSecurityGstins(List<Long> entityIds) {
	List<String> gstinList = new ArrayList<>();
	User user = SecurityContext.getUser();
	if (entityIds != null && !entityIds.isEmpty()) {
		entityIds.forEach(entityId -> {
			List<Quartet<String, String,String, String>> applicableAttrs = user
					.getApplicableAttrs(entityId);
			if (applicableAttrs != null && !applicableAttrs.isEmpty()) {
				for (Quartet<String, String,String, String> applicableAttr : applicableAttrs) {
					String attrCode = applicableAttr.getValue0();

					if (attrCode.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						List<Pair<Long, String>> attrValuesForGstin = user
								.getAttrValuesForAttrCode(entityId, attrCode);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"attrValuesForGstin " + attrValuesForGstin);
						}
						if (attrValuesForGstin != null
								&& !attrValuesForGstin.isEmpty()) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"attrValuesForGstin is not null or empty");
							}

							for (Pair<Long, String> attrValue : attrValuesForGstin) {
								String attrVal = attrValue.getValue1();
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"attrValuesForGstin " + attrVal);
								}

								gstinList.add(attrVal);
							}
						}
					}

				}

			}
		});
	}

	return gstinList;
}
}