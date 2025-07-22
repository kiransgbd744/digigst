package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityAtConfigEntity;
import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityAtConfiRepository;
import com.ey.advisory.admin.data.repositories.client.EntityAtValueRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GstinElRegRepository;
import com.ey.advisory.admin.data.repositories.client.OrganizationRepository;

@Service("DefaultGstinRegFileUploadService")
public class DefaultGstinRegFileUploadService
		implements GstinRegFileUploadService {

	@Autowired
	@Qualifier("GstinElRegRepository")
	private GstinElRegRepository gstinRegRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("entityAtConfiRepository")
	private EntityAtConfiRepository entityAtConfiRepository;

	@Autowired
	@Qualifier("entityAtValueRepository")
	private EntityAtValueRepository entityAtValueRepository;

	@Autowired
	@Qualifier("DefaultOrganizationFileUploadService")
	private OrganizationFileUploadService orgService;

	@Autowired
	@Qualifier("OrganizationRepository")
	private OrganizationRepository organizationRepository;

	@Override
	public List<GSTNDetailEntity> saveAll(List<GSTNDetailEntity> gstins) {
		return gstinRegRepo.saveAll(gstins);
	}

	@Override
	public List<Long> getByGstin(String gstin, Long entityId) {
		return gstinRegRepo.getByGstin(gstin, entityId);
	}

	@Override
	public void disableUploadedGstinEntries(Long entityId, String groupCode,
			List<GSTNDetailEntity> gstinList) {
		List<String> gstins = gstinList.stream()
				.map(entity -> entity.getGstin()).collect(Collectors.toList());
		gSTNDetailRepository.updateAll(gstins, entityId, groupCode);
		entityAtConfiRepository.updateGstinValue(entityId, groupCode);
		entityAtValueRepository.updateAllValues(entityId, groupCode, gstins);
	}

	@Override
	public void createGstinEntriesInOrg(Long entityId, String groupCode,
			List<GSTNDetailEntity> gstinList) {

		// Create EntityAtalueEntity objects for each GSTIN
		EntityAtConfigEntity entityAtConfigId = new EntityAtConfigEntity();
		Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);
		entityAtConfigId.setGroupId(groupId);
		entityAtConfigId.setGroupcode(groupCode);
		entityAtConfigId.setEntityId(entityId);
		entityAtConfigId.setAtCode("GSTIN");
		entityAtConfigId.setAtName("GSTIN 1");
		entityAtConfigId.setAtInward("M");
		entityAtConfigId.setAtOutward("M");
		entityAtConfigId.setDelete(false);
		entityAtConfigId.setCreatedBy(System.getProperty("user.name"));
		entityAtConfigId.setCreatedOn(LocalDateTime.now());
		entityAtConfigId.setModifiedBy(System.getProperty("user.name"));
		entityAtConfigId.setModifiedOn(LocalDateTime.now());

		EntityAtConfigEntity entity = entityAtConfiRepository
				.save(entityAtConfigId);

		List<EntityAtValueEntity> entityAtValueEntities = new ArrayList<EntityAtValueEntity>();

		if (!gstinList.isEmpty()) {
			gstinList.forEach(atValue -> {
				EntityAtValueEntity newAtVal = new EntityAtValueEntity();
				// set all the values.
				newAtVal.setGroupId(groupId);
				newAtVal.setGroupCode(groupCode);
				newAtVal.setEntityId(entityId);
				newAtVal.setAtCode(entity.getAtCode());
				newAtVal.setEntityAtConfigId(entity.getId());
				newAtVal.setAtValue(atValue.getGstin());
				newAtVal.setDelete(false);
				newAtVal.setCreatedBy(System.getProperty("user.name"));
				newAtVal.setCreatedOn(LocalDateTime.now());
				newAtVal.setModifiedBy(System.getProperty("user.name"));
				newAtVal.setModifiedOn(LocalDateTime.now());
				newAtVal.setAtCode(entity.getAtCode());
				entityAtValueEntities.add(newAtVal);
			});
		}

		orgService.saveAll(entityAtValueEntities);
	}

}
