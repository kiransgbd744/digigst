package com.ey.advisory.admin.data.repositories.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.master.OperationalPartnerDashboardMasterEntity;

@Repository("OperationDashboardPartnerRepository")
@Transactional(value = "masterTransactionManager", propagation = Propagation.REQUIRED)


public interface OperationDashboardPartnerRepository
		extends JpaRepository<OperationalPartnerDashboardMasterEntity, Long> {

	@Modifying
	@Query("UPDATE OperationalPartnerDashboardMasterEntity log SET log.isDelete = true WHERE log.groupCode =:groupCode ")
	void softDeleteId(@Param("groupCode") String groupCode);

	@Query("select e FROM OperationalPartnerDashboardMasterEntity e WHERE e.groupCode =:groupCode and e.isDelete = false ")
	OperationalPartnerDashboardMasterEntity findActiveGroupCode(
			@Param("groupCode") String groupCode);

}
