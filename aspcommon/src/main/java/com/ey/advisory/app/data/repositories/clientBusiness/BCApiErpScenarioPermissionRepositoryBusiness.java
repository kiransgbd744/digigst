/**
 * 
 */
package com.ey.advisory.app.data.repositories.clientBusiness;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.ewb.data.entities.clientBusiness.BCApiErpScenarioPermissionEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("BCApiErpScenarioPermissionRepositoryBusiness")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface BCApiErpScenarioPermissionRepositoryBusiness
		extends CrudRepository<BCApiErpScenarioPermissionEntity, Long> {

	@Query("select e from BCApiErpScenarioPermissionEntity e where "
			+ " e.scenarioId = :scenarioId AND e.gstinId=:gstinId AND e.isDelete=false")
	public BCApiErpScenarioPermissionEntity findDestinationByGstin(
			@Param("scenarioId") Long scenarioId,
			@Param("gstinId") String gstinId);

}
