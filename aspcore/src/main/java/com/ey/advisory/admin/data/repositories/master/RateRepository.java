package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.RateMasterEntity;


/**
 * 
 * @author Anand3.M
 *
 */

@Repository("RateRepositoryMaster")
public interface RateRepository extends JpaRepository<RateMasterEntity, Long>,
		JpaSpecificationExecutor<RateMasterEntity> {
	
	
	@Query("SELECT g FROM RateMasterEntity g")
	public List<RateMasterEntity> findAll();
	
	

}
