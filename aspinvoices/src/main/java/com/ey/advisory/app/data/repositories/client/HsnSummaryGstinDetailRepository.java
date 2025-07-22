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

import com.ey.advisory.service.hsn.summary.HsnSummaryGstinDetailsEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("HsnSummaryGstinDetailRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED, readOnly = false)
public interface HsnSummaryGstinDetailRepository
		extends JpaRepository<HsnSummaryGstinDetailsEntity, Long>,
		JpaSpecificationExecutor<HsnSummaryGstinDetailsEntity> {

	@Modifying
	@Query("UPDATE HsnSummaryGstinDetailsEntity SET status =:status"
			+ " where configId =:configId")
	public void updateReconConfigStatus(@Param("status") String status,
			@Param("configId") Long configId);

	@Query("Select distinct gstin from HsnSummaryGstinDetailsEntity where"
			+ " configId =:configId ")
	public List<String> findByConfigId(@Param("configId") Long configId);
}
