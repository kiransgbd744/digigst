package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.StateCodeInfoEntity;

/**
 * 
 * @author Siva.Nandam
 *
 *         Repository for StateCodeInfoEntity class
 */
@Repository("StatecodeRepositoryMaster")
public interface StatecodeRepository
		extends JpaRepository<StateCodeInfoEntity, Long>,
		JpaSpecificationExecutor<StateCodeInfoEntity> {

	@Query("SELECT g.stateName FROM StateCodeInfoEntity g"
			+ " WHERE g.stateCode = :statecode")
	public String findStateNameByCode(@Param("statecode") String statecode);

	@Query("SELECT g FROM StateCodeInfoEntity g")
	List<StateCodeInfoEntity> findAll();

	@Query("SELECT count(g.stateCode) FROM StateCodeInfoEntity g"
			+ " WHERE g.stateCode = :stateCode")
	public int findStateCodeCount(
			@Param("stateCode") String statecode);
	
	@Query(value = "select STATE_CODE from  MASTER_STATE where lower(STATE_NAME) = :state",nativeQuery = true)
	public String findStateCode(@Param("state") String state);
	
	
	

}
