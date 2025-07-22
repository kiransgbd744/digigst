package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.CatogeryEntity;



/**
 * @author Siva.Nandam
 *
 */
@Repository("CatoryRepository")
public interface CatoryRepository 
         extends JpaRepository <CatogeryEntity, Long> {
	
	 @Query("SELECT r FROM CatogeryEntity r")
	List<CatogeryEntity> FindAll();
	
}
