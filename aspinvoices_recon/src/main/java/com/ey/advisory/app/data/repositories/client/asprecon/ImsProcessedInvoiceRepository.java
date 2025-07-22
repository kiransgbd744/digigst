package com.ey.advisory.app.data.repositories.client.asprecon;

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

import com.ey.advisory.app.service.ims.ImsProcessedInvoiceEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Repository("ImsProcessedInvoiceRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ImsProcessedInvoiceRepository
		extends JpaRepository<ImsProcessedInvoiceEntity, Long>,
		JpaSpecificationExecutor<ImsProcessedInvoiceEntity> {

	public List<ImsProcessedInvoiceEntity> findByFileId(Long fileId);

	@Modifying
	@Query("Update ImsProcessedInvoiceEntity SET i"
			+ "sDelete = true,modifiedOn = CURRENT_TIMESTAMP, modifiedBy =:modifiedBy "
			+ "   WHERE  docKey in (:docKeyList) AND isDelete = false")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList,
			@Param("modifiedBy") String modifiedBy);

	@Query("select e from ImsProcessedInvoiceEntity e WHERE"
			+ " e.isDelete = false AND e.isSavedToGstin = false "
			+ " AND e.isSentToGstin = false AND "
			+ " e.recipientGstin=:recipientGstin AND "
			+ " e.tableType IN (:tableType) AND e.gstnErrorDesc IS NULL AND e.refId IS NULL")
	public List<ImsProcessedInvoiceEntity> findActiveRecipientGstin(
			@Param("recipientGstin") String recipientGstin, @Param("tableType") List<String> tableType);

	@Query("select e.tableType,count(e) from ImsProcessedInvoiceEntity e WHERE"
			+ " e.isDelete = false AND e.isSavedToGstin = false "
			+ " AND e.isSentToGstin = false AND "
			+ " e.recipientGstin=:recipientGstin AND "
			+ " e.tableType IN (:tableType) AND e.gstnErrorDesc IS NULL"
			+ " GROUP BY e.tableType")
	public List<Object[]> findActiveCountRecipientGstin(
			@Param("recipientGstin") String recipientGstin, @Param("tableType") List<String> tableType);
	
	@Modifying
	@Query("Update ImsProcessedInvoiceEntity SET "
			+ "isSentToGstin =:isSentToGstin, gstnSentDateTime = CURRENT_TIMESTAMP, "
			+ " modifiedOn = CURRENT_TIMESTAMP, "
			+ " modifiedBy = 'SYSTEM', saveBatchId =:saveBatchId, refId=:refId"
			+ "  WHERE id In (:id)")
	int updateBatchIdAndSentFlag(@Param("id") List<Long> id,
			@Param("saveBatchId") Long saveBatchId,
			@Param("refId") String refId,
			@Param("isSentToGstin") boolean isSentToGstin);

	@Modifying
	@Query("Update ImsProcessedInvoiceEntity SET "
			+ "isSavedToGstin = :isSavedToGstin, modifiedOn = CURRENT_TIMESTAMP, "
			+ " modifiedBy = 'SYSTEM'  WHERE id In (:id)")
	int updateSaveFlag(@Param("id") List<Long> id,
			@Param("isSavedToGstin") boolean isSavedToGstin);

	@Modifying
	@Query("Update ImsProcessedInvoiceEntity SET "
			+ "isSavedToGstin =:isSavedToGstin, gstnSavedDateTime = CURRENT_TIMESTAMP, "
			+ " isSentToGstin =:isSentToGstin, modifiedOn = CURRENT_TIMESTAMP, "
			+ " modifiedBy = 'SYSTEM'  WHERE refId =:refId ")
	int updateSaveFlagByRefId(@Param("refId") String refId,
			@Param("isSavedToGstin") boolean isSavedToGstin,
			@Param("isSentToGstin") boolean isSentToGstin);

	@Modifying
	@Query("Update ImsProcessedInvoiceEntity SET "
			+ "isSavedToGstin = false, gstnSavedDateTime = CURRENT_TIMESTAMP, "
			+ " isSentToGstin = false, modifiedOn = CURRENT_TIMESTAMP, "
			+ " gstnErrorCode =:gstnErrorCode, gstnErrorDesc =:gstnErrorDesc, "
			+ " modifiedBy = 'SYSTEM'  WHERE gstnSaveDockey =:gstnSaveDockey "
			+ " AND isSavedToGstin = false")
	int updateErrorByGstnDocKey(@Param("gstnSaveDockey") String gstnSaveDockey,
			@Param("gstnErrorCode") String gstnErrorCode,
			@Param("gstnErrorDesc") String gstnErrorDesc);
	
	@Query(value = "WITH LatestDocs AS ( SELECT *, "
			+ "           ROW_NUMBER() OVER (PARTITION BY DOC_KEY "
			+ " ORDER BY SAVED_TO_GSTIN_DATE DESC) AS rn "
			+ "    FROM TBL_GETIMS_PROCESSED "
			+ "    WHERE IS_SAVED_TO_GSTIN = true AND "
			+ " RECIPIENT_GSTIN =:recipientGstin ) SELECT * "
			+ " FROM LatestDocs WHERE rn = 1 "
			+ " ORDER BY ID DESC", nativeQuery = true)
	List<ImsProcessedInvoiceEntity> findLatestSavedToGstin(
			@Param("recipientGstin") String recipientGstin);
	
	@Modifying
	@Query("Update ImsProcessedInvoiceEntity SET "
			+ "isSavedToGstin =:isSavedToGstin, gstnSavedDateTime = CURRENT_TIMESTAMP, "
			+ " isSentToGstin =:isSentToGstin, modifiedOn = CURRENT_TIMESTAMP, "
			+ " modifiedBy = 'SYSTEM'  WHERE refId =:refId AND gstnSaveDockey "
			+ " NOT IN (:gstnSaveDockey)")
	int updateSaveFlagByRefIdAndSaveDocKey(@Param("refId") String refId,
			@Param("isSavedToGstin") boolean isSavedToGstin,
			@Param("isSentToGstin") boolean isSentToGstin,
			@Param("gstnSaveDockey") List<String> gstnSaveDockey);

	@Query("select e.tableType,count(e) from ImsProcessedInvoiceEntity e WHERE"
			+ " e.isDelete = false AND e.isSavedToGstin = false "
			+ " AND e.isSentToGstin = false AND "
			+ " e.recipientGstin=:recipientGstin AND "
			+ " e.tableType IN (:tableType) AND e.gstnErrorDesc IS NULL "
			+ " AND e.actionResponse IN ('A','P','R') GROUP BY e.tableType")
	public List<Object[]> findActiveAPRCountRecipientGstin(
			@Param("recipientGstin") String recipientGstin, @Param("tableType") List<String> tableType);
	
	@Query("select e.tableType,count(e) from ImsProcessedInvoiceEntity e WHERE"
			+ " e.isDelete = false AND e.isSavedToGstin = false "
			+ " AND e.isSentToGstin = false AND "
			+ " e.recipientGstin=:recipientGstin AND "
			+ " e.tableType IN (:tableType) AND e.gstnErrorDesc IS NULL "
			+ " AND e.actionResponse IN ('N') GROUP BY e.tableType")
	public List<Object[]> findActiveNCountRecipientGstin(
			@Param("recipientGstin") String recipientGstin, @Param("tableType") List<String> tableType);
	
	@Modifying
	@Query("UPDATE ImsProcessedInvoiceEntity e SET " +
		       " e.isDelete = true, e.isManualDelete = true, " +
		       " e.actionDigi = null, " +
		       " e.digiActionDateTime = null " +
		       //" e.isSavedToGstin = null " +
		       " WHERE e.recipientGstin IN (:gstins) " +
		       " AND  e.tableType IN (:tableTypes) " +
		       " AND e.actionResponse IN (:actions) " +
		       " AND e.isSavedToGstin = false " + " AND e.isSentToGstin = false " +
		       " AND e.availableInIms = true AND e.isDelete = false")
	int deleteUnsavedDigiGSTActions(
	    @Param("gstins") List<String> gstins,
	    @Param("tableTypes") List<String> tableTypes,
	    @Param("actions") List<String> actions);
	
	@Query(value = "WITH LatestDocs AS ( "
	        + "SELECT DOC_KEY, ACTION_RESPONSE, "
	        + "ROW_NUMBER() OVER (PARTITION BY DOC_KEY ORDER BY SAVED_TO_GSTIN_DATE DESC) AS rn "
	        + "FROM TBL_GETIMS_PROCESSED "
	        + "WHERE IS_SAVED_TO_GSTIN = TRUE "
	        + "AND RECIPIENT_GSTIN = :recipientGstin "
	        + "AND DOC_KEY IN (:docKeyList) "
	        + "AND TABLE_TYPE IN (:newTableType)) "
	        + "SELECT DOC_KEY, ACTION_RESPONSE "
	        + "FROM LatestDocs "
	        + "WHERE rn = 1",
	   nativeQuery = true)
	List<Object[]> findLatestActionResponsesByDocKeyList(
	    @Param("recipientGstin") String recipientGstin,
	    @Param("docKeyList") List<String> docKeyList,
	    @Param("newTableType") List<String> newTableType);
	
	
	@Query(value = "SELECT * FROM  TBL_GETIMS_PROCESSED "
			+ " WHERE SOURCE_TYPE = 'API' AND IS_DELETE = false "
			+ "  AND TO_DATE(CREATED_ON) BETWEEN :fromDate AND :toDate", nativeQuery = true)
	List<ImsProcessedInvoiceEntity> findBySourceTypeApiAndCreatedOnBetweenActive(
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query(value = "SELECT * FROM  TBL_GETIMS_PROCESSED "
			+ " WHERE SOURCE_TYPE = 'API' AND IS_DELETE = true "
			+ "  AND TO_DATE(CREATED_ON) BETWEEN :fromDate AND :toDate", nativeQuery = true)
	List<ImsProcessedInvoiceEntity> findBySourceTypeApiAndCreatedOnBetweenInActive(
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);
	
	@Query(value = "SELECT * FROM  TBL_GETIMS_PROCESSED "
			+ " WHERE SOURCE_TYPE = 'API' "
			+ "  AND TO_DATE(CREATED_ON) BETWEEN :fromDate AND :toDate", nativeQuery = true)
	List<ImsProcessedInvoiceEntity> findBySourceTypeApiAndCreatedOnBetweenTotal(
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);
	
	@Query(value = "SELECT * FROM  TBL_GETIMS_PROCESSED "
			+ " WHERE SOURCE_TYPE = 'API' AND IS_DELETE = false "
			+ "  AND TO_DATE(CREATED_ON) BETWEEN :fromDate AND :toDate "
			+ " AND RECIPIENT_GSTIN  IN (:recipientGstin)", nativeQuery = true)
	List<ImsProcessedInvoiceEntity> findBySourceTypeApiAndCreatedOnBetweenActive(
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate,
			@Param("recipientGstin") List<String> recipientGstin);

	@Query(value = "SELECT * FROM  TBL_GETIMS_PROCESSED "
			+ " WHERE SOURCE_TYPE = 'API' AND IS_DELETE = true "
			+ "  AND TO_DATE(CREATED_ON) BETWEEN :fromDate AND :toDate "
			+ " AND RECIPIENT_GSTIN  IN (:recipientGstin)", nativeQuery = true)
	List<ImsProcessedInvoiceEntity> findBySourceTypeApiAndCreatedOnBetweenInActive(
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate,
			@Param("recipientGstin") List<String> recipientGstin);
	
	@Query(value = "SELECT * FROM  TBL_GETIMS_PROCESSED "
			+ " WHERE SOURCE_TYPE = 'API' "
			+ "  AND TO_DATE(CREATED_ON) BETWEEN :fromDate AND :toDate "
			+ " AND RECIPIENT_GSTIN  IN (:recipientGstin) ", nativeQuery = true)
	List<ImsProcessedInvoiceEntity> findBySourceTypeApiAndCreatedOnBetweenTotal(
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate,
			@Param("recipientGstin") List<String> recipientGstin);
}
