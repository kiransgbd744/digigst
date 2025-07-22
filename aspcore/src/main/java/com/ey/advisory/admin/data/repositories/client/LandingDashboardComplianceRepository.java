package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.LandingDashboardComplianceStatusentity;

/**
 * @author Sakshi.jain
 *
 */
@Repository("LandingDashboardComplianceRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface LandingDashboardComplianceRepository
		extends CrudRepository<LandingDashboardComplianceStatusentity, Long> {
	
	@Query("Select e from LandingDashboardComplianceStatusentity e where "
			+ "e.batchId =:batchId and e.isActive = true ")
	public List<LandingDashboardComplianceStatusentity> getBatchIdData(
			@Param("batchId") Long batchId);
	
	@Query("Select e from LandingDashboardComplianceStatusentity e where "
			+ " e.entityId =:entityId and e.isActive = true and e.derRetPeriod =:derivedTaxPeriod ")
	public List<LandingDashboardComplianceStatusentity> getentityIdtaxPeriodData(@Param("entityId") Long entityId,
			@Param("derivedTaxPeriod") String derivedTaxPeriod);
	


	}
