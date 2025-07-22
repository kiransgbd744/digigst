package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1EInvReconConfigEntity;

/**
 * 
 * @author Rajesh N K
 *
 */
@Repository("Gstr1EInvReconConfigRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1EInvReconConfigRepository
		extends JpaRepository<Gstr1EInvReconConfigEntity, Long>,
		JpaSpecificationExecutor<Gstr1EInvReconConfigEntity> {

		
	@Modifying
	@Query("UPDATE Gstr1EInvReconConfigEntity SET status =:status,"
			+ " filePath =:filePath, completedOn =:completedOn"
			+ " where reconConfigId =:configId")
	public void updateReconConfigStatusAndReportName(
			@Param("status") String status, @Param("filePath") String filePath,
			@Param("completedOn") LocalDateTime completedOn, 
			@Param("configId") Long configId);

	@Query("Select configEntity from Gstr1EInvReconConfigEntity configEntity where"
			+ " reconConfigId =:reconConfigId")
	public Gstr1EInvReconConfigEntity findByConfigId(@Param("reconConfigId") Long reconConfigId);
	
}
