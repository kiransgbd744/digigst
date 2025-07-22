
package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.util.List;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;

public interface EleeFileUploadService {

	List<EntityInfoEntity> saveAll(List<EntityInfoEntity> EntNames);

	void updateAll(List<EntityInfoEntity> elEntitlementEntityList);

}
