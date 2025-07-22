package com.ey.advisory.app.data.repositories.client.gstr7trans;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;

@Repository("Gstr7TransDocHeaderRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr7TransDocHeaderRepository
		extends JpaRepository<Gstr7TransDocHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr7TransDocHeaderEntity doc SET doc.isDelete=true,"
			+ "doc.modifiedOn=:modifiedOn,doc.modifiedBy=:modifiedBy "
			+ "WHERE doc.id IN (:ids)")
	void updateDocDeletion(@Param("ids") List<Long> ids,
			@Param("modifiedOn") LocalDateTime updatedDate,
			@Param("modifiedBy") String updatedBy);

	@Query("SELECT doc FROM Gstr7TransDocHeaderEntity doc "
			+ "WHERE doc.docKey = :docKey AND doc.isDelete = false ")
	public List<Gstr7TransDocHeaderEntity> findDocsByDocKey(
			@Param("docKey") String docKey);

	@Query("SELECT doc.id FROM Gstr7TransDocHeaderEntity doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDelete = false")
	List<Long> findActiveDocsByDocKeys(@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Gstr7TransDocHeaderEntity doc SET doc.isDelete=true,"
			+ "doc.modifiedOn=:modifiedOn,doc.modifiedBy=:modifiedBy "
			+ "WHERE doc.id IN (:ids)")
	public void updateDuplicateDocDeletionByDocKeys(
			@Param("ids") List<Long> ids,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("modifiedBy") String modifiedBy);

	@Query("SELECT COUNT(doc) FROM Gstr7TransDocHeaderEntity doc "
			+ "WHERE doc.fileId=:fileId AND "
			+ "doc.isError=true AND doc.dataOriginTypeCode='E' AND doc.isDelete = false")
	public Integer businessValidationCount(@Param("fileId") Long fileId);

	@Query("SELECT COUNT(doc) FROM Gstr7TransDocHeaderEntity doc "
			+ "WHERE doc.fileId=:fileId AND "
			+ "doc.isError=false AND doc.isProcessed=true AND doc.dataOriginTypeCode='E' AND doc.isDelete = false")
	public Integer processedCount(@Param("fileId") Long fileId);

	@Query("SELECT doc FROM Gstr7TransDocHeaderEntity doc "
			+ "WHERE doc.docKey = :docKey AND doc.isDelete = false and "
			+ "doc.supplyType <> 'CAN' AND doc.isProcessed = true")
	public Optional<Gstr7TransDocHeaderEntity> findOrgDocByDocKey(
			@Param("docKey") String docKey);

	@Query("SELECT doc.docKey,doc.isProcessed FROM Gstr7TransDocHeaderEntity doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDelete = false and "
			+ "doc.supplyType <> 'CAN' AND doc.isProcessed = true")
	public List<Object[]> findOrgDocByDocKey(
			@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Gstr7TransDocHeaderEntity doc SET doc.batchId=:batchId,"
			+ "doc.isSentToGstn=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,"
			+ "doc.modifiedOn = CURRENT_TIMESTAMP WHERE doc.returnPeriod = :retPeriod "
			+ "AND doc.deductorGstin = :deductorGstin AND doc.section = :section "
			+ "AND doc.isDelete = false AND doc.id <= :hMaxId AND "
			+ "(doc.supplyType != 'CAN' OR doc.supplyType IS NULL)")
	public void updateSaveDetails(@Param("batchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("deductorGstin") String sgstin,
			@Param("section") String section, @Param("hMaxId") Long hMaxId);

	@Modifying
	@Query("UPDATE Gstr7TransDocHeaderEntity doc SET doc.batchId=:batchId,"
			+ "doc.isSentToGstn=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,"
			+ "doc.modifiedOn = CURRENT_TIMESTAMP WHERE doc.returnPeriod = :retPeriod "
			+ "AND doc.deductorGstin = :deductorGstin AND doc.section = :section "
			+ "AND doc.isDelete = false AND doc.id <= :hMaxId AND "
			+ "doc.supplyType = 'CAN'")
	public void updateSaveDetailsCan(@Param("batchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("deductorGstin") String sgstin,
			@Param("section") String section, @Param("hMaxId") Long hMaxId);

	@Modifying
	@Query("UPDATE Gstr7TransDocHeaderEntity doc SET doc.isSavedToGstn = true,"
			+ "doc.modifiedOn = CURRENT_TIMESTAMP ,doc.savedToGSTNDate = CURRENT_TIMESTAMP "
			+ "WHERE doc.batchId = :id AND doc.isDelete = false")
	public void markDocsAsSavedForBatch(@Param("id") Long id);
	
	@Modifying
	@Query("UPDATE Gstr7TransDocHeaderEntity doc SET doc.gstnError = true,"
			+ "doc.modifiedOn = CURRENT_TIMESTAMP ,doc.savedToGSTNDate = CURRENT_TIMESTAMP WHERE "
			+ " doc.batchId = :id AND doc.isDelete = false")
	public void markDocsAsErrorForBatch(@Param("id") Long id);


}