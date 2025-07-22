package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.InwardDocTypeMasterEntity;



/**
 * @author Siva.Nandam
 *
 */
@Repository("InwardDocTypeRepository")
public interface InwardDocTypeRepository 
         extends JpaRepository <InwardDocTypeMasterEntity, Long> {
	
	 @Query("SELECT r FROM InwardDocTypeMasterEntity r")
	List<InwardDocTypeMasterEntity> FindAll();
	
}
