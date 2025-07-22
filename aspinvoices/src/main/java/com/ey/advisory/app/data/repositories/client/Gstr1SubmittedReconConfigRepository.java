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

import com.ey.advisory.app.data.entities.client.Gstr1PrVsSubmittedReconConfigEntity;

/**
 * 
 * @author kiran s
 *
 */
@Repository("Gstr1SubmittedReconConfigRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1SubmittedReconConfigRepository
		extends JpaRepository<Gstr1PrVsSubmittedReconConfigEntity, Long>,
		JpaSpecificationExecutor<Gstr1PrVsSubmittedReconConfigEntity> {

		
	@Modifying
	@Query("UPDATE Gstr1PrVsSubmittedReconConfigEntity SET status =:status,"
			+ " filePath =:filePath, completedOn =:completedOn"
			+ " where reconConfigId =:configId")
	public void updateReconConfigStatusAndReportName(
			@Param("status") String status, @Param("filePath") String filePath,
			@Param("completedOn") LocalDateTime completedOn, 
			@Param("configId") Long configId);

	@Query("Select configEntity from Gstr1PrVsSubmittedReconConfigEntity configEntity where"
			+ " reconConfigId =:reconConfigId")
	public Gstr1PrVsSubmittedReconConfigEntity findByConfigId(@Param("reconConfigId") Long reconConfigId);
	
	
	@Query("Select  e.filePath"
			+ " from Gstr1PrVsSubmittedReconConfigEntity e where e.reconConfigId =:reconConfigId")
	public String getPrVsSubmDataList(@Param("reconConfigId") Long reconConfigId);

	
}
