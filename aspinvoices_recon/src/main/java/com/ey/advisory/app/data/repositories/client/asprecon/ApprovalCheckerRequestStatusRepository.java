package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.ApprovalRequestStatusEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */

@Repository("ApprovalCheckerRequestStatusRepository")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public interface ApprovalCheckerRequestStatusRepository
			extends JpaRepository<ApprovalRequestStatusEntity, Long>,
			JpaSpecificationExecutor<ApprovalRequestStatusEntity>  {
	 
	@Query(value = " select CHECKER_NAME,total_request,Total_Approved,Total_Pending,Total_Rejected from (select  CHECKER_NAME,ENTITY_ID, count(REQUEST_ID) total_request, "
			+ " (case when REQUEST_STATUS ='Approved' then sum(cnt)  else 0 end) "
			+ " as Total_Approved,(case when REQUEST_STATUS ='Pending' then sum(cnt) "
			+ " else 0 end) as Total_Pending,(case when REQUEST_STATUS ='Rejected' "
			+ " then sum(cnt)  else 0 end) as Total_Rejected from (select count(*) "
			+ " as cnt, CAS.REQUEST_ID, CAS.CHECKER_NAME, AWR.ENTITY_ID,MAP(REQUEST_STATUS,'Pending (reversed)', "
			+ " 'Pending',REQUEST_STATUS) as REQUEST_STATUS "
			+ " from CHECKER_APPROVAL_STATUS CAS inner join APPROVAL_WORKFLOW_REQUEST AWR on CAS.REQUEST_ID = AWR.REQUEST_ID "
			+ " where (CAS.action_by is null  or CAS.checker_name= action_by) "
			+ " group by CAS.CHECKER_NAME,CAS.REQUEST_ID,MAP(CAS.REQUEST_STATUS,'Pending (reversed)', "
			+ " 'Pending',CAS.REQUEST_STATUS), AWR.ENTITY_ID) "
			+ " group by CHECKER_NAME,REQUEST_STATUS,ENTITY_ID) "
			+ " where checker_name =:userName AND ENTITY_ID =:entityId ", nativeQuery = true)
	public List<Object[]> findRequestCounts(
			@Param("userName") String userName,
			@Param("entityId") Long entityId);
	
	@Modifying
	@Query(" Update ApprovalRequestStatusEntity set reqStatus =:status, actionBy =:userName, "
			+ " checkcomments =:checComments, actionDateTime = CURRENT_TIMESTAMP "
			+ " where requestId =:reqId ")
	public void updateStatusActionByComments(@Param("status") String status, 
			@Param("userName") String userName,
			@Param("checComments") String checComments,
			@Param("reqId") Long reqId );

	@Modifying
	@Query(" Update ApprovalRequestStatusEntity set reqStatus =:status, "
			+ " revertBy =:userName, actionBy = null, checkcomments = null, actionDateTime = null, "
			+ " revertDateTime = CURRENT_TIMESTAMP where requestId =:reqId ")
	public void updateStatusActionByCommentsForRevert(@Param("status") String status, 
			@Param("userName") String userName,
			@Param("reqId") Long reqId);
}


