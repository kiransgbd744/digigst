package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.RecipientTypeEntity;


/**
 * @author Siva.Nandam
 *
 */
@Repository("RecipientTypeRepositoryMaster")
public interface RecipientTypeRepository extends 
JpaRepository <RecipientTypeEntity, Long> {

	
	
	 @Query("SELECT  r FROM RecipientTypeEntity r ")
	List<RecipientTypeEntity> findAll();
	
}
