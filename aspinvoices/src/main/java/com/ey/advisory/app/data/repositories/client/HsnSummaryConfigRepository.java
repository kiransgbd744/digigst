package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.service.hsn.summary.HsnSummaryConfigEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("HsnSummaryConfigRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED, readOnly = false)
public interface HsnSummaryConfigRepository
		extends JpaRepository<HsnSummaryConfigEntity, Long>,
		JpaSpecificationExecutor<HsnSummaryConfigEntity> {

	@Modifying
	@Query("UPDATE HsnSummaryConfigEntity SET status =:status,"
			+ " completedOn =:completedOn" + " where configId =:configId")
	public void updateReconConfigStatusAndReportName(
			@Param("status") String status,
			@Param("completedOn") LocalDateTime completedOn,
			@Param("configId") Long configId);

	@Query("Select distinct gstin from HsnSummaryConfigEntity where"
			+ " configId =:configId ")
	public List<String> findByConfigId(@Param("configId") Long configId);
}
