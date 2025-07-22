package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.ApprovalStatusEntity;

/**
 * 
 * @author Mohana.Dasari
 *
 */

@Repository("ApprovalStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ApprovalStatusRepository
		extends JpaRepository<ApprovalStatusEntity, Long>,
		JpaSpecificationExecutor<ApprovalStatusEntity> {

	@Query("SELECT doc FROM ApprovalStatusEntity doc WHERE doc.id = (SELECT "
			+ "MAX(innerDoc.id) FROM ApprovalStatusEntity innerDoc WHERE "
			+ "innerDoc.isDelete = false AND "
			+ "innerDoc.gstin=:gstin AND innerDoc.returnPeriod=:returnPeriod )")
	ApprovalStatusEntity findByStatus(@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod);

	@Modifying
	@Query("UPDATE ApprovalStatusEntity doc SET doc.approvalStatus = "
			+ ":approvalStatus, doc.approvedBy = :user, doc.approvedOn = "
			+ ":apprvdDate WHERE doc.id IN (SELECT MAX(iDoc.id) FROM "
			+ "ApprovalStatusEntity iDoc WHERE iDoc.isDelete = false AND "
			+ "iDoc.gstin=:gstin AND iDoc.returnPeriod=:returnPeriod)")
	void updategstin(@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("approvalStatus") Integer approvalStatus,
			@Param("user") String user,
			@Param("apprvdDate") LocalDateTime apprvdDate);

	/*@Query("SELECT doc FROM ApprovalStatusEntity doc WHERE doc.id = :id AND "
			+ "doc.isDelete = false AND "
			+ "doc.gstin=:gstin AND doc.returnPeriod=:returnPeriod")
	ApprovalStatusEntity findByStatusId(@Param("id") Long id,
			@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod);*/

	@Modifying
	@Query("UPDATE ApprovalStatusEntity doc SET doc.approvalStatus = "
			+ ":approvalStatus, doc.approvedBy = :user, doc.approvedOn = "
			+ ":apprvdDate WHERE doc.id = :id AND "
			+ "doc.isDelete = false AND "
			+ "doc.gstin=:gstin AND doc.returnPeriod=:returnPeriod")
	void updateGstinId(@Param("id") Long id, @Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("approvalStatus") Integer approvalStatus,
			@Param("user") String user,
			@Param("apprvdDate") LocalDateTime apprvdDate);

}
