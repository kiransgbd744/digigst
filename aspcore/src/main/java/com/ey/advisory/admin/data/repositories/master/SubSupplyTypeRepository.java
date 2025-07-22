package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.SubSupplyTypeEntity;



/**
 * @author Siva.Nandam
 *
 */
@Repository("SubSupplyTypeRepository")
public interface SubSupplyTypeRepository 
         extends JpaRepository <SubSupplyTypeEntity, Long> {
	
	 @Query("SELECT r FROM SubSupplyTypeEntity r")
	List<SubSupplyTypeEntity> FindAll();
	
}
