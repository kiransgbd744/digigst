/**
 * 
 */
package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("ErpEventsScenarioPermissionRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ErpEventsScenarioPermissionRepository
		extends CrudRepository<ErpEventsScenarioPermissionEntity, Long> {

	@Query("SELECT sp.id from ErpEventsScenarioPermissionEntity sp "
			+ "WHERE sp.erpId=:erpId AND  sp.scenarioId=:scenarioId "
			+ " AND sp.isDelete=false ")
	public Long getScenarioPermission(@Param("erpId") Long erpId,
			@Param("scenarioId") Long scenarioId);

	@Modifying
	@Transactional
	@Query("UPDATE ErpEventsScenarioPermissionEntity "
			+ "SET isDelete=true  WHERE id=:scenPermIds AND isDelete=false ")
	public void updateScenarioPerm(
			@Param("scenPermIds") List<Long> scenPermIds);

	/*
	 * @Query("SELECT sp FROM ErpEventsScenarioPermissionEntity sp " +
	 * "WHERE sp.scenarioId=:scenarioId AND sp.isDelete=false ") public
	 * ErpEventsScenarioPermissionEntity getDestinationName(@Param("scenarioId")
	 * Long scenarioId);
	 */

	public ErpEventsScenarioPermissionEntity findByScenarioIdAndErpIdAndIsDeleteFalse(
			Long scenarioId, Long erpId);

	public ErpEventsScenarioPermissionEntity findByScenarioIdAndIsDeleteFalse(
			Long scenarioId);

	@Modifying
	@Transactional
	@Query("UPDATE ErpEventsScenarioPermissionEntity SET isDelete=true "
			+ "WHERE erpId=:erpId AND  scenarioId=:scenarioId "
			+ " AND destName=:destName AND isDelete=false ")
	public void updateEventsScenarioPerm(@Param("erpId") Long erpId,
			@Param("scenarioId") Long scenarioId,
			@Param("destName") String destName);

	@Query("SELECT sp FROM ErpEventsScenarioPermissionEntity sp "
			+ "WHERE sp.scenarioId=:scenarioId AND sp.erpId=:erpId AND sp.isDelete=false")
	public ErpEventsScenarioPermissionEntity getEntityByScenarioIdAndErpId(
			@Param("scenarioId") Long scenarioId, @Param("erpId") Long erpId);

	@Query("SELECT sp FROM ErpEventsScenarioPermissionEntity sp "
			+ "WHERE sp.scenarioId=:scenarioId AND sp.isDelete=false")
	public ErpEventsScenarioPermissionEntity getEntityByScenarioId(
			@Param("scenarioId") Long scenarioId);

	@Query("SELECT sp FROM ErpEventsScenarioPermissionEntity sp "
			+ "WHERE sp.scenarioId=:scenarioId AND sp.isDelete=false")
	public List<ErpEventsScenarioPermissionEntity> getErpEventsScenarioPerms(
			@Param("scenarioId") Long scenarioId);

}
