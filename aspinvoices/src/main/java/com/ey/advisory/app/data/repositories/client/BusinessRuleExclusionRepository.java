package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.BusinessRuleExclusion;


/**
 * This class is responsible for repository operations of Business Rule 
 * Exclusion
 * @author Murali.Singanamala
 *
 */
@Repository("BusinessRuleExclusionRepository")
public interface BusinessRuleExclusionRepository
		extends JpaRepository<BusinessRuleExclusion, Long> {
	
	@Query("select g.ruleName FROM BusinessRuleExclusion g WHERE g.groupCode = "
			+ ":groupCode ")
	public List<String> findByGroupCode(@Param ("groupCode") String groupCode);
			
}
