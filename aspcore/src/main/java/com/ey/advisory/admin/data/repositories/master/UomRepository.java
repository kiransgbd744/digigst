package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.UomMasterEntity;

/**
 * @author Siva.Nandam
 *
 */
@Repository("UomRepositoryMaster")
public interface UomRepository extends 
JpaRepository <UomMasterEntity, Long> {
	
	 @Query("SELECT r FROM UomMasterEntity r ")
	List<UomMasterEntity> findAll();
}
