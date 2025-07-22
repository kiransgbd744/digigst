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

import com.ey.advisory.app.data.entities.client.Gstr6ComputeCredDistDataEntity;

/**
 * 
 * @author Kiran
 *
 */
@Repository("Gstr6ComputeCredDistDataRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr6ComputeCredDistDataRepository
		extends JpaRepository<Gstr6ComputeCredDistDataEntity, Long>,
		JpaSpecificationExecutor<Gstr6ComputeCredDistDataEntity> {
	
	@Modifying
	@Query("UPDATE Gstr6ComputeCredDistDataEntity SET status =:status,"
			+ " filePath =:filePath, docId =:docId, completedOn =:completedOn"
			+ " where requestId =:requestId")
	public void updateGstr6CredDistDataComp(
			@Param("status") String status, @Param("filePath") String filePath,
			@Param("completedOn") LocalDateTime completedOn, 
			@Param("requestId") Long requestId,@Param("docId") String docId);
}
