package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1AdvanceAdjustmentFileUploadEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("Gstr1ATARepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1ATARepository
		extends JpaRepository<Gstr1AdvanceAdjustmentFileUploadEntity, Long>,
		JpaSpecificationExecutor<Gstr1AdvanceAdjustmentFileUploadEntity> {

	@Modifying
	@Query("UPDATE Gstr1AdvanceAdjustmentFileUploadEntity txpd SET "
			+ "txpd.isDelete= TRUE WHERE txpd.txpdInvKey IN (:txpdKey) ")
	public void updateKey(@Param("txpdKey") String txpdKey);

	@Modifying
	@Query("UPDATE Gstr1AdvanceAdjustmentFileUploadEntity doc SET "
			+ "doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.modifiedOn = "
			+ "CURRENT_TIMESTAMP WHERE doc.returnPeriod = :retPeriod AND doc.sgstin = :sgstin "
			+ "AND doc.isDelete = false AND doc.id <= :vMaxId")
	void updateSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("vMaxId") Long vMaxId);

	@Query("SELECT COUNT(*) FROM Gstr1AdvanceAdjustmentFileUploadEntity "
			+ "WHERE sgstin=:gstin AND returnPeriod = :taxperiod ")
	public int gstinCount(@Param("gstin") String gstin,
			@Param("taxperiod") String taxperiod);

	@Modifying
	@Query("UPDATE Gstr1AdvanceAdjustmentFileUploadEntity doc SET doc.isSaved"
			+ "=true, doc.isGstnError=false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE Gstr1AdvanceAdjustmentFileUploadEntity doc SET "
			+ "doc.isGstnError=true, doc.isSaved=false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE Gstr1AdvanceAdjustmentFileUploadEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND  b.id IN (:id) ")
	public int UpdateId(@Param("id") List<Long> id);

	@Query("SELECT doc.id FROM Gstr1AdvanceAdjustmentFileUploadEntity doc "
			+ "WHERE doc.txpdInvKey IN (:docKeys) AND doc.isDelete = false ")
	List<Long> findActiveDocsByDocKeys(@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Gstr1AdvanceAdjustmentFileUploadEntity doc SET doc.isDelete=true,"
			+ "doc.modifiedOn =:updatedDate,doc.modifiedBy=:updatedBy "
			+ "WHERE doc.id IN (:ids) ")
	public void updateDuplicateDocDeletionByDocKeys(
			@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy);

	@Query("SELECT COUNT(*) FROM Gstr1AdvanceAdjustmentFileUploadEntity "
			+ "WHERE sgstin=:gstin AND derivedRetPeriod BETWEEN "
			+ ":derivedTaxPeriodFrom AND :derivedTaxPeriodTo ")
	public int gstinCountByRetPerFromTo(@Param("gstin") String gstin,
			@Param("derivedTaxPeriodFrom") int derivedTaxPeriodFrom,
			@Param("derivedTaxPeriodTo") int derivedTaxPeriodTo);

	@Query("SELECT COUNT(*) FROM Gstr1AdvanceAdjustmentFileUploadEntity  WHERE  "
			+ " isDelete = false and sgstin=:sgstin and returnPeriod=:returnPeriod")
	public int isAdAjuDataAvail(@Param("sgstin") String sgstin,
			@Param("returnPeriod") String returnPeriod);

}
