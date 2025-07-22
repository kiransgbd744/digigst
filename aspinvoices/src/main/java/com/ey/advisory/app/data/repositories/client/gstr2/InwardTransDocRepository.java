package com.ey.advisory.app.data.repositories.client.gstr2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.InwardTransDocSearchPredicateBuilder;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.dto.DocSearchReqDto;

@Repository("InwardTransDocRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface InwardTransDocRepository
		extends JpaRepository<InwardTransDocument, Long>,
		JpaSpecificationExecutor<InwardTransDocument> {

	@Query("SELECT COUNT(doc) from InwardTransDocument doc "
			+ "where doc.origSgstin=:sgstin "
			+ "and doc.origDocNo=:origDocNo and doc.origDocDate=:origDocDate")

	public int findInvoices(@Param("sgstin") String sgstin,
			@Param("origDocNo") String origDocNo,
			@Param("origDocDate") LocalDate origDocDate);

	@Query("SELECT doc FROM InwardTransDocument doc "
			+ "WHERE doc.docKey = :docKey " + " ORDER BY doc.id DESC ")
	public List<InwardTransDocument> findDocsByDocKey(
			@Param("docKey") String docKey);

	public default Page<InwardTransDocument> findDocsBySearchCriteria(
			DocSearchReqDto searchParams, Pageable pageRequest) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			InwardTransDocSearchPredicateBuilder builder = StaticContextHolder
					.getBean("DefaultInwardTransDocSearchPredicateBuilder",
							InwardTransDocSearchPredicateBuilder.class);

			List<Predicate> predicates = builder.build(searchParams, root,
					criteriaBuilder);

			// Include the Sort Criteria, so that pagination will return
			// consistent results.
			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("createdDate")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		}, pageRequest);
	}

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET doc.isDeleted=true,"
			+ "doc.updatedDate=:updatedDate,doc.modifiedBy=:updatedBy,"
			+ "doc.inactiveReason = :inactiveReason  "
			+ "WHERE doc.id IN (:ids)")
	void updateDocDeletion(@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy,
			@Param("inactiveReason") String inactiveReason);

	@Query("SELECT COUNT(doc) FROM InwardTransDocument doc "
			+ "WHERE doc.acceptanceId=:acceptanceId AND "
			+ "doc.isError=true AND doc.dataOriginTypeCode='E' AND doc.isDeleted = false")
	public Integer businessValidationCount(
			@Param("acceptanceId") final Long fileId);

	@Query("SELECT COUNT(*) FROM InwardTransDocument "
			+ "WHERE cgstin=:gstin AND taxperiod = :taxperiod ")
	public int gstinCount(@Param("gstin") String gstin,
			@Param("taxperiod") String taxperiod);

	@Query("SELECT doc.cgstin FROM InwardTransDocument doc WHERE "
			+ "doc.receivedDate BETWEEN :fromRcvDate AND :toRcvDate AND "
			+ "doc.dataOriginTypeCode IN ('A','AI') "
			+ "AND doc.isDeleted = false")
	public List<String> findGstinByRecevedDateBatween(
			@Param("fromRcvDate") LocalDate fromRcvDate,
			@Param("toRcvDate") LocalDate toRcvDate);

	/*
	 * @Query("select i FROM InwardTransDocError i WHERE i.docHeaderId " +
	 * "IN (:ids) AND i.valType = :valType") public List<InwardTransDocError>
	 * findByDocHeaderIds(
	 * 
	 * @Param("ids") List<Long> ids,
	 * 
	 * @Param("valType") String valType);
	 */

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.updatedDate = "
			+ "CURRENT_TIMESTAMP WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids);

	@Query(value = "SELECT doc.ID, doc.CUST_GSTIN, doc.DOC_NUM, doc.DOC_TYPE, doc.DOC_DATE, "
			+ "doc.IS_PROCESSED, doc.GSTN_ERROR, 'TRUE' as InwdError, doc."
			+ "COMPANY_CODE, doc.FI_YEAR, doc.PURCHASE_VOUCHER_NUM, "
			+ "doc.PAYLOAD_ID, doc.RECEIVED_DATE, err.ITM_NO, '' AS errorField, "
			// + "IFNULL(doc.ERROR_CODES, '') || ',' || IFNULL(err.ERROR_CODES,
			// '') "
			+ "CASE WHEN doc.ERROR_CODES IS NOT NULL THEN doc.ERROR_CODES ELSE err.ERROR_CODES END "
			+ "AS ERROR_CODE, '' AS errorDesc, '' AS type "
			+ "FROM ANX_INWARD_DOC_HEADER doc LEFT OUTER JOIN ANX_INWARD_DOC_ITEM "
			+ "err ON doc.ID = err.DOC_HEADER_ID WHERE doc.IS_ERROR=TRUE AND "
			+ "doc.DATAORIGINTYPECODE = 'A' AND doc.CUST_GSTIN = :gstin AND "
			+ "doc.ID between :minId AND :maxId", nativeQuery = true)
	public List<Object[]> aspErrorDocsForRevIntegrationByGstin(
			@Param("gstin") String gstin, @Param("minId") Long minId,
			@Param("maxId") Long maxId);

	// doc.isDeleted = false AND removed to send all Saved data to ERP.
	@Query("SELECT doc.id, doc.cgstin, doc.docNo, doc.docType, doc.docDate, "
			+ "doc.isProcessed, doc.isGstnError, 'TRUE' as InwdError"
			+ ",doc.companyCode, doc.finYear, doc.purchaseVoucherNum, "
			+ "doc.payloadId, doc.receivedDate, err."
			+ "itemNum, err.errorField, err.errorCode, err.errorDesc, err.valType "
			+ "FROM InwardTransDocument doc INNER JOIN InwardTransDocError "
			+ "err ON doc.id = err.docHeaderId WHERE doc.isGstnError=true AND "
			+ "doc.dataOriginTypeCode = 'A' "
			+ "AND doc.cgstin = :gstin AND doc.gstnBatchId =:batchId")
	public List<Object[]> gstnErrorDocsForRevIntegrationByGstin(
			@Param("gstin") String gstin, @Param("batchId") Long batchId);

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET doc.isGstnError = true,doc.gstr6SavedReturnPeriod  = :gstr6SavedReturnPeriod,"
			+ "doc.updatedDate = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP WHERE "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.isDeleted = false")
	public void markDocsAsErrorForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("now") LocalDateTime now,
			@Param("gstr6SavedReturnPeriod") String gstr6SavedReturnPeriod);

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET doc.isSaved  = true,"
			+ "doc.gstr6SavedReturnPeriod  = :gstr6SavedReturnPeriod,"
			+ "doc.updatedDate = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP "
			+ "WHERE doc.gstnBatchId = :gstnBatchId AND doc.isDeleted = false")
	public void markDocsAsSavedForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("now") LocalDateTime now,
			@Param("gstr6SavedReturnPeriod") String gstr6SavedReturnPeriod);

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.updatedDate = "
			+ "CURRENT_TIMESTAMP WHERE doc.taxperiod = :retPeriod AND doc.cgstin = :cgstin "
			+ "AND doc.gstnBifurcationNew = :section AND "
			+ "doc.isDeleted = false AND doc.id <= :hMaxId")
	void updateSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("cgstin") String cgstin, @Param("section") String section,
			@Param("hMaxId") Long hMaxId);

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET "
			+ "doc.isSaved=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET "
			+ "doc.isGstnError=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);

	@Query("SELECT doc.id FROM InwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false and  "
			+ "doc.isSubmitted = false")
	List<Long> findActiveDocsByDocKeys(@Param("docKeys") List<String> docKeys);
	

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET doc.isDeleted=true,"
			+ "doc.updatedDate=:updatedDate,doc.modifiedBy=:updatedBy "
			+ "WHERE doc.id IN (:ids) and doc.isSubmitted = false")
	public void updateDuplicateDocDeletionByDocKeys(
			@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy);

	@Query("SELECT doc.docKey FROM InwardTransDocument doc "
			+ "WHERE doc.id = :oldId AND doc.isDeleted= false")
	String findDocKeyById(@Param("oldId") Long oldId);

	@Query("SELECT doc.id FROM InwardTransDocument doc "
			+ "WHERE doc.docKey= :docKey AND doc.isDeleted= true AND "
			+ "doc.isProcessed = true ORDER BY ID DESC ")
	List<Object> findIdByDocKey(@Param("docKey") String docKey);

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET "
			+ "doc.isDeleted = false WHERE doc.id = :newId")
	void updateOldProcessedInv(@Param("newId") Long newId);

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET "
			+ "doc.isDeleted = true WHERE doc.id = :oldId")
	void updateNewErrorInv(@Param("oldId") Long oldId);

	@Query("SELECT count(doc) FROM InwardTransDocument doc "
			+ "WHERE doc.cgstin=:gstin AND doc.docDate BETWEEN "
			+ ":docRecvFrom AND :docRecvTo")
	public List<Object> findByGstinDocRecvFromTo(@Param("gstin") String gstin,
			@Param("docRecvFrom") LocalDate docRecvFrom,
			@Param("docRecvTo") LocalDate docRecvTo);

	@Query("SELECT count(doc) FROM InwardTransDocument doc "
			+ "WHERE doc.cgstin=:gstin AND doc.taxperiod BETWEEN "
			+ ":taxPeriodFrom AND :taxPeriodTo")
	public List<Object> findByGstinTaxPeriodFromTo(@Param("gstin") String gstin,
			@Param("taxPeriodFrom") String taxPeriodFrom,
			@Param("taxPeriodTo") String taxPeriodTo);

	@Query("SELECT doc.id, doc.docKey FROM InwardTransDocument doc WHERE "
			+ "doc.gstnBatchId = :gstnBatchId and doc.isDeleted = false")
	public List<Object[]> findDocIdsByBatchId(@Param("gstnBatchId") Long id);

	@Query("SELECT doc.id, doc.docKey FROM InwardTransDocument doc WHERE "
			+ "doc.docKey IN (:invKeyList) AND doc.gstnBatchId = :gstnBatchId "
			+ "and doc.isDeleted = false")
	public List<Object[]> findDocIdsForBatchByDocKeys(
			@Param("gstnBatchId") Long id,
			@Param("invKeyList") List<String> invKeyList);

	/*
	 * @Modifying
	 * 
	 * @Query("UPDATE InwardTransDocument doc SET doc.isGstnError = true," +
	 * "doc.updatedDate = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP," +
	 * "doc.isSaved  = false WHERE " +
	 * "doc.docKey IN (:invKeyList) AND doc.gstnBatchId = :gstnBatchId " +
	 * "AND doc.isDeleted = false") public void
	 * markDocsAsErrorsForBatch(@Param("gstnBatchId") Long gstnBatchId,
	 * 
	 * @Param("invKeyList") List<String> invKeyList,
	 * 
	 * @Param("now") LocalDateTime now);
	 */

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET doc.isSaved = true"
			+ ",doc.gstr6SavedReturnPeriod  = :gstr6SavedReturnPeriod,"
			+ "doc.updatedDate = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP, "
			+ "doc.isGstnError = false WHERE "
			+ "doc.docKey NOT IN (:erroredDocKeyList) AND "
			+ "doc.gstnBatchId = :gstnBatchId ")
	public void markDocsAsSavedForBatchByErroredDocKeys(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("erroredDocKeyList") List<String> erroredDocKeyList,
			@Param("now") LocalDateTime now,@Param("gstr6SavedReturnPeriod") String gstr6SavedReturnPeriod);

	@Query("SELECT doc.docKey,doc.docDate FROM InwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false and  "
			+ "doc.isSubmitted = false")
	List<Object[]> findActiveOrgDocsByDocKeys(
			@Param("docKeys") List<String> docKeys);

	@Query("SELECT isSubmitted FROM OutwardTransDocument doc "
			+ "WHERE doc.docKey = :docKey AND doc.isDeleted = false ")
	public List<Boolean> findDocsCountByDocKey(@Param("docKey") String docKey);

	@Query("SELECT MAX(doc.id) FROM InwardTransDocument doc WHERE doc.cgstin "
			+ "= :cgstin AND doc.isDeleted = false")
	public Long findMaxIdByCgstinAndIsDeletedFalse(
			@Param("cgstin") String cgstin);

	@Query("SELECT doc.docKey,isSubmitted FROM InwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.supplyType <> 'CAN' AND "
			+ "doc.isProcessed = true AND doc.isDeleted = false ")
	public List<Object[]> findCancelDocsCountsByDocKeys(
			@Param("docKeys") List<String> docKeys);
	
	@Query("SELECT doc.docKey,doc.gstr6SavedReturnPeriod FROM InwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.supplyType <> 'CAN' AND "
			+ "doc.isProcessed = true AND doc.isDeleted = false ")
	public List<Object[]> findActiveOrgDocs(
			@Param("docKeys") List<String> docKeys);

	@Query("SELECT doc.docKey FROM InwardTransDocument doc WHERE "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.cgstin =:cgstin")
	public List<String> findDocKeysByBatchIdAndCtin(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("cgstin") String cgstin);

	@Query("SELECT doc FROM InwardTransDocument doc "
			+ "WHERE doc.docKey IN (:invKeySetAsList) AND "
			+ " doc.gstnBatchId = :gstnBatchId ")
	public List<InwardTransDocument> findByGstnBatchIdAndDocKeyIn(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("invKeySetAsList") List<String> invKeySetAsList);

	@Query("SELECT doc.docKey FROM InwardTransDocument doc "
			+ " WHERE doc.gstnBatchId = :gstnBatchId AND "
			+ " doc.gstnBifurcation = :gstnBifurcation AND doc.docNo = :docNo "
			+ " AND doc.isDeleted = false " + " AND doc.cgstin =:cgstin "
			+ " AND doc.taxperiod =:taxperiod ")
	public String findDocKeyByBatchID(@Param("gstnBatchId") Long gstnBatchId,
			@Param("gstnBifurcation") String gstnBifurcation,
			@Param("docNo") String docNo, @Param("cgstin") String cgstin,
			@Param("taxperiod") String taxperiod);

	@Query(value = "SELECT doc.ID,doc.CUST_GSTIN,doc.DOC_NUM,doc.DOC_TYPE,doc.DOC_DATE, "
			+ "doc.IS_PROCESSED,doc.GSTN_ERROR,'FALSE' as InwdError,doc.COMPANY_CODE, doc.FI_YEAR, "
			+ "doc.PAYLOAD_ID,doc.RECEIVED_DATE, "
			+ " doc.PURCHASE_VOUCHER_NUM, '' AS errorField, "
			+ "IFNULL(doc.ERROR_CODES, '') || ',' "
			+ "AS ERROR_CODE, '' AS errorDesc, '' AS type,doc.PURCHASE_VOUCHER_DATE "
			+ "FROM ANX_INWARD_DOC_HEADER doc " + "WHERE "
			+ "doc.DATAORIGINTYPECODE = 'A' AND doc.PAYLOAD_ID=:payloadId ", nativeQuery = true)
	public List<Object[]> aspErrorDocsForRevIntegrationByPayloadId(
			@Param("payloadId") String payloadId);

	@Query(value = "SELECT ITM_NO,IFNULL(ERROR_CODES, '') || ',' "
			+ "FROM ANX_INWARD_DOC_ITEM "
			+ "WHERE DOC_HEADER_ID=:docHeaderId", nativeQuery = true)
	public List<Object[]> aspErrorInwardDocItemForDocHeaderId(
			@Param("docHeaderId") Long docHeaderId);

	@Query("SELECT doc FROM InwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false and  "
			+ "doc.isSubmitted = false AND doc.isProcessed = true")
	List<InwardTransDocument> findActiveOrgDocumentByDocKeys(
			@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET doc.isSubmitted=true,"
			+ "doc.submittedDate = :modifiedDate,doc.updatedDate=:modifiedOn "
			+ "WHERE doc.taxperiod =:taxperiod AND "
			+ "doc.sgstin =:sgstin AND doc.isDeleted = false")
	public void markSubmittedAsTrue(@Param("taxperiod") String taxperiod,
			@Param("sgstin") String sgstin,
			@Param("modifiedDate") LocalDate modifiedDate,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query(value = "SELECT ITM_NO,IFNULL(ERROR_CODES, '') || ',' as ERROR_CODES "
			+ "FROM ANX_INWARD_DOC_ITEM "
			+ "WHERE DOC_HEADER_ID=:docHeaderId", nativeQuery = true)
	public List<Object[]> aspErrorDocItemForDocHeaderIdFromInward(
			@Param("docHeaderId") Long docHeaderId);

	@Modifying
	@Query("UPDATE InwardTransDocument doc SET doc.isSent = false, doc.isSaved "
			+ "= false, doc.isGstnError = false, doc.gstnBatchId =null, doc."
			+ "sentToGSTNDate = null, doc.savedToGSTNDate = null, doc."
			+ "gstnErrorCode = null, doc.gstnErrorDesc = null WHERE doc.cgstin "
			+ "= :cgstin AND doc.taxperiod = :retPeriod AND doc.gstnBifurcation "
			+ "IN (:sections) AND doc.isDeleted = false AND doc.isSent = true")
	public void resetSaveGstr6AuditColumns(@Param("cgstin") String cgstin,
			@Param("retPeriod") String retPeriod,
			@Param("sections") List<String> sections);

	@Query(value = "SELECT HDR.ID,COUNT(ITM.DOC_HEADER_ID) "
			+ "FROM ANX_INWARD_DOC_HEADER HDR,ANX_INWARD_DOC_ITEM ITM "
			+ "WHERE HDR.ID = ITM.DOC_HEADER_ID "
			+ "AND HDR.ID IN (:docIds) GROUP BY HDR.ID ", nativeQuery = true)
	public List<Object[]> findDocsByIds(@Param("docIds") List<Long> docIds);

	@Query("SELECT doc FROM InwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false and "
			+ "doc.isProcessed = true")
	List<InwardTransDocument> findIsdRecordsByDocKeys(
			@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE InwardTransDocument set isDeleted = true, modifiedBy = :modifiedBy,"
			+ "updatedDate= :updatedDate,inactiveReason = :inactiveReason where "
			+ "acceptanceId=:acceptanceId AND isDeleted = false")
	public Integer deleteFile(@Param("acceptanceId") Long fileId,
			@Param("modifiedBy") String modifiedBy,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("inactiveReason") String inactiveReason);

	@Modifying
	@Query("UPDATE InwardTransDocument SET isDeleted=true,modifiedBy = :modifiedBy,"
			+ "updatedDate= :updatedDate,inactiveReason = :inactiveReason "
			+ "WHERE id IN (:ids) and isDeleted=false")
	int updateInwardDocDeletion(@Param("ids") List<Long> ids,
			@Param("modifiedBy") String updatedBy,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("inactiveReason") String inactiveReason);

	@Query("SELECT COUNT(*) FROM InwardTransDocument  WHERE isProcessed = true "
			+ "AND isDeleted = false and cgstin=:cgstin and taxperiod=:taxperiod "
			+ "and supplyType <> 'CAN' and docType = 'SLF'")
	public int isInvDataAvaCount(@Param("cgstin") String cgstin,
			@Param("taxperiod") String taxperiod);

	@Query("SELECT doc FROM InwardTransDocument doc"
			+ " WHERE id IN (:ids) and isDeleted=false")
	List<InwardTransDocument> getInwardDocDetails(@Param("ids") List<Long> ids);

	@Query("SELECT concat(doc.taxperiod,doc.docKey) FROM InwardTransDocument doc"
			+ " WHERE doc.docKey IN (:docs) and doc.isDeleted=false and supplyType <> 'CAN' and isProcessed = true")
	public List<String> findByDocKeyInAndIsDeletedFalseWithTaxPeriod(
			@Param("docs") List<String> docs);

	@Query("SELECT  doc.docKey FROM InwardTransDocument doc"
			+ " WHERE doc.docKey IN (:docs) and doc.isDeleted=false")
	public List<String> findByDocKeyInAndIsDeletedFalse(
			@Param("docs") List<String> docs);

	public List<InwardTransDocument> findByAcceptanceIdAndIsDeletedFalse(
			Long fileId);

}
