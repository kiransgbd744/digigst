package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.DmsRuleMasterEntity;

/**
 * @author ashutosh.kar
 *
 */
@Repository("DmsRuleMasterRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface DmsRuleMasterRepository 
		extends CrudRepository<DmsRuleMasterEntity, Long> {

	List<DmsRuleMasterEntity> findByIsActiveTrue();
	
	@Modifying
    @Query("UPDATE DmsRuleMasterEntity r SET r.isActive = false WHERE r.isActive = true")
    void softDeleteAllActive();

	@Query("Select e.ruleName FROM DmsRuleMasterEntity e WHERE "
			+ " e.ruleId = :ruleId AND e.isActive = true ")
	String findByRuleId(@Param("ruleId") Long ruleId);
	
	@Query("SELECT r.ruleId FROM DmsRuleMasterEntity r WHERE r.ruleName = :ruleName AND r.isActive = true")
	Long findRuleIdByRuleName(@Param("ruleName") String ruleName);


}
