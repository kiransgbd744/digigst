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

import com.ey.advisory.app.data.entities.client.asprecon.ApprovalRequestEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */

@Repository("ApprovalMakerRequestRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ApprovalMakerRequestRepository
		extends JpaRepository<ApprovalRequestEntity, Long>,
		JpaSpecificationExecutor<ApprovalRequestEntity> {

	@Query("SELECT prt.requestId, prt.createdOn, prt.gstin, chd.checkerName, chd.checkerEmail, prt.action, "
			+ "chd.reqStatus, chd.actionDateTime,chd.checkcomments "
			+ "FROM ApprovalRequestEntity prt INNER JOIN ApprovalRequestStatusEntity chd "
			+ "ON prt.requestId = chd.requestId "
			+ "WHERE prt.makerName =:makerName AND prt.taxPeriod =:taxPeriod AND prt.returnType =:retType "
			+ "AND prt.entityId =:entityId AND prt.gstin IN (:gstin)")
	List<Object[]> findByNameAndTaxPeriodANDRetTypeAndEntityId(
			@Param("makerName") String makerName,
			@Param("taxPeriod") String taxPeriod,
			@Param("retType") String retType, @Param("entityId") Long entityId,
			@Param("gstin") List<String> gstin);

	@Query("SELECT prt.reqKey "
			+ "FROM ApprovalRequestEntity prt INNER JOIN ApprovalRequestStatusEntity chd "
			+ "ON prt.requestId = chd.requestId "
			+ "WHERE prt.entityId =:entityId AND prt.gstin IN (:gstin) "
			+ "AND prt.returnType =:retType AND prt.taxPeriod =:taxPeriod "
			+ "AND chd.reqStatus IN (:reqStatus) AND prt.makerName =:userName")
	List<String> findRequestKeys(@Param("entityId") Long entityId,
			@Param("gstin") List<String> gstin,
			@Param("retType") String retType,
			@Param("taxPeriod") String taxPeriod,
			@Param("reqStatus") List<String> reqStatus,
			@Param("userName") String userName);

	@Query("SELECT prt.reqKey "
			+ "FROM ApprovalRequestEntity prt INNER JOIN ApprovalRequestStatusEntity chd "
			+ "ON prt.requestId = chd.requestId "
			+ "WHERE prt.entityId =:entityId AND prt.gstin IN (:gstin) "
			+ "AND prt.returnType =:retType AND prt.taxPeriod =:taxPeriod "
			+ "AND chd.reqStatus IN (:reqStatus) ")
	List<String> findRequestKey(@Param("entityId") Long entityId,
			@Param("gstin") List<String> gstin,
			@Param("retType") String retType,
			@Param("taxPeriod") String taxPeriod,
			@Param("reqStatus") List<String> reqStatus);

	@Modifying
	@Query("UPDATE ApprovalRequestEntity SET snapPath =:snapPath, snapCreatedOn =CURRENT_TIMESTAMP "
			+ "WHERE requestId =:requestId")
	public void updateSnapShotPdf(@Param("requestId") Long requestId,
			@Param("snapPath") String snapPath);
	
	@Query("select prt.gstin, prt.taxPeriod, prt.returnType, prt.makerEmail, chd.checkerEmail, prt.action "
			+ " from ApprovalRequestEntity prt INNER JOIN ApprovalRequestStatusEntity "
			+ " chd on prt.requestId = chd.requestId "
			+ " where prt.requestId =:reqId ")
	public List<Object[]> findMakerAndCheckerEmails(@Param("reqId") Long reqId);

}