package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.TransportEntity;



/**
 * @author Siva.Nandam
 *
 */
@Repository("TransPortRepository")
public interface TransPortRepository 
         extends JpaRepository <TransportEntity, Long> {
	
	 @Query("SELECT r FROM TransportEntity r")
	List<TransportEntity> FindAll();
	
}
