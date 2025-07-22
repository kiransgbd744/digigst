package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.SupplyTypeMasterEntityClient;

/**
 * @author Siva.Nandam
 *
 */
@Repository("SupplyTypeMasterRepository")
public interface SupplyTypeMasterRepository extends 
JpaRepository <SupplyTypeMasterEntityClient, Long> {

	
	 @Query("SELECT  r FROM SupplyTypeMasterEntity r WHERE "
	    		+ "r.supplyType = :supplyType")
	List<SupplyTypeMasterEntityClient> getBysupplyType(@Param("supplyType") 
   String supplyType);
	
	 @Query("SELECT  s FROM SupplyTypeMasterEntity s")
	List<SupplyTypeMasterEntityClient> findAll();
}
