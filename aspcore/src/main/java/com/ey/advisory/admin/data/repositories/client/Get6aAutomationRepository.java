package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Get6aAutomationEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("Get6aAutomationRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Get6aAutomationRepository
		extends CrudRepository<Get6aAutomationEntity, Long> {

	List<Get6aAutomationEntity> findByIsDeleteFalseOrderByEntityIdDesc();

	@Modifying
	@Transactional
	@Query("UPDATE Get6aAutomationEntity SET isDelete=true WHERE "
			+ "entityId=:entityId and isDelete=false")
	void updateInActiveGet6aBasedOnEntityId(@Param("entityId") Long entityId);

	
}
