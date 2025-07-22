package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.StateCodeInfoEntityClient;
/**
 * 
 * @author Siva.Nandam
 *
 *Repository for StateCodeInfoEntity class
 */
@Repository("StatecodeRepository")
public interface StatecodeRepository extends 
JpaRepository<StateCodeInfoEntityClient, Long> , 
JpaSpecificationExecutor<StateCodeInfoEntityClient>{

	@Query("SELECT g.stateCode FROM StateCodeInfoEntityClient g")	
	List<String> findAllStates();
	
	@Query("SELECT g FROM StateCodeInfoEntityClient g")	
	List<StateCodeInfoEntityClient> findAll();
	
	@Query("SELECT count(g.stateCode) FROM StateCodeInfoEntityClient g"
			+ " WHERE g.stateCode = :s")
	public int findStateCode(@Param("s") String s);
	
	
	
	
	@Query("SELECT g.stateCode FROM StateCodeInfoEntityClient g"
			+ " WHERE g.stateCode = :s")
	public List<String> getStateCode(@Param("s") String s);
	
	@Query("SELECT g.stateCode FROM StateCodeInfoEntityClient g"
			+ " WHERE g.stateName = :s")
	public String getStateCodes(@Param("s") String s);  
	
	
	@Query("SELECT g.stateName FROM StateCodeInfoEntityClient g"
			+ " WHERE g.stateCode = :statecode")
	public String findStateNameByCode(@Param("statecode") String statecode);
	
	@Query("SELECT g.stateName FROM StateCodeInfoEntityClient g"
			+ " WHERE g.stateCode = :statecode")
	StateCodeInfoEntityClient findStateCodes(@Param("statecode") String statecode);
	
}
