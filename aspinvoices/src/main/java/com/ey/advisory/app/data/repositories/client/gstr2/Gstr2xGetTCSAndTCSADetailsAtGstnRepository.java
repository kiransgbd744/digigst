package com.ey.advisory.app.data.repositories.client.gstr2;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2xTcsAndTcsaInvoicesEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("Gstr2xGetTCSAndTCSADetailsAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2xGetTCSAndTCSADetailsAtGstnRepository
		extends JpaRepository<GetGstr2xTcsAndTcsaInvoicesEntity, Long> {

	// Curently isDelete is not added in table
	@Modifying
	@Query("UPDATE GetGstr2xTcsAndTcsaInvoicesEntity b SET b.isDelete = true ,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.sgstin = :gstin AND b.retPeriod = :returnPeriod")
	void softlyDeleteTdsHeader(@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("SELECT doc FROM GetGstr2xTcsAndTcsaInvoicesEntity doc "
			+ "WHERE doc.docKey IN (:docKey) AND " + " doc.isDelete = false ")
	List<GetGstr2xTcsAndTcsaInvoicesEntity> findByfilproKey(
			@Param("docKey") List<String> docKey);

	@Modifying
	@Query("UPDATE GetGstr2xTcsAndTcsaInvoicesEntity doc SET doc.userActionFlag = 'U' "
			+ "WHERE doc.docKey IN (:docKey) AND doc.isDelete = false")
	void updateUserActionFlag(@Param("docKey") List<String> docKey);

	@Query("SELECT doc.id FROM GetGstr2xTcsAndTcsaInvoicesEntity doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDelete = false ")
	List<Long> findActiveDocsByDocKeys(@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE GetGstr2xTcsAndTcsaInvoicesEntity doc SET doc.userActionFlag = 'E', "
			+ "doc.modifiedOn =:updatedDate,doc.modifiedBy =:updatedBy "
			+ "WHERE doc.id IN (:ids) ")
	void updateUserActionByDocKeys(@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy);

	@Query("SELECT doc FROM GetGstr2xTcsAndTcsaInvoicesEntity doc WHERE "
			+ "doc.recordType = :recordType AND doc.sgstin = :gstin AND "
			+ "doc.cgstin = :ctin AND doc.retPeriod = :retPeriod AND "
			+ "doc.deductorUploadedMonth = :deductorUplMonth AND "
			+ "doc.orgDeductorUploadedMonth = :orgDeductorUplMonth AND "
			+ "doc.isDelete = false ")
	List<GetGstr2xTcsAndTcsaInvoicesEntity> findRecords(
			@Param("recordType") String recordType,
			@Param("gstin") String gstin, @Param("retPeriod") String retPeriod,
			@Param("ctin") String ctin,
			@Param("deductorUplMonth") String deductorUplMonth,
			@Param("orgDeductorUplMonth") String orgDeductorUplMonth);

	@Query("SELECT doc FROM GetGstr2xTcsAndTcsaInvoicesEntity doc WHERE "
			+ "doc.recordType = :recordType AND doc.sgstin = :gstin AND "
			+ "doc.cgstin = :ctin AND doc.retPeriod = :retPeriod AND "
			+ "doc.deductorUploadedMonth = :deductorUplMonth AND "
			+ "doc.isDelete = false ")

	List<GetGstr2xTcsAndTcsaInvoicesEntity> findRecordsWithOrgDrUplMonth(
			@Param("recordType") String recordType,
			@Param("gstin") String gstin, @Param("retPeriod") String retPeriod,
			@Param("ctin") String ctin,
			@Param("deductorUplMonth") String deductorUplMonth);

	@Query("SELECT doc FROM GetGstr2xTcsAndTcsaInvoicesEntity doc "
			+ "WHERE doc.psKey IN (:docKey) AND " + " doc.isDelete = false ")
	List<GetGstr2xTcsAndTcsaInvoicesEntity> findByfilPSproKey(
			@Param("docKey") List<String> docKey);

	@Modifying
	@Query("UPDATE GetGstr2xTcsAndTcsaInvoicesEntity doc SET doc.userActionFlag = 'U' "
			+ "WHERE doc.docKey =:docKey AND doc.isDelete = false")
	void updatePsUserActionFlag(@Param("docKey") String docKey);

	@Query("SELECT doc FROM GetGstr2xTcsAndTcsaInvoicesEntity doc "
			+ "WHERE doc.isDelete = false ")
	List<GetGstr2xTcsAndTcsaInvoicesEntity> findActiveRecords();
}
