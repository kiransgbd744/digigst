package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.ReconConfigEntity;

/**
 * @author Arun.KA
 *
 */

@Repository("ReconConfigRepository")
@Transactional(value = "clientTransactionManager", propagation = 
			Propagation.REQUIRED)
public interface ReconConfigRepository
		extends JpaRepository<ReconConfigEntity, Long>,
		JpaSpecificationExecutor<ReconConfigEntity> {

	@Modifying
	@Query("UPDATE ReconConfigEntity SET status =:status,"
			+ " filePath =:filePath, completedOn =:completedOn"
			+ " where configId =:configId")
	public void updateReconConfigStatusAndReportName(
			@Param("status") String status, @Param("filePath") String filePath,
			@Param("completedOn") Date completedOn, 
			@Param("configId") Long configId);

	@Query("Select configEntity from ReconConfigEntity configEntity where"
			+ " configId =:configId")
	public ReconConfigEntity findByConfigId(@Param("configId") Long configId);

}
