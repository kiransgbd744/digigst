/**
 * 
 */
package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.util.List;

import com.ey.advisory.admin.data.entities.client.ELEntitlementEntity;
/**
 * @author Sasidhar Reddy
 *
 */
public interface EleFuncFileUploadService {
	List<ELEntitlementEntity> saveAll(
			List<ELEntitlementEntity> Entdetails);
	
	void markEntitlementsAsDeleted(List<ELEntitlementEntity>  elEntitlementEntitiesList);

}
