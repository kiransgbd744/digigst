package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.DocTypeMasterEntity;



/**
 * @author Siva.Nandam
 *
 */
@Repository("DocTypeRepositoryMaster")
public interface DocTypeRepository 
         extends JpaRepository <DocTypeMasterEntity, Long> {
	
	 @Query("SELECT r FROM DocTypeMasterEntity r")
	List<DocTypeMasterEntity> FindAll();
	
}
