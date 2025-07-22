package com.ey.advisory.app.data.repositories.client.drc01c;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.DRC01BReminderFrequencyEntity;
import com.ey.advisory.admin.data.entities.client.DRC01CReminderFrequencyEntity;

@Repository("TblDrc01bReminderFrequencyRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TblDrc01bReminderFrequencyRepo
		extends JpaRepository<DRC01BReminderFrequencyEntity, Long>,
		JpaSpecificationExecutor<DRC01BReminderFrequencyEntity> {

	@Modifying
	@Query("UPDATE DRC01BReminderFrequencyEntity g SET g.isDelete = true,g.modifiedOn = CURRENT_TIMESTAMP "
			+ "WHERE g.entityId =:entityId and g.isDelete = false")
	public int inActivateRecords(@Param("entityId") Long entityId);

	List<DRC01BReminderFrequencyEntity> findAllByEntityIdAndIsDeleteFalse(
			Long entityId);
	
	@Query("Select g.drc01bReminderDate from DRC01BReminderFrequencyEntity g "
			+ "WHERE g.entityId =:entityId and g.isDelete = false")
	public List<String> getReminderdates(@Param("entityId") Long entityId);

	@Query("Select g from DRC01BReminderFrequencyEntity g "
			+ "WHERE g.entityId =:entityId and g.isDelete = false")
	public List<DRC01BReminderFrequencyEntity> getReminderdatesEntity(@Param("entityId") Long entityId);		
	
}