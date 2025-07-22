/**
 * 
 */
package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ELEntitlementEntity;
import com.ey.advisory.admin.data.repositories.client.GstinELeFunctionalityRepository;

/**
 * @author Sasidhar Reddy
 *
 */
@Service("DefalutGstinELeFuncFileUploadService")
public class DefalutGstinELeFuncFileUploadService
		implements EleFuncFileUploadService {
	@Autowired
	@Qualifier("GstinELeFunctionalityRepository")
	private GstinELeFunctionalityRepository eLeFunctionalityRepository;

	@Override
	public List<ELEntitlementEntity> saveAll(List<ELEntitlementEntity> Entdetails) {
		return eLeFunctionalityRepository.saveAll(Entdetails);
	}

	@Override
	public void markEntitlementsAsDeleted(
					List<ELEntitlementEntity> elEntitlementEntitiesList) {
		
		List<Long> entityIds = elEntitlementEntitiesList.stream()
				.map(ELEntitlementEntity::getEntityId).collect(Collectors.toList());
		eLeFunctionalityRepository.markEntitlementsAsDeleted(entityIds);
	}

}
