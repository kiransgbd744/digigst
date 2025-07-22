package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.DocCatogeryEntity;



/**
 * @author Siva.Nandam
 *
 */
@Repository("DocCatoryRepository")
public interface DocCatoryRepository 
         extends JpaRepository <DocCatogeryEntity, Long> {
	
	 @Query("SELECT r FROM DocCatogeryEntity r")
	List<DocCatogeryEntity> FindAll();
	
}
