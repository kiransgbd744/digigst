package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.ImsReconConfigEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("ImsReconStatusConfigRepository")
@Transactional(value = "clientTransactionManager",
				propagation = Propagation.REQUIRED)
public interface ImsReconStatusConfigRepository
		extends JpaRepository<ImsReconConfigEntity, Long>,
		JpaSpecificationExecutor<ImsReconConfigEntity> {

	@Modifying
	@Query("update ImsReconConfigEntity set status = :status where batchId  "
	+ " IN (:batchId) ")
	public void updateStatus(@Param("batchId") List<Long> batchId, @Param("status") String status);

	@Query("Select count(*) from ImsReconConfigEntity where status IN (:status)")
	public int findByStatusIn(@Param("status") List<String> status);
	
	@Modifying
	@Query("update ImsReconConfigEntity set status =:status, "
			+ " completedOn = CURRENT_TIMESTAMP"
			+ " where batchId =:batchId ")
	public void updateStatusAndResp(@Param("batchId") Long batchId,
			@Param("status") String status);
}