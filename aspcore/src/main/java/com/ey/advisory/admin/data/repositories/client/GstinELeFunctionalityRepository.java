/**
 * 
 */
package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ELEntitlementEntity;

/**
 * @author Sasidhar Reddy
 *
 */
@Component("GstinELeFunctionalityRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstinELeFunctionalityRepository
		extends JpaRepository<ELEntitlementEntity, Long>,
		JpaSpecificationExecutor<ELEntitlementEntity> {

	@Modifying
	@Query("UPDATE ELEntitlementEntity SET isDelete = true WHERE entityId in (:entityIds)")
	public void markEntitlementsAsDeleted(@Param("entityIds") List<Long> entityIds);
}
