package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.PortCodeInfoEntity;


/**
 * 
 * @author Siva.Nandam
 *
 *Repository for PortCodeInfoEntity class
 */
@Repository("PortCodeRepositoryMaster")
public interface PortCodeRepository extends 
JpaRepository<PortCodeInfoEntity, Long> , 
JpaSpecificationExecutor<PortCodeInfoEntity>{

	
	@Query("SELECT g FROM PortCodeInfoEntity g")	
	List<PortCodeInfoEntity> findAll();
	
	
	
}
