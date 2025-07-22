package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.RecipientTypeEntityClient;

/**
 * @author Siva.Nandam
 *
 */
@Repository("RecipientTypeRepository")
public interface RecipientTypeRepository extends 
JpaRepository <RecipientTypeEntityClient, Long> {

	
	 @Query("SELECT  r FROM RecipientTypeEntity r WHERE "
	    		+ "r.recipientType = :recipientType")
	List<RecipientTypeEntityClient> getByRecipientType(@Param("recipientType") 
    String recipientType);
	
	 @Query("SELECT  r FROM RecipientTypeEntity r ")
	List<RecipientTypeEntityClient> findAll();
	
}
