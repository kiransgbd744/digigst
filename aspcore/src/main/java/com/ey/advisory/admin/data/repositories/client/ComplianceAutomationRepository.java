package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ComplianceAutomationEntity;

/**
 * 
 * @author Anand3.M
 *
 */
@Repository("ComplianceAutomationRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ComplianceAutomationRepository
		extends CrudRepository<ComplianceAutomationEntity, Long> {

	
	List<ComplianceAutomationEntity> findByIsDeleteFalseOrderByEntityIdDesc();
	/*
	 * List<String> findDistinctMonthlyDayByEntityIdAndIsDeleteFalse(Long
	 * entityId);
	 */

	/*
	 * @Modifying
	 * 
	 * @Transactional
	 * 
	 * @Query("UPDATE ComplianceAutomationEntity SET isDelete=true WHERE " +
	 * "entityId=:entityId and isDelete=false") void
	 * updateInActiveComplianceBasedOnEntityId(@Param("entityId") Long
	 * entityId);
	 */

}
