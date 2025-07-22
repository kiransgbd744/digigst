package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Get2aAutomationEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("Get2aAutomationRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Get2aAutomationRepository
		extends CrudRepository<Get2aAutomationEntity, Long> {

	List<Get2aAutomationEntity> findByIsDeleteFalseOrderByEntityIdDesc();

	@Modifying
	@Transactional
	@Query("UPDATE Get2aAutomationEntity SET isDelete=true WHERE "
			+ "entityId=:entityId and isDelete=false")
	void updateInActiveGet2aBasedOnEntityId(@Param("entityId") Long entityId);

	@Query("SELECT count(g) from Get2aAutomationEntity g WHERE "
			+ "g.uniqueKey=:uniqueKey and g.isDelete=false")
	Integer findCountOfGet2aAuto(@Param("uniqueKey") String uniqueKey);

	@Modifying
	@Query("UPDATE Get2aAutomationEntity SET lastPostedDate= CURRENT_TIMESTAMP WHERE "
			+ "entityId=:entityId and isDelete=false")
	void updateLastJobPostDateByEntityId(@Param("entityId") Long entityId);

	@Modifying
	@Transactional
	@Query("UPDATE Get2aAutomationEntity SET isDelete=true WHERE "
			+ "entityId=:entityId and getEvent=:getEvent and isDelete=false")
	void updateInActiveGet2aBasedOnGetEvent(@Param("entityId") Long entityId,
			@Param("getEvent") String getEvent);
	
	@Modifying
	@Transactional
	@Query("UPDATE Get2aAutomationEntity SET isDelete=true WHERE "
			+ "entityId=:entityId and getEvent IN :getEvents and isDelete=false")
	void updateInActiveGet2aBasedOnGetEventIn(@Param("entityId") Long entityId,
			@Param("getEvents") List<String> getEvents);
	
	@Query("SELECT calendarDateAsString from Get2aAutomationEntity WHERE "
			+ " entityId = :entityId and getEvent = :getEvent and isDelete=false"
			+ " order by calendarDateAsString asc")
	List<String> getCustomDatesinAsc(@Param("entityId") Long entityId,
			@Param("getEvent") String getEvent);
}
