package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.AIM3BLockStatusEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("AIM3BLockStatusRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface AIM3BLockStatusRepository
		extends JpaRepository<AIM3BLockStatusEntity, Long>,
		JpaSpecificationExecutor<AIM3BLockStatusEntity> {

	@Modifying
	@Query("update AIM3BLockStatusEntity set status = :status where batchId  "
			+ " IN (:batchId) ")
	public void updateStatus(@Param("batchId") List<Long> batchId,
			@Param("status") String status);

	@Modifying
	@Query("update AIM3BLockStatusEntity set status =:status, "
			+ " errorMsgProc1 =:errorMsgProc1, "
			+ " errorMsgProc2 =:errorMsgProc2, completedOn = CURRENT_TIMESTAMP"
			+ " where batchId =:batchId ")
	public void updateStatusAndResp(@Param("batchId") Long batchId,
			@Param("status") String status,
			@Param("errorMsgProc1") String errorMsgProc1,
			@Param("errorMsgProc2") String errorMsgProc2);

	@Query("Select count(*) from AIM3BLockStatusEntity where status IN (:status)")
	public int findByStatusIn(@Param("status")List<String> status);
}