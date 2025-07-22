/**
 * 
 */
package com.ey.advisory.admin.data.repositories.client;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ErpScenarioMasterEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("ErpScenarioMasterRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ErpScenarioMasterRepository
		extends CrudRepository<ErpScenarioMasterEntity, Long> {

	public ErpScenarioMasterEntity findByIdAndIsDelete(Long id,
			boolean isDelete);

	@Query("SELECT scen.id, scen.scenarioName,scen.jobType,"
			+ "scen.dataType FROM ErpScenarioMasterEntity scen "
			+ "WHERE scen.isDelete=false AND scen.jobType='B'")
	public List<Object[]> getScenarioIdNameBackGroundJob();

	public Optional<ErpScenarioMasterEntity> findByScenarioNameAndIsDeleteFalse(
			String scenarioName);

	@Query("SELECT e.id FROM ErpScenarioMasterEntity e WHERE "
			+ "e.scenarioName=:scenarioName AND e.isDelete=false ")
	public Long findSceIdOnScenarioName(@Param("scenarioName") String sceName);

	@Query("SELECT scen.id, scen.scenarioName,scen.jobType,"
			+ "scen.dataType FROM ErpScenarioMasterEntity scen "
			+ "WHERE scen.isDelete=false AND scen.jobType='E'")
	public List<Object[]> getScenarioIdNameEventBasedJob();
	

	@Query("SELECT e.id FROM ErpScenarioMasterEntity e WHERE "
			+ "e.scenarioName IN (:scenarioName) AND e.isDelete=false ")
	public List<Long> findSceIdOnScenarioNames(@Param("scenarioName") List<String> sceNames);
}
