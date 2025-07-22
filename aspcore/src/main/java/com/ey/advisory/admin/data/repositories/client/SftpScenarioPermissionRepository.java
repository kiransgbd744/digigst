package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.SftpScenarioPermissionEntity;

@Repository("SftpScenarioPermissionRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface SftpScenarioPermissionRepository
		extends CrudRepository<SftpScenarioPermissionEntity, Long> {

	@Query("SELECT e FROM SftpScenarioPermissionEntity e WHERE e.isDelete=false ")
	public List<SftpScenarioPermissionEntity> getSftpScenarioPermissionEntities();

	@Query("select e from SftpScenarioPermissionEntity e where "
			+ "e.scenarioId=:scenarioId AND e.erpId=:erpId AND isDelete=false")
	public SftpScenarioPermissionEntity findByErpIdScenId(
			@Param("scenarioId") Long scenarioId, @Param("erpId") Long erpId);

	@Modifying
	@Transactional
	@Query("UPDATE SftpScenarioPermissionEntity SET isDelete=true "
			+ "WHERE erpId=:erpId AND  scenarioId=:scenarioId "
			+ "AND isDelete=false ")
	public void updateSftpScenarioPerm(@Param("erpId") Long erpId,
			@Param("scenarioId") Long scenarioId);

	@Query("select e from SftpScenarioPermissionEntity e where "
			+ "e.scenarioId=:scenarioId AND isDelete=false")
	public SftpScenarioPermissionEntity findByScenId(
			@Param("scenarioId") Long scenarioId);
	
	public List<SftpScenarioPermissionEntity> findByIsDelete(
			boolean isDelete);

}
