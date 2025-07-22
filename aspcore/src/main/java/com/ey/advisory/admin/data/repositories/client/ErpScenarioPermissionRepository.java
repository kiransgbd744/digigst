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

import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("ErpScenarioPermissionRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ErpScenarioPermissionRepository
		extends CrudRepository<ErpScenarioPermissionEntity, Long> {
	
	public List<ErpScenarioPermissionEntity> findByGroupcodeAndIsDelete(
			String groupcode, boolean isDelete);

	public List<ErpScenarioPermissionEntity> findByGroupcodeAndIsDeleteAndScenarioIdNotIn(
			String groupcode, boolean isDelete, List<Long> scenariodIdList);

	@Modifying
	@Transactional
	@Query("UPDATE ErpScenarioPermissionEntity scp SET isDelete =true WHERE "
			+ "scp.gstinId= :gstinId AND scp.erpId = :erpId AND "
			+ "scp.entityId = :entityId AND scp.scenarioId = :scenarioId")
	public void deleterecord(@Param("gstinId") Long gstinId,
			@Param("erpId") Long erpId, @Param("entityId") Long entityId,
			@Param("scenarioId") Long scenarioId);

	@Query("SELECT scp FROM ErpScenarioPermissionEntity scp WHERE "
			+ "scp.gstinId= :gstinId AND scp.erpId = :erpId AND "
			+ "scp.entityId = :entityId AND scp.scenarioId = :scenarioId AND "
			+ "scp.isDelete = false")
	public List<ErpScenarioPermissionEntity> getDuplicateIds(
			@Param("gstinId") Long gstinId, @Param("erpId") Long erpId,
			@Param("entityId") Long entityId,
			@Param("scenarioId") Long scenarioId);

	public List<ErpScenarioPermissionEntity> findByScenarioIdAndGstinIdAndIsDeleteFalse(
			Long scenarioId, Long gstinId);

	public ErpScenarioPermissionEntity findByScenarioIdAndGstinIdAndErpIdAndIsDeleteFalse(
			Long scenarioId, Long gstinId, Long erpId);

	@Query("select e from ErpScenarioPermissionEntity e where "
			+ "e.gstinId=:gstinId AND e.scenarioId=:scenarioId AND isDelete=false")
	public ErpScenarioPermissionEntity findScenarioIdBasedScenIdAndGstin(
			@Param("gstinId") Long gstinId,
			@Param("scenarioId") Long scenarioId);

	@Query("select e from ErpScenarioPermissionEntity e where "
			+ "e.gstinId=:gstinId AND e.scenarioId=:scenarioId AND "
			+ "e.entityId = :entityId AND isDelete=false")
	public ErpScenarioPermissionEntity findSceIdBasedScenIdAndGstinAndEntity(
			@Param("gstinId") Long gstinId,
			@Param("scenarioId") Long scenarioId,
			@Param("entityId") Long entityId);

	public ErpScenarioPermissionEntity findByGstinIdAndScenarioIdAndEntityIdAndIsDeleteFalse(
			@Param("gstinId") Long gstinId,
			@Param("scenarioId") Long scenarioId,
			@Param("entityId") Long entityId);
	

	public List<ErpScenarioPermissionEntity> findByScenarioIdAndEntityIdAndIsDeleteFalse(
			@Param("scenarioId") Long scenarioId,
			@Param("entityId") Long entityId);
	
	public ErpScenarioPermissionEntity findByScenarioIdAndGstinIdAndErpIdAndEntityIdAndIsDeleteFalse(
			Long scenarioId, Long gstinId, Long erpId, Long entityId);

	/*public ErpScenarioPermissionEntity findByGroupcodeAndIsDeleteAndDestNameAndGstinIdAndscenarioId(
			String groupCode, boolean b, String oldGstr2aGetRevIntg, Long gstinId,
			Long scenarioId);*/

}
