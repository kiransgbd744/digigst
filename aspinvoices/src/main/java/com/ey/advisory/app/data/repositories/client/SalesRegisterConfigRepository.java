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

import com.ey.advisory.service.gstr1.sales.register.SalesRegisterConfigEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("SalesRegisterConfigRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface SalesRegisterConfigRepository
		extends JpaRepository<SalesRegisterConfigEntity, Long>,
		JpaSpecificationExecutor<SalesRegisterConfigEntity> {

	@Modifying
	@Query("UPDATE SalesRegisterConfigEntity SET status =:status,"
			+ " completedOn =:completedOn"
			+ " where configId =:configId")
	public void updateReconConfigStatusAndReportName(
			@Param("status") String status,
			@Param("completedOn") LocalDateTime completedOn,
			@Param("configId") Long configId);

	
	@Query("Select distinct gstin from SalesRegisterConfigEntity where"
			+ " configId =:configId ")
	public List<String> findByConfigId(@Param("configId") Long configId);
}
