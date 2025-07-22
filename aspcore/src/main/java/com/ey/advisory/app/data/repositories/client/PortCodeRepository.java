package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.PortCodeInfoEntityClient;
/**
 * 
 * @author Siva.Nandam
 *
 *Repository for PortCodeInfoEntity class
 */
@Repository("PortCodeRepository")
public interface PortCodeRepository extends 
JpaRepository<PortCodeInfoEntityClient, Long> , 
JpaSpecificationExecutor<PortCodeInfoEntityClient>{

	@Query("SELECT g.portCode FROM PortCodeInfoEntity g")	
	List<String> findAllPortCodes();
	
	@Query("SELECT g FROM PortCodeInfoEntity g")	
	List<PortCodeInfoEntityClient> findAll();
	
	@Query("SELECT count(g.portCode) FROM PortCodeInfoEntity g"
			+ " WHERE g.portCode = :s")
	public int findPortCode(@Param("s") String s);
	
}
