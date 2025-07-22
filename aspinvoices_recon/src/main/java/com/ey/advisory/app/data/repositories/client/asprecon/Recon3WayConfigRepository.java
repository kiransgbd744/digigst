package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.Recon3WayConfigEntity;

/**
 * @author Sakshi.jain
 *
 */

@Repository("Recon3WayConfigRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Recon3WayConfigRepository
		extends JpaRepository<Recon3WayConfigEntity, Long>,
		JpaSpecificationExecutor<Recon3WayConfigEntity> {

	@Modifying
	@Query("UPDATE Recon3WayConfigEntity SET status =:status,"
			+ " completedOn =:completedOn"
			+ " where configId =:configId")
	public void updateReconConfigStatusAndReportName(
			@Param("status") String status,
			@Param("completedOn") LocalDateTime completedOn,
			@Param("configId") Long configId);

}
