package com.ey.advisory.app.data.repositories.client.drc;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.drc.TblDrc01AutoGetCallDetails;

@Repository("Drc01AutoGetCallDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Drc01AutoGetCallDetailsRepository
		extends JpaRepository<TblDrc01AutoGetCallDetails, Long>,
		JpaSpecificationExecutor<TblDrc01AutoGetCallDetails> {

	@Modifying
	@Query("UPDATE TblDrc01AutoGetCallDetails m SET m.emailType =:emailType, m.emailStatus = 'SENT', m.reminderCount =:reminderCount WHERE m.id =:id ")
	void updateEmailTypeAndEmailStatus(@Param("id") Long id, @Param("emailType") String emailType, @Param("reminderCount") int reminderCount);
	
	@Query(value = "Select top 1 * from DRC01B_DRC01C_AUTO_GET_EMAIL_DETAILS where REF_ID IS NOT NULL "
			+ " and EMAIL_TYPE IS NULL and REMARK IS NULL AND ENTITY_ID =:entityId "
			+ " order by ID asc", nativeQuery = true)
	TblDrc01AutoGetCallDetails findOriginalEntry(@Param("entityId") Long entityId);
	
	@Query(value = "Select top 1 * from DRC01B_DRC01C_AUTO_GET_EMAIL_DETAILS where REF_ID IS NOT NULL "
			+ " and EMAIL_TYPE  IS NOT NULL and REMARK IS NULL AND REMINDER_COUNT =:reminderCount AND ENTITY_ID =:entityId AND COMMUNICATION_TYPE =:commType"
			+ " order by ID asc", nativeQuery = true)
	TblDrc01AutoGetCallDetails findReminderEntry(@Param("reminderCount") int reminderCount, @Param("commType") String commType, @Param("entityId") Long entityId);
	
	@Modifying
	@Query("UPDATE TblDrc01AutoGetCallDetails m SET m.emailType =:emailType, m.emailStatus = 'FAILED', m.reminderCount =:reminderCount WHERE m.id =:id ")
	void updateEmailTypeAndFailedEmailStatus(@Param("id") Long id, @Param("emailType") String emailType,@Param("reminderCount") int reminderCount);
		
	@Modifying
	@Query("UPDATE TblDrc01AutoGetCallDetails m SET m.remark =:remark, m.drc01bOpted =:drc01bOpted, m.drc01cOpted =:drc01cOpted WHERE m.id =:id ")
	void updateRemarksAndIsOpted(@Param("remark") String remark, @Param("drc01bOpted") String drc01bOpted, @Param("drc01cOpted") String drc01cOpted , @Param("id") Long id);
	
	@Modifying
	@Query("UPDATE TblDrc01AutoGetCallDetails SET reportstatus =:reportStatus, "
			+ " updatedOn =:updatedOn "
			+ " where id =:requestId ")
	void updateStatus(@Param("reportStatus") String reportStatus, @Param("requestId") Long requestId,
			@Param("updatedOn") LocalDateTime updatedOn);
	
	
}
