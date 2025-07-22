package com.ey.advisory.app.data.repositories.client.gstr8;

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

import com.ey.advisory.app.data.entities.gstr8.Gstr8UploadProcessedEntity;

/**
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr8UploadPsdRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr8UploadPsdRepository
		extends JpaRepository<Gstr8UploadProcessedEntity, Long>,
		JpaSpecificationExecutor<Gstr8UploadProcessedEntity> {

	List<Gstr8UploadProcessedEntity> findByGstinAndReturnPeriodAndIsActive(
			String gstin, String returnPeriod, boolean isActive);

	List<Gstr8UploadProcessedEntity> findByGstinAndIdentifierAndDocTypeAndSgstinAndIsActive(
			String gstin, String identifier, String docType, String sgstin,
			boolean isActive);

	List<Gstr8UploadProcessedEntity> findByGstinAndReturnPeriodAndIdentifierAndSgstinAndDocTypeAndIsActive(
			String gstin, String returnPeriod, String identifier, String sgstin,
			String docType, boolean isActive);

	@Modifying
	@Query("Update Gstr8UploadProcessedEntity SET isActive = false  WHERE "
			+ "docKey in (:docKeyList) AND isActive = true")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList);

	public List<Gstr8UploadProcessedEntity> findByFileId(Long fileId);

	@Modifying
	@Query("UPDATE Gstr8UploadProcessedEntity doc SET doc.isSavedGstn = true,"
			+ "doc.updatedDate = :now,doc.savedGstnDate = CURRENT_TIMESTAMP "
			+ "WHERE doc.gstnBatchId = :id AND doc.isActive = false")
	public void markDocsAsSavedForBatch(@Param("id") Long id,
			@Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Gstr8UploadProcessedEntity doc SET doc.gstnError = true,"
			+ "doc.updatedDate = :now,doc.savedGstnDate = CURRENT_TIMESTAMP WHERE "
			+ " doc.gstnBatchId = :id AND doc.isActive = false")
	public void markDocsAsErrorForBatch(@Param("id") Long id,
			@Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Gstr8UploadProcessedEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentGstn=true,doc.sentGstnDate = CURRENT_TIMESTAMP,"
			+ "doc.updatedDate = :now WHERE doc.id IN (:ids)")
	public void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids, @Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Gstr8UploadProcessedEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentGstn=true,doc.sentGstnDate = CURRENT_TIMESTAMP,"
			+ "doc.updatedDate = :updatedDate WHERE doc.returnPeriod = :returnPeriod "
			+ "AND doc.gstin = :gstin AND doc.section = :section "
			+ "AND doc.isActive = false AND doc.id <= :hMaxId AND (doc.supplyType != 'CAN' OR doc.supplyType IS NULL)")
	public void updateNewReturnsSumryBatchId(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("returnPeriod") String returnPeriod, @Param("gstin") String gstin,
			@Param("section") String section, @Param("hMaxId") Long hMaxId,
			@Param("updatedDate") LocalDateTime updatedDate);

	@Query("SELECT doc FROM Gstr8UploadProcessedEntity doc WHERE  "
			+ "doc.gstin = :gstin AND doc.section = :section AND "
			+ "doc.returnPeriod = :returnPeriod AND doc.sgstin = :sgstin AND "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.isActive = true")
	public List<Gstr8UploadProcessedEntity> findByGstnBatchIdAndDocKeyIn(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("gstin") String gstin, @Param("returnPeriod") String returnPeriod,
			@Param("sgstin") String sgstin, @Param("section") String section);

	@Query("SELECT doc FROM Gstr8UploadProcessedEntity doc WHERE "
			+ "doc.gstin = :gstin AND doc.section = :section AND doc.returnPeriod = :returnPeriod AND doc.sgstin = :sgstin AND "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.isActive = true "
			+ "AND doc.originalReturnPeriod = :originalReturnPeriod")
	public List<Gstr8UploadProcessedEntity> findByGstnBatchIdAndDocKeyIn(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("gstin") String gstin, @Param("returnPeriod") String returnPeriod,
			@Param("sgstin") String sgstin, @Param("section") String section,
			@Param("originalReturnPeriod") String originalReturnPeriod);

	@Modifying
	@Query("UPDATE Gstr8UploadProcessedEntity doc SET doc.isSavedGstn = true,"
			+ "doc.updatedDate = CURRENT_TIMESTAMP,doc.savedGstnDate = CURRENT_TIMESTAMP, "
			+ "doc.gstnError = false WHERE "
			+ "doc.gstin = :gstin AND doc.section = :section AND doc.returnPeriod = :returnPeriod AND doc.sgstin = :sgstin AND "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.isActive = true")
	public void markDocsAsSavedForBatchByErroredDocKeys(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("gstin") String gstin, @Param("returnPeriod") String returnPeriod,
			@Param("sgstin") String sgstin, @Param("section") String section);

	@Modifying
	@Query("UPDATE Gstr8UploadProcessedEntity doc SET doc.isSavedGstn = true,"
			+ "doc.updatedDate = CURRENT_TIMESTAMP,doc.savedGstnDate = CURRENT_TIMESTAMP, "
			+ "doc.gstnError = false WHERE "
			+ "doc.gstin = :gstin AND doc.section = :section "
			+ "AND doc.returnPeriod = :returnPeriod  AND doc.sgstin = :sgstin"
			+ " AND doc.originalReturnPeriod = :originalReturnPeriod AND "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.isActive = true")
	public void markDocsAsSavedForBatchByErroredDocKeys(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("gstin") String gstin, @Param("returnPeriod") String returnPeriod,
			@Param("sgstin") String sgstin, @Param("section") String section,
			@Param("originalReturnPeriod") String originalReturnPeriod);

	@Query("SELECT count(*) FROM Gstr8UploadProcessedEntity doc "
			+ "WHERE doc.gstnError= true AND doc.gstin =:gstin "
			+ " AND doc.returnPeriod =:returnPeriod "
			+ " AND doc.gstnBatchId =:batchId ")
	public Integer countOfBatchRecords(@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("batchId") Long batchId);

}
