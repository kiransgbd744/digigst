package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.SupplyTypeMasterEntity;


/**
 * @author Siva.Nandam
 *
 */
@Repository("SupplyTypeRepository")
public interface SupplyTypeMasterRepository extends 
JpaRepository <SupplyTypeMasterEntity, Long> {

	
	
	 @Query("SELECT  s FROM SupplyTypeMasterEntity s")
	List<SupplyTypeMasterEntity> findAll();
}
