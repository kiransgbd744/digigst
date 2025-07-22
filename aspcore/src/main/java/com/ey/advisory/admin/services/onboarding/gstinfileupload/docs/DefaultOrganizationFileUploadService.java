/**
 * 
 */
package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;
import com.ey.advisory.admin.data.repositories.client.OrganizationRepository;

/**
 * @author Sasidhar Reddy
 *
 */
@Service("DefaultOrganizationFileUploadService")
public class DefaultOrganizationFileUploadService
		implements OrganizationFileUploadService {
	@Autowired
	@Qualifier("OrganizationRepository")
	private OrganizationRepository orgRepo;

	@Override
	public List<EntityAtValueEntity> saveAll(List<EntityAtValueEntity> attributes) {
		return orgRepo.saveAll(attributes);
	}

	
	}

