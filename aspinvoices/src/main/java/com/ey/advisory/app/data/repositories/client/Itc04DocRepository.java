/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
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

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Itc04DocRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Itc04DocRepository
		extends JpaRepository<Itc04HeaderEntity, Long>,
		JpaSpecificationExecutor<Itc04HeaderEntity> {

	@Modifying
	@Query("UPDATE Itc04HeaderEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentToGstn=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,"
			+ "doc.modifiedOn = :now WHERE doc.id IN (:ids)")
	public void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids, @Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Itc04HeaderEntity doc SET doc.isSavedToGstn  = true,"
			+ "doc.modifiedOn = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP "
			+ "WHERE doc.gstnBatchId = :gstnBatchId AND doc.isDeleted = false")
	public void markDocsAsSavedForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Itc04HeaderEntity doc SET doc.isGstnError = true,"
			+ "doc.modifiedOn = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP WHERE "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.isDeleted = false")
	public void markDocsAsErrorForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Itc04HeaderEntity doc SET doc.isDeleted=true,"
			+ "doc.modifiedOn=:updatedDate,doc.modifiedBy=:updatedBy "
			+ "WHERE doc.id IN (:ids)")
	void updateDocDeletion(@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime modifiedOn,
			@Param("updatedBy") String modifiedBy);

	@Query("SELECT doc.id FROM Itc04HeaderEntity doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false and  "
			+ "doc.isSubmitted = false")
	List<Long> findActiveDocsByDocKeys(@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Itc04HeaderEntity doc SET doc.isDeleted=true,"
			+ "doc.modifiedOn=:updatedDate,doc.modifiedBy=:updatedBy "
			+ "WHERE doc.id IN (:ids) and doc.isSubmitted = false")
	public void updateDuplicateDocDeletionByDocKeys(
			@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy);

	@Query("SELECT COUNT(doc) FROM Itc04HeaderEntity doc "
			+ "WHERE doc.fileId=:acceptanceId AND "
			+ "doc.isError=true AND doc.isDeleted = false ") // AND
																// doc.dataOriginTypeCode='E'
	public Integer businessValidationCount(
			@Param("acceptanceId") final Long fileId);

	@Query("SELECT doc FROM Itc04HeaderEntity doc "
			+ "WHERE doc.docKey = :docKey " + " ORDER BY doc.id DESC ")
	public List<Itc04HeaderEntity> findDocsByDocKey(
			@Param("docKey") String docKey);

	@Query("SELECT doc.docKey FROM Itc04HeaderEntity doc WHERE "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.jobWorkerGstin =:jobWorkerGstin")
	public List<String> findDocKeysByBatchIdAndCtin(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("jobWorkerGstin") String jobWorkerGstin);

	@Modifying
	@Query("UPDATE Itc04HeaderEntity doc SET doc.isSavedToGstn = true,"
			+ "doc.modifiedOn = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP, "
			+ "doc.isGstnError = false WHERE "
			+ "doc.docKey NOT IN (:erroredDocKeyList) AND "
			+ "doc.gstnBatchId = :gstnBatchId ")
	public void markDocsAsSavedForBatchByErroredDocKeys(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("erroredDocKeyList") List<String> erroredDocKeyList,
			@Param("now") LocalDateTime now);

	@Query("SELECT doc FROM Itc04HeaderEntity doc "
			+ "WHERE doc.docKey IN (:invKeySetAsList) AND "
			+ " doc.gstnBatchId = :gstnBatchId ")
	public List<Itc04HeaderEntity> findByGstnBatchIdAndDocKeyIn(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("invKeySetAsList") List<String> invKeySetAsList);

	@Query("SELECT doc.docKey FROM Itc04HeaderEntity doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false ")
	public List<String> findCancelDocsByDocKeys(
			@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Itc04HeaderEntity doc SET doc.isSentToGstn = false, doc.isSavedToGstn "
			+ "= false, doc.isGstnError = false, doc.gstnBatchId =null, doc."
			+ "sentToGSTNDate = null, doc.savedToGSTNDate = null, doc."
			+ "gstnErrorCode = null, doc.gstnErrorDesc = null WHERE doc.supplierGstin "
			+ "= :supplierGstin AND doc.qRetPeriod = :qRetPeriod AND doc.tableNumber "
			+ " IN (:tableNumber) AND doc.isDeleted = false AND doc.isSentToGstn = true")
	public void resetSaveItc04AuditColumns(
			@Param("supplierGstin") String supplierGstin,
			@Param("qRetPeriod") String qRetPeriod,
			@Param("tableNumber") List<String> tableNumber);

	@Query("SELECT doc.docKey FROM Itc04HeaderEntity doc "
			+ "WHERE doc.docKey IN (:docKeys) and  "
			+ "doc.isSavedToGstn = true ")
	List<String> findActiveGstinSaveStatusTrue(
			@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Itc04HeaderEntity doc SET doc.isSentToGstn = false, doc.isSavedToGstn = false,"
			+ "doc.isGstnError = false, doc.gstnBatchId =null,doc.sentToGSTNDate = null, "
			+ "doc.savedToGSTNDate = null,doc.gstnErrorCode = null, doc.gstnErrorDesc = null,"
			+ "doc.saveFlag = (CASE WHEN doc.isSavedToGstn = TRUE AND "
			+ " (doc.actionType IS NULL OR doc.actionType <>'''CAN''') THEN 'E' ELSE doc.saveFlag END) "
			+ " WHERE doc.supplierGstin = :supplierGstin AND doc.qRetPeriod = :qRetPeriod "
			+ " AND doc.tableNumber IN (:tableNumber) AND doc.isDeleted = false AND doc.isSentToGstn = true")
	public void resetSaveItc04(@Param("supplierGstin") String supplierGstin,
			@Param("qRetPeriod") String qRetPeriod,
			@Param("tableNumber") List<String> tableNumber);

	@Query("SELECT doc FROM Itc04HeaderEntity doc "
			+ "WHERE doc.deliveryChallanaDate BETWEEN :fromChallanDate "
			+ "AND :toChallanDate AND doc.isDeleted = false AND "
			+ "doc.isProcessed = true AND doc.finYear = :finYear AND doc.supplierGstin IN (:supplierGstins) ")
	List<Itc04HeaderEntity> findActiveDocsByChallanDate(
			@Param("fromChallanDate") LocalDate fromChallanDate,
			@Param("toChallanDate") LocalDate toChallanDate,
			@Param("finYear") String finYear,
			@Param("supplierGstins") List<String> supplierGstins);

	@Query(value = "SELECT * FROM ITC04_HEADER " + "WHERE IS_DELETE = false "
			+ "AND CONCAT(RIGHT(QRETURN_PERIOD, 4), LEFT(QRETURN_PERIOD, 2)) "
			+ "BETWEEN :periodStart AND :periodEnd AND FI_YEAR = :finYear AND IS_PROCESSED = true AND SUPPLIER_GSTIN IN (:supplierGstins)", nativeQuery = true)
	List<Itc04HeaderEntity> findActiveDocsByRetPeriod(
			@Param("periodStart") String periodStart,
			@Param("periodEnd") String periodEnd,
			@Param("finYear") String finYear,
			@Param("supplierGstins") List<String> supplierGstins);

	@Query("SELECT doc FROM Itc04HeaderEntity doc "
			+ "WHERE doc.deliveryChallanaDate BETWEEN :fromChallanDate "
			+ "AND :toChallanDate AND doc.isDeleted = false AND "
			+ "doc.isProcessed = true AND doc.finYear = :finYear ")
	List<Itc04HeaderEntity> findActiveDocsByReturnPeriod(
			@Param("fromChallanDate") String fromChallanDate,
			@Param("toChallanDate") String toChallanDate,
			@Param("finYear") String finYear);

	@Query("SELECT doc FROM Itc04HeaderEntity doc "
			+ "WHERE doc.isDeleted = false AND "
			+ "doc.isProcessed = true AND doc.finYear = :finYear ")
	List<Itc04HeaderEntity> findActiveDocs(@Param("finYear") String finYear);
}