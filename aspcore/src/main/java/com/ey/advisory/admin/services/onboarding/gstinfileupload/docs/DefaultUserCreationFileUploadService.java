/**
 * 
 */
package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;

/**
 * @author Sasidhar Reddy
 *
 */
@Service("DefaultUserCreationFileUploadService")
public class DefaultUserCreationFileUploadService
		implements UserCreationFileUploadService {
	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository UserRepo;

	@Override
	public List<UserCreationEntity> saveAll(List<UserCreationEntity> Users) {
		return UserRepo.saveAll(Users);
	}

}
