package com.ey.advisory.app.data.gstr1A.repositories.client;

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
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1APrVsSubmittedReconConfigEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr1ASubmittedReconConfigRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1ASubmittedReconConfigRepository
		extends JpaRepository<Gstr1APrVsSubmittedReconConfigEntity, Long>,
		JpaSpecificationExecutor<Gstr1APrVsSubmittedReconConfigEntity> {

	@Modifying
	@Query("UPDATE Gstr1APrVsSubmittedReconConfigEntity SET status =:status,"
			+ " filePath =:filePath, completedOn =:completedOn"
			+ " where reconConfigId =:configId")
	public void updateReconConfigStatusAndReportName(
			@Param("status") String status, @Param("filePath") String filePath,
			@Param("completedOn") LocalDateTime completedOn,
			@Param("configId") Long configId);

	@Query("Select configEntity from Gstr1APrVsSubmittedReconConfigEntity configEntity where"
			+ " reconConfigId =:reconConfigId")
	public Gstr1PrVsSubmittedReconConfigEntity findByConfigId(
			@Param("reconConfigId") Long reconConfigId);

	@Query("Select  e.filePath"
			+ " from Gstr1APrVsSubmittedReconConfigEntity e where e.reconConfigId =:reconConfigId")
	public String getPrVsSubmDataList(
			@Param("reconConfigId") Long reconConfigId);

}
