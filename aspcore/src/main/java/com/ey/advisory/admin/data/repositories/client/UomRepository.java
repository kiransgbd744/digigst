package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.UomMasterEntityClient;

/**
 * @author Siva.Nandam
 *
 */
@Repository("UomRepository")
public interface UomRepository extends 
JpaRepository <UomMasterEntityClient, Long> {
	 @Query("SELECT r FROM UomMasterEntity r WHERE "
	    		+ "r.uqc = :uqc")
	List<UomMasterEntityClient> getByUom(@Param("uqc") 
 String uqc);
	
	 @Query("SELECT r FROM UomMasterEntity r ")
	List<UomMasterEntityClient> findAll();
}
