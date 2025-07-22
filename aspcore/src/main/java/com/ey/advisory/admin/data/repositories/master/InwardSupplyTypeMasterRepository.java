package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.InwardSupplyTypeMasterEntity;


/**
 * @author Siva.Nandam
 *
 */
@Repository("InwardSupplyTypeMasterRepository")
public interface InwardSupplyTypeMasterRepository extends 
JpaRepository <InwardSupplyTypeMasterEntity, Long> {


	 @Query("SELECT  s FROM InwardSupplyTypeMasterEntity s")
	List<InwardSupplyTypeMasterEntity> findAll();
}
