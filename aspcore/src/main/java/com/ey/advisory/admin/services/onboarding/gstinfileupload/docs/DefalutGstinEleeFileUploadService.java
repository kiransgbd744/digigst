package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.GstinEleeRepository;

@Service("DefalutGstinEleeFileUploadService")
public class DefalutGstinEleeFileUploadService implements EleeFileUploadService {

	@Autowired
	@Qualifier("GstinEleeRepository")
	private GstinEleeRepository gstinEleeRepo;

	@Override
	public List<EntityInfoEntity> saveAll(List<EntityInfoEntity> EntNames) {
		return gstinEleeRepo.saveAll(EntNames);
	}

	@Override
	public void updateAll(List<EntityInfoEntity> elEntitlementEntityList) {
		
		List<String> entityNames = elEntitlementEntityList.stream()
				.map(EntityInfoEntity::getEntityName).collect(Collectors.toList());
		gstinEleeRepo.updateAll(entityNames);
	}

}
