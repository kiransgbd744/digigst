/**
 * 
 */
package com.ey.advisory.common.client.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.client.domain.B2CQRAmtConfigEntity;

/**
 * @author Siva.Reddy
 *
 */
@Repository("B2CQRAmtConfigRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface B2CQRAmtConfigRepo
		extends JpaRepository<B2CQRAmtConfigEntity, Long>,
		JpaSpecificationExecutor<B2CQRAmtConfigEntity> {

	@Modifying
	@Query("UPDATE B2CQRAmtConfigEntity g SET g.isActive = false,g.updatedOn = CURRENT_TIMESTAMP,"
			+ "g.updatedBy =:updatedBy "
			+ "WHERE g.entityId = :entityId and g.isActive = true")
	public int updateActiveExistingRecords(@Param("entityId") Long entityId,
			@Param("updatedBy") String updatedBy);

	public B2CQRAmtConfigEntity findByEntityIdAndIsActiveTrue(Long entityId);

	B2CQRAmtConfigEntity findByPanAndIsActiveTrue(String pan);
}