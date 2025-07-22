package com.ey.advisory.admin.data.repositories.client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.LandingDashboardGstr1VsGstr3BEntity;

/**
 * @author Sakshi.jain
 *
 */
@Repository("LandingDashboardOutwardSupplyRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface LandingDashboardOutwardSupplyRepository
		extends CrudRepository<LandingDashboardGstr1VsGstr3BEntity, Long> {
	
	@Query("Select e from LandingDashboardGstr1VsGstr3BEntity e where "
			+ " e.batchId =:batchId and e.isActive = true ")
	public LandingDashboardGstr1VsGstr3BEntity getBatchIdData(
			@Param("batchId") Long batchId);
	
	@Query("Select e from LandingDashboardGstr1VsGstr3BEntity e where  "
			+ " e.entityId =:entityId and e.isActive = true and e.derRetPeriod =:derivedTaxPeriod  ")
	public LandingDashboardGstr1VsGstr3BEntity getentityIdtaxPeriodData(
			@Param("entityId") Long entityId,
			@Param("derivedTaxPeriod") String derivedTaxPeriod);
	

	}
