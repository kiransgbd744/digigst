package com.ey.advisory.app.services.businessvalidation.table4;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.OutwardTable4Entity;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.StaticContextHolder;

@Component("OnboardAttributesChecking")
public class OnboardAttributesChecking {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	public List<String> attributes(OutwardTable4Entity document,
			                                       String attributeName) {

		List<String> attribues = new ArrayList<>();

		gstinInfoRepository = StaticContextHolder
				.getBean("GSTNDetailRepository", GSTNDetailRepository.class);

		List<GSTNDetailEntity> outs = gstinInfoRepository
				                             .findByGstin(document.getSgstin());
		
		GSTNDetailEntity out = outs.get(0);
		if (out != null) {
			
			Long entityId = out.getEntityId();
			User user = SecurityContext.getUser();

			List<Quartet<String,String,String,String>> applicableAttrs = user
					                            .getApplicableAttrs(entityId);

			for (Quartet<String,String,String,String> applicableAtt : applicableAttrs) {

				String value0 = applicableAtt.getValue0();

				if (value0.equalsIgnoreCase(attributeName)) {

					List<Pair<Long, String>> attrValuesForAttrCode = user
							.getAttrValuesForAttrCode(entityId,attributeName);

					for (Pair<Long, String> keys : attrValuesForAttrCode) {
						String value = keys.getValue1();
						attribues.add(value);

					}
				}
			}
		}

		return attribues;

	}
}
