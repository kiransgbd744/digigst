/**
 * 
 */
package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.util.List;

import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;

/**
 * @author Sasidhar Reddy
 *
 */
public interface OrganizationFileUploadService {
	public List<EntityAtValueEntity> saveAll(List<EntityAtValueEntity> attributes);


}
