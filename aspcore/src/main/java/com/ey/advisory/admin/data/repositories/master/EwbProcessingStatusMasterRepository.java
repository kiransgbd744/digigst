/**
 * 
 */
package com.ey.advisory.admin.data.repositories.master;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.master.EwbProcessingStatusMasterEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("EwbProcessingStatusMasterRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EwbProcessingStatusMasterRepository
		extends CrudRepository<EwbProcessingStatusMasterEntity, Long> {
	
	@Query("SELECT ewb.ewbStatus FROM EwbProcessingStatusMasterEntity ewb "
			+ "WHERE ewb.id = :ewbStatusCode")
	String findByStausCode(@Param("ewbStatusCode") Long ewbStatusCode);
	
	
}
