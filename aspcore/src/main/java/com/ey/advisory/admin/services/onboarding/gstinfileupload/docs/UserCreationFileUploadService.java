/**
 * 
 */
package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.util.List;

import com.ey.advisory.admin.data.entities.client.UserCreationEntity;

/**
 * @author Sasidhar Reddy
 *
 */
public interface UserCreationFileUploadService {
	public List<UserCreationEntity> saveAll(List<UserCreationEntity> Users);

}
