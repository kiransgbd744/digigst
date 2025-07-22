package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocSearchForSaveCustomRepository;
import com.ey.advisory.app.data.repositories.client.ProcedureCallRepository;
import com.ey.advisory.common.EinvoiceDefaultOutwardDocSearchPredicateBuilder;
import com.ey.advisory.common.GstnOutwardTransDocSearchPredicateBuilder;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.dto.DocSearchReqDto;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;
import com.ey.advisory.core.dto.GstnDocSearchReqDto;

/**
 * This class is responsible for repository operations of Document Header Entity
 * 
 * @author Mohana.Dasari
 *
 */

@Repository("DocRepositoryGstr1A")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface DocRepositoryGstr1A
		extends JpaRepository<Gstr1AOutwardTransDocument, Long>,
		JpaSpecificationExecutor<Gstr1AOutwardTransDocument>,
		DocSearchForSaveCustomRepository, ProcedureCallRepository {

	public List<Gstr1AOutwardTransDocument> findByDocNoAndDocDateAndDocTypeAndSgstin(
			String docNo, LocalDate docDate, String docType, String sgstin);

	public List<Gstr1AOutwardTransDocument> findByDocNoAndDocDateAndCgstinAndDocTypeAndSgstin(
			String docNo, LocalDate docDate, String cgstin, String docType,
			String sgstin);

	public List<Gstr1AOutwardTransDocument> findByDocNoAndDocDateAndDocTypeAndSgstinAndIsProcessedAndIsDeleted(
			String docNo, LocalDate docDate, String docType, String sgstin,
			boolean isProcessed, boolean isDeleted);

	public default Page<Gstr1AOutwardTransDocument> findDocsBySearchCriteria(
			DocSearchReqDto searchParams, Pageable pageRequest) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			Gstr1AOutwardTransDocSearchPredicateBuilder builder = StaticContextHolder
					.getBean("Gstr1ADefaultOutwardTransDocSearchPredicateBuilder",
							Gstr1AOutwardTransDocSearchPredicateBuilder.class);

			List<Predicate> predicates = builder.build(searchParams, root,
					criteriaBuilder);

			// Include the Sort Criteria, so that pagination will return
			// consistent results.
			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("createdDate")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		}, pageRequest);
	};

	public default Page<Gstr1AOutwardTransDocument> findDocsByEinvoiceSearchCriteria(
			EInvoiceDocSearchReqDto searchParams, Pageable pageRequest) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			Gstr1AEinvoiceDefaultOutwardDocSearchPredicateBuilder builder = StaticContextHolder
					.getBean("Gstr1AEinvoiceDefaultOutwardDocSearchPredicateBuilder",
							Gstr1AEinvoiceDefaultOutwardDocSearchPredicateBuilder.class);

			List<Predicate> predicates = builder.build(searchParams, root,
					criteriaBuilder);

			// Include the Sort Criteria, so that pagination will return
			// consistent results.
			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("createdDate")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		}, pageRequest);
	};

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isDeleted=true,"
			+ "doc.updatedDate=:updatedDate,doc.modifiedBy=:updatedBy "
			+ "WHERE doc.id IN (:ids)")
	void updateDocDeletion(@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.updatedDate = "
			+ ":modifiedOn WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.gstnSaveRefId=:gstnSaveRefId,"
			+ "doc.updatedDate = :modifiedOn WHERE doc.gstnBatchId=:gstnBatchId")
	void updateGstnSaveRefId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("gstnSaveRefId") String gstnSaveRefId,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("SELECT doc.sgstin from Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.gstnBatchId = :id ")
	String getBatchId(@Param("id") Long id);

	// AND doc.isDeleted = false removed from query
	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isGstnError = true,"
			+ "doc.updatedDate =:modifiedOn,doc.savedToGSTNDate = CURRENT_TIMESTAMP,"
			+ "doc.isSaved  = false WHERE doc.gstnBatchId = :gstnBatchId")
	public void markDocsAsErrorForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	// and doc.isDeleted = false is removed from below Query because this is
	// already been selected and sent to Gstn So even if it is deleted update
	// should happen.
	/*
	 * @Modifying
	 * 
	 * @Query("UPDATE OutwardTransDocument doc SET doc.isGstnError = true," +
	 * "doc.updatedDate =:modifiedOn,doc.savedToGSTNDate = CURRENT_TIMESTAMP," +
	 * "doc.isSaved  = false WHERE doc.docKey IN (:invKeyList) AND " +
	 * "doc.gstnBatchId = :gstnBatchId") public void
	 * markDocsAsErrorsForBatch(@Param("gstnBatchId") Long gstnBatchId,
	 * 
	 * @Param("invKeyList") List<String> invKeyList,
	 * 
	 * @Param("modifiedOn") LocalDateTime modifiedOn);
	 */

	/**
	 * This method will mark all the documents for a batch as saved, by updating
	 * the isSAved flag to true.
	 * 
	 * @param id
	 */
	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isSaved  = true,"
			+ "doc.updatedDate =:modifiedOn,doc.savedToGSTNDate = CURRENT_TIMESTAMP, "
			+ "doc.isGstnError = false WHERE doc.gstnBatchId = :gstnBatchId")
	public void markDocsAsSavedForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	/**
	 * This method takes the list of invoice keys for which GSTN returned errors
	 * and marks all the other invoices in the specified batch as succeeded, by
	 * updating the 'isSavedToGstn' flag as true.
	 * 
	 * @param id
	 * @param erroredDocKeyList
	 */
	// and doc.isDeleted = false is removed from below Query because this is
	// already been selected and sent to Gstn So even if it is deleted update
	// should happen.
	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isSaved = true,"
			+ "doc.updatedDate = :modifiedOn,doc.savedToGSTNDate = CURRENT_TIMESTAMP, "
			+ "doc.isGstnError = false WHERE doc.docKey NOT IN ("
			+ ":erroredDocKeyList) AND " + "doc.gstnBatchId = :gstnBatchId")
	public void markDocsAsSavedForBatchByErroredDocKeys(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("erroredDocKeyList") List<String> erroredDocKeyList,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	/**
	 * This method accepts a list of doc keys and returns an object array where
	 * the first element of the array is the document header id and the second
	 * element is the invoice key. We are selecting the invoice key back
	 * because, since we're not doing an ORDER BY, the invoices returned may not
	 * be in the order in which we passed the invoice keys. Hence, we need both
	 * the invoice key and the document header id as return.
	 * 
	 * @param id
	 * @param invKeyList
	 * @return
	 */
	// and doc.isDeleted = false is removed from below Query because this is
	// already been selected and sent to Gstn So even if it is deleted update
	// should happen.
	/*
	 * @Query("SELECT doc.docKey,doc.id,doc.sgstin,doc.taxperiod,doc." +
	 * "derivedTaxperiod,doc.acceptanceId FROM OutwardTransDocument doc WHERE "
	 * + "doc.docKey IN (:invKeyList) AND doc.gstnBatchId = :gstnBatchId")
	 * public List<Object[]> findDocIdsForBatchByDocKeys(
	 * 
	 * @Param("gstnBatchId") Long id,
	 * 
	 * @Param("invKeyList") List<String> invKeyList);
	 */

	public List<Gstr1AOutwardTransDocument> findByGstnBatchIdAndDocKeyIn(
			Long gstnBatchId, List<String> docKey);

	// and doc.isDeleted = false is removed from below Query because this is
	// already been selected and sent to Gstn So even if it is deleted update
	// should happen.
	@Query("SELECT doc.docKey FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.cgstin =:cgstin")
	public List<String> findDocKeysByBatchIdAndCtin(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("cgstin") String cgstin);

	/*
	 * @Query("SELECT COUNT(doc.sgstin) from OutwardTransDocument doc where " +
	 * "doc.sgstin=:sgstin " + "and doc.origDocDate=:origDocDate " +
	 * "and doc.origDocNo=:origDocNo AND doc.isDeleted = false") public int
	 * CountOriginalDocdateAndNo(@Param("sgstin") String sgstin,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate,
	 * 
	 * @Param("origDocNo") String origDocNo);
	 */
	/*
	 * @Query("SELECT doc from OutwardTransDocument doc where doc.sgstin=:sgstin "
	 * + "and doc.origDocDate=:origDocDate " +
	 * "and doc.origDocNo=:origDocNo AND doc.isDeleted = false") public
	 * List<OutwardTransDocument> findOriginalDocdateAndNo(
	 * 
	 * @Param("sgstin") String sgstin,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate,
	 * 
	 * @Param("origDocNo") String origDocNo);
	 */
	/*
	 * @Query("SELECT COUNT(doc) from OutwardTransDocument doc " +
	 * "where doc.sgstin=:sgstin " + "and doc.origDocNo=:origDocNo " +
	 * "and doc.origDocDate=:origDocDate AND doc.isDeleted = false")
	 * 
	 * public int findInvoices(@Param("sgstin") String sgstin,
	 * 
	 * @Param("origDocNo") String origDocNo,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate);
	 */
	@Query("SELECT count(g) from Gstr1AOutwardTransDocument g "
			+ "where g.isDeleted = false and g.isSubmitted = true "
			+ "and g.docKey = :docKey ")
	public int findSubmitTag(@Param("docKey") String docKey);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument g SET g.isDeleted = true "
			+ "WHERE g.docKey = :docKey and g.isSubmitted = false")
	public void findDetailsOfDoc(@Param("docKey") String docKey);

	@Query("SELECT count(doc) from Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docDate=:docDate " + "and doc.sgstin=:sgstin "
			+ "and doc.docNo=:docNo " + "and doc.docType IN('INV') "
			+ "and doc.isSubmitted = true AND doc.isDeleted = false")

	public int findduplicates(@Param("docDate") LocalDate docDate,
			@Param("sgstin") String sgstin, @Param("docNo") String docNo);

	@Query("SELECT count(doc) from OutwardTransDocument doc "
			+ "WHERE doc.docDate=:docDate " + "and doc.sgstin=:sgstin "
			+ "and doc.docNo=:docNo " + "and doc.docType IN('DR') "
			+ "and doc.isSubmitted = true AND doc.isDeleted = false")
	public int findDRduplicates(@Param("docDate") LocalDate docDate,
			@Param("sgstin") String sgstin, @Param("docNo") String docNo);

	@Query("SELECT count(doc) from Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docDate=:docDate " + "and doc.sgstin=:sgstin "
			+ "and doc.docNo=:docNo " + "and doc.docType IN('CR') "
			+ "and doc.isSubmitted = true AND doc.isDeleted = false")
	public int findCRduplicates(@Param("docDate") LocalDate docDate,
			@Param("sgstin") String sgstin, @Param("docNo") String docNo);

	/*
	 * @Query("SELECT count(doc) from Gstr1AOutwardTransDocument doc where " +
	 * " doc.origDocNo=:origDocNo " +
	 * " and doc.origDocDate=:origDocDate AND doc.isDeleted = false") int
	 * findOriginalDoc(@Param("origDocNo") String origDocNo,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate);
	 */
	@Query("SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,"
			+ "doc.docAmount,doc.pos,doc.reverseCharge,doc.egstin,"
			+ "doc.diffPercent,rate.taxRate,rate.taxValue,"
			+ "rate.igstAmt,rate.cgstAmt,rate.sgstAmt,rate.cessAmt,doc.id,"
			+ "doc.supplyType,doc.taxPayable,doc.docDate FROM "
			+ "Gstr1AOutwardTransDocument doc JOIN DocRateSummary rate "
			+ "ON doc.id=rate.docHeaderId AND doc.gstnBatchId IN (SELECT b.id "
			+ "FROM Gstr1SaveBatchEntity b WHERE b.gstnStatus IN "
			+ "('P','PE')) AND doc.isError = false ORDER BY doc.gstnBatchId")
	List<Object[]> getTaxDocsForRevIntegration();

	/*
	 * @Query("SELECT count(doc) from Gstr1AOutwardTransDocument doc " +
	 * "WHERE doc.docType IN('RNV','INV') " + "and doc.sgstin=:sgstin " +
	 * "and doc.docNo=:origDocNo " +
	 * "and doc.docDate=:origDocDate AND doc.isDeleted = false")
	 * 
	 * public int findDuplicateKeyDocnoAndDocdate(@Param("sgstin") String
	 * sgstin,
	 * 
	 * @Param("origDocNo") String origDocNo,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate);
	 */
	/*
	 * @Query("SELECT count(doc) from Gstr1AOutwardTransDocument doc " +
	 * "WHERE doc.docType IN('CR') " + "and doc.sgstin=:sgstin " +
	 * "and doc.docNo=:origDocNo " +
	 * "and doc.docDate=:origDocDate AND doc.isDeleted = false")
	 * 
	 * public int findDuplicateKeyDocnoAndDocdateRCR(
	 * 
	 * @Param("sgstin") String sgstin,
	 * 
	 * @Param("origDocNo") String origDocNo,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate);
	 */
	/*
	 * @Query("SELECT count(doc) from Gstr1AOutwardTransDocument doc " +
	 * "WHERE doc.docType IN('DR') " + "and doc.sgstin=:sgstin " +
	 * "and doc.docNo=:origDocNo " +
	 * "and doc.docDate=:origDocDate AND doc.isDeleted = false")
	 * 
	 * public int findDuplicateKeyDocnoAndDocdateRDR(
	 * 
	 * @Param("sgstin") String sgstin,
	 * 
	 * @Param("origDocNo") String origDocNo,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate);
	 */
	/*
	 * @Query("SELECT count(doc) from Gstr1AOutwardTransDocument doc " +
	 * "WHERE doc.docType IN('INV') " + "and doc.sgstin=:sgstin " +
	 * "and doc.docNo=:origDocNo " +
	 * "and doc.docDate=:origDocDate AND doc.isDeleted = false") public int
	 * findDuplicateKeyDocnoAndDocdateandINV(
	 * 
	 * @Param("sgstin") String sgstin,
	 * 
	 * @Param("origDocNo") String origDocNo,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate);
	 */
	/*
	 * @Query("SELECT count(doc) from Gstr1AOutwardTransDocument doc " +
	 * "WHERE doc.docType IN('RNV','INV') " + "and doc.sgstin=:sgstin " +
	 * "and doc.docNo=:origDocNo " + "and doc.docDate=:origDocDate " +
	 * "and doc.cgstin=:cgstin AND doc.isDeleted = false") public int
	 * findDuplicateKeyDocnoAndDocdateCgstin(
	 * 
	 * @Param("sgstin") String sgstin,
	 * 
	 * @Param("origDocNo") String origDocNo,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate,
	 * 
	 * @Param("cgstin") String cgstin);
	 */
	/*
	 * @Query("SELECT count(doc) from Gstr1AOutwardTransDocument doc " +
	 * "WHERE doc.docType IN('RNV','INV') " + "and doc.sgstin=:sgstin " +
	 * "and doc.docNo=:origDocNo " + "and doc.docDate=:origDocDate and " +
	 * "doc.reverseCharge=:reverseCharge AND doc.isDeleted = false") public int
	 * findDuplicateKeyDocnoAndDocdateRcm(
	 * 
	 * @Param("sgstin") String sgstin,
	 * 
	 * @Param("origDocNo") String origDocNo,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate,
	 * 
	 * @Param("reverseCharge") String reverseCharge);
	 */
	@Query("SELECT count(outWardEntity) from Gstr1AOutwardTransDocument "
			+ "outWardEntity WHERE " + "outWardEntity.sgstin = :sgstin  AND "
			+ "outWardEntity.docDate = :docDate AND "
			+ "outWardEntity.docNo = :docNo AND "
			+ "outWardEntity.finYear = :finYear AND "
			+ "outWardEntity.isDeleted = false")
	public int findDuplicateCheck(@Param("sgstin") String sgstin,
			@Param("docDate") LocalDate docDate, @Param("docNo") String docNo,
			@Param("finYear") String finYear);

	@Query("SELECT count(outWardEntity) from Gstr1AOutwardTransDocument "
			+ "outWardEntity WHERE " + "outWardEntity.sgstin = :sgstin  AND "
			+ "outWardEntity.docDate = :docDate AND "
			+ "outWardEntity.docNo = :docNo AND "
			+ "outWardEntity.finYear = :finYear AND "
			+ "outWardEntity.isSubmitted = true AND "
			+ "outWardEntity.isDeleted = false")
	public int findDuplicateChecksubmit(@Param("sgstin") String sgstin,
			@Param("docDate") LocalDate docDate, @Param("docNo") String docNo,
			@Param("finYear") String finYear);

	/*
	 * @Query("SELECT doc from Gstr1AOutwardTransDocument doc " +
	 * "WHERE doc.docType IN('RNV','INV') " + "and doc.sgstin=:sgstin " +
	 * "and doc.docNo=:origDocNo " +
	 * "and doc.docDate=:origDocDate AND doc.isDeleted = false") public
	 * List<Gstr1AOutwardTransDocument> getDuplicateKeyDocnoAndDocdate(
	 * 
	 * @Param("sgstin") String sgstin,
	 * 
	 * @Param("origDocNo") String origDocNo,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate);
	 */
	/*
	 * @Query("SELECT doc from Gstr1AOutwardTransDocument doc " +
	 * "WHERE doc.docType IN('CR') " + "and doc.sgstin=:sgstin " +
	 * "and doc.docNo=:origDocNo " +
	 * "and doc.docDate=:origDocDate AND doc.isDeleted = false") public
	 * List<Gstr1AOutwardTransDocument> getDuplicateKeyDocnoAndDocdateCR(
	 * 
	 * @Param("sgstin") String sgstin,
	 * 
	 * @Param("origDocNo") String origDocNo,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate);
	 */
	/*
	 * @Query("SELECT doc from Gstr1AOutwardTransDocument doc " +
	 * "WHERE doc.docType IN('DR') " + "and doc.sgstin=:sgstin " +
	 * "and doc.docNo=:origDocNo " +
	 * "and doc.docDate=:origDocDate AND doc.isDeleted = false ") public
	 * List<Gstr1AOutwardTransDocument> getDuplicateKeyDocnoAndDocdateDR(
	 * 
	 * @Param("sgstin") String sgstin,
	 * 
	 * @Param("origDocNo") String origDocNo,
	 * 
	 * @Param("origDocDate") LocalDate origDocDate);
	 */
	@Query("SELECT doc FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey = :docKey AND doc.isDeleted = false ")
	public List<Gstr1AOutwardTransDocument> findDocsByDocKey(
			@Param("docKey") String docKey);

	@Query("SELECT doc FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.supplyType <> 'CAN' AND doc.aspInvoiceStatus=2")
	public List<Gstr1AOutwardTransDocument> findDocsByDocKeyIn(
			@Param("docKeys") List<String> docKeys);

	@Query("SELECT doc FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey = :docKey "
			+ "and doc.supplyType <> 'CAN' AND doc.isSent=true")
	public List<Gstr1AOutwardTransDocument> findOrgDocsByDocKey(
			@Param("docKey") String docKey);

	@Query(value = "Select top 1 * from ANX_OUTWARD_DOC_HEADER_1A where DOC_KEY =:docKey "
			+ "and SUPPLY_TYPE <> 'CAN' and "
			+ " IS_SENT_TO_GSTN = true order by ID desc", nativeQuery = true)
	public Optional<Gstr1AOutwardTransDocument> findLatestSavedDoc(
			@Param("docKey") String docKey);

	@Query("SELECT isSubmitted FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey = :docKey AND doc.isDeleted = false ")
	public List<Boolean> findDocsCountByDocKey(@Param("docKey") String docKey);

	@Query("SELECT doc.docKey,isSubmitted FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) "
			+ "and doc.supplyType <> 'CAN' AND doc.isDeleted = false "
			+ "AND doc.aspInvoiceStatus=2")
	public List<Object[]> findCancelDocsCountsByDocKeys(
			@Param("docKeys") List<String> docKeys);

	@Query("SELECT doc.docKey,COUNT(doc.id) FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) "
			+ "and doc.supplyType <> 'CAN' AND doc.isDeleted = true AND doc.isSent = true "
			+ "AND doc.aspInvoiceStatus=2 GROUP BY doc.docKey, doc.id ORDER BY doc.id DESC")
	public List<Object[]> findByOrgDocSaveStatus(
			@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("Update Gstr1AOutwardTransDocument doc SET doc.isDeleted = true "
			+ "WHERE doc.docType =:docType AND doc.sgstin=:sgstin "
			+ "and doc.docNo=:preceedingInvoiceNumber "
			+ "and doc.finYear=:finYear ")
	void updateDocsByDocKey(@Param("docType") String docType,
			@Param("sgstin") String sgstin, @Param("finYear") String finYear,
			@Param("preceedingInvoiceNumber") String preceedingInvoiceNumber);

	// Gstn Invoive Errors
	public default Page<Gstr1AOutwardTransDocument> findGstnDocsBySearchCriteria(
			GstnDocSearchReqDto searchParams, Pageable pageRequest) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			Gstr1AGstnOutwardTransDocSearchPredicateBuilder builder = StaticContextHolder
					.getBean("Gstr1AGstnDefaultOutwardTransDocSearchPredicateBuilder",
							Gstr1AGstnOutwardTransDocSearchPredicateBuilder.class);

			List<Predicate> predicates = builder.build(searchParams, root,
					criteriaBuilder);

			// Include the Sort Criteria, so that pagination will return
			// consistent results.
			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("createdDate")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		}, pageRequest);
	};

	@Query("SELECT doc.preceedingInvoiceNumber,doc.preceedingInvoiceDate "
			+ "FROM Gstr1AOutwardTransDocument doc WHERE doc.docKey = :invKey AND "
			+ "doc.isDeleted = false")
	public Object[] fetchOriginDocForRCrRDr(@Param("invKey") String invKey);

	@Query("SELECT doc.cgstin," + "COUNT(doc.id),SUM(doc.docAmount),"
			+ "SUM(doc.taxableValue),SUM(doc.cgstAmount),SUM(doc.sgstAmount),"
			+ "SUM(doc.igstAmount),SUM(doc.cessAmountSpecific) "
			+ "FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.taxperiod IN(:returnPeriods) "
			+ "AND doc.gstnBifurcation IN('B2B') "
			+ "AND doc.tableTypeNew IN('4A','4B','4C','6B','6C') "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDeleted = false GROUP BY doc.cgstin")
	public List<Object[]> getB2bData(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);

	@Query("SELECT doc.cgstin," + "COUNT(doc.id),SUM(doc.docAmount),"
			+ "SUM(doc.taxableValue),SUM(doc.cgstAmount),SUM(doc.sgstAmount),"
			+ "SUM(doc.igstAmount),SUM(doc.cessAmountSpecific) "
			+ "FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.taxperiod IN(:returnPeriods) "
			+ "AND doc.gstnBifurcation IN('B2BA') "
			+ "AND doc.tableTypeNew IN('4A','4B','4C','6B','6C') "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDeleted = false GROUP BY doc.cgstin")
	public List<Object[]> getB2baData(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);

	@Query("SELECT doc.cgstin," + "COUNT(doc.id),SUM(doc.docAmount),"
			+ "SUM(doc.taxableValue),SUM(doc.cgstAmount),SUM(doc.sgstAmount),"
			+ "SUM(doc.igstAmount),SUM(doc.cessAmountSpecific) "
			+ "FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.taxperiod IN(:returnPeriods) "
			+ "AND doc.gstnBifurcation IN('CR','DR','RFV','CDN') AND doc.tableTypeNew IN('9B') "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDeleted = false GROUP BY doc.cgstin")
	public List<Object[]> getCdnrData(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);

	@Query("SELECT doc.cgstin," + "COUNT(doc.id),SUM(doc.docAmount),"
			+ "SUM(doc.taxableValue),SUM(doc.cgstAmount),SUM(doc.sgstAmount),"
			+ "SUM(doc.igstAmount),SUM(doc.cessAmountSpecific) "
			+ "FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.taxperiod IN(:returnPeriods) "
			+ "AND doc.gstnBifurcation IN('RCR','RDR','RRFV','CDNA') AND doc.tableTypeNew IN('9B') "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDeleted = false GROUP BY doc.cgstin")
	public List<Object[]> getCdnraData(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);

	@Query("SELECT doc.pos," + "COUNT(doc.id),SUM(doc.docAmount),"
			+ "SUM(doc.taxableValue),"
			+ "SUM(doc.igstAmount),SUM(doc.cessAmountSpecific) "
			+ "FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.taxperiod IN(:returnPeriods) "
			+ "AND doc.gstnBifurcation IN('B2CL') AND doc.tableTypeNew IN('5A','5B') "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDeleted = false GROUP BY doc.pos")
	public List<Object[]> getB2clData(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);

	@Query("SELECT doc.pos," + "COUNT(doc.id),SUM(doc.docAmount),"
			+ "SUM(doc.taxableValue),"
			+ "SUM(doc.igstAmount),SUM(doc.cessAmountSpecific) "
			+ "FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.taxperiod IN(:returnPeriods) "
			+ "AND doc.gstnBifurcation IN('B2CL') AND doc.tableTypeNew IN('5A','5B') "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDeleted = false GROUP BY doc.pos")

	public List<Object[]> getB2claData(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);

	@Query("SELECT doc.pos," + "COUNT(doc.id),SUM(doc.docAmount),"
			+ "SUM(doc.taxableValue),"
			+ "SUM(doc.igstAmount),SUM(doc.cessAmountSpecific) "
			+ "FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.taxperiod IN(:returnPeriods) "
			+ "AND doc.gstnBifurcation IN('CR','DR','RFV','CDN') "
			+ "AND doc.tableTypeNew IN('9B') " + "AND doc.isSaved = true "
			+ "AND doc.isDeleted = false GROUP BY doc.pos")
	public List<Object[]> getCdnurData(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);

	@Query("SELECT doc.pos," + "COUNT(doc.id),SUM(doc.docAmount),"
			+ "SUM(doc.taxableValue),"
			+ "SUM(doc.igstAmount),SUM(doc.cessAmountSpecific) "
			+ "FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.taxperiod IN(:returnPeriods) "
			+ "AND doc.gstnBifurcation IN('CR','DR','RFV','CDN') "
			+ "AND doc.tableTypeNew IN('9B') " + "AND doc.isSaved = true "
			+ "AND doc.isDeleted = false GROUP BY doc.pos")
	public List<Object[]> getCdnuraData(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);

	@Query("SELECT " + "COUNT(doc.id),SUM(doc.docAmount),"
			+ "SUM(doc.taxableValue)," + "SUM(doc.igstAmount) "
			+ "FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.taxperiod IN(:returnPeriods) "
			+ "AND doc.gstnBifurcation IN('EXPA') "
			+ "AND doc.tableTypeNew IN('9A') " + "AND doc.isSaved = true "
			+ "AND doc.isDeleted = false")
	public List<Object[]> getExpa(@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);

	@Query("SELECT " + "COUNT(doc.id),SUM(doc.docAmount),"
			+ "SUM(doc.taxableValue)," + "SUM(doc.igstAmount) "
			+ "FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.taxperiod IN(:returnPeriods) "
			+ "AND doc.gstnBifurcation IN('EXP') "
			+ "AND doc.tableTypeNew IN('6A') " + "AND doc.isSaved = true "
			+ "AND doc.isDeleted = false")
	public List<Object[]> getExp(@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);

	@Query("SELECT COUNT(doc) FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.acceptanceId=:acceptanceId AND "
			+ "doc.isError=true AND doc.dataOriginTypeCode='E' AND doc.isDeleted = false")
	public Integer businessValidationCount(
			@Param("acceptanceId") final Long fileId);

	@Query(value = "SELECT doc.ID, doc.SUPPLIER_GSTIN, doc.DOC_NUM, doc.DOC_TYPE, doc.DOC_DATE, "
			+ "doc.IS_PROCESSED, doc.GSTN_ERROR, 'FALSE' as InwdError, doc."
			+ "COMPANY_CODE, doc.FI_YEAR, doc.ACCOUNTING_VOUCHER_NUM, "
			+ "doc.PAYLOAD_ID, doc.RECEIVED_DATE, err.ITM_NO, '' AS errorField, "
			// + "IFNULL(doc.ERROR_CODES, '') || ',' || IFNULL(err.ERROR_CODES,
			// '') "
			+ "CASE WHEN doc.ERROR_CODES IS NOT NULL THEN doc.ERROR_CODES ELSE err.ERROR_CODES END "
			+ "AS ERROR_CODE, '' AS errorDesc, '' AS type "
			+ "FROM ANX_OUTWARD_DOC_HEADER doc LEFT OUTER JOIN ANX_OUTWARD_DOC_ITEM "
			+ "err ON doc.ID = err.DOC_HEADER_ID WHERE doc.ASP_INVOICE_STATUS=1 AND "
			+ "doc.DATAORIGINTYPECODE = 'A' AND doc.SUPPLIER_GSTIN = :gstin AND "
			+ "doc.ID between :minId AND :maxId", nativeQuery = true)
	public List<Object[]> aspErrorDocsForRevIntegrationByGstin(
			@Param("gstin") String gstin, @Param("minId") Long minId,
			@Param("maxId") Long maxId);

	// doc.isDeleted = false AND removed to send all Saved data to ERP.
	@Query("SELECT doc.id, doc.sgstin, doc.docNo, doc.docType, doc.docDate, "
			+ "doc.isProcessed, doc.isGstnError, 'FALSE' as InwdError, doc."
			+ "companyCode, doc.finYear, doc.accountingVoucherNumber, "
			+ "doc.payloadId, doc.receivedDate, err."
			+ "itemNum, err.errorField, err.errorCode, err.errorDesc, err.type "
			+ "FROM Gstr1AOutwardTransDocument doc INNER JOIN OutwardTransDocError "
			+ "err ON doc.id = err.docHeaderId WHERE doc.isGstnError=true AND "
			+ "doc.dataOriginTypeCode = 'A' "
			+ "AND doc.sgstin = :gstin AND doc.gstnBatchId =:batchId")
	public List<Object[]> gstnErrorDocsForRevIntegrationByGstin(
			@Param("gstin") String gstin, @Param("batchId") Long batchId);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.erpBatchId  = :batchId,"
			+ "doc.updatedDate = CURRENT_TIMESTAMP WHERE doc.taxperiod = :retPeriod AND "
			+ "doc.sgstin = :sgstin  AND doc.erpBatchId IS NULL AND "
			+ "doc.isDeleted = false")
	public void updateDocsWithErpWorkflowBatchId(
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("batchId") Long batchId);

	@Query(value = "SELECT RETURN_PERIOD,SUPPLIER_GSTIN,'OUTWARD' AS DATA_TYPE,"
			+ "TAX_DOC_TYPE,AN_TABLE_SECTION AS TABLE_SECTION,DOC_TYPE,"
			+ "COUNT(ID) AS TOT_DOC,SUM(TAXABLE_VALUE)  AS TAXABLE_VALUE,"
			+ "SUM(IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT_SPECIFIC+CESS_AMT_ADVALOREM) AS TAX_PAYABLE,"
			+ "SUM(DOC_AMT) AS INV_VAL,SUM(IGST_AMT) AS IGST_AMT,SUM(CGST_AMT) AS CGST_AMT, "
			+ "SUM(SGST_AMT) AS SGST_AMT,SUM(CESS_AMT_SPECIFIC+CESS_AMT_ADVALOREM) AS CESS,"
			+ "COUNT(ID) AS MEMOTOT_DOC,SUM(TAXABLE_VALUE)  AS MEMOTAXABLE_VALUE,"
			+ "SUM(MEMO_VALUE_IGST+MEMO_VALUE_CGST+MEMO_VALUE_SGST) AS MEMOTAX_PAYABLE,"
			+ "SUM(DOC_AMT) AS MEMOINV_VAL,SUM(MEMO_VALUE_IGST) AS MEMOIGST,"
			+ "SUM(MEMO_VALUE_CGST) AS MEMOCGST,SUM(MEMO_VALUE_SGST) AS MEMOSGST,"
			+ "SUM(CESS_AMT_SPECIFIC+CESS_AMT_ADVALOREM) AS MEMOCESS "
			+ "FROM ANX_OUTWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
			+ "IS_DELETE = FALSE AND AN_TABLE_SECTION IN ('3A','3B','3C','3D',"
			+ "'3E','3F','3G') AND IS_SENT_TO_GSTN  = FALSE AND RETURN_PERIOD"
			+ " = :retPeriod AND SUPPLIER_GSTIN = :sgstin "
			+ "GROUP BY RETURN_PERIOD,SUPPLIER_GSTIN,TAX_DOC_TYPE,"
			+ "AN_TABLE_SECTION,DOC_TYPE", nativeQuery = true)
	public List<Object[]> approvalReqDocsForRevIntegration(
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin);

	@Query(value = "SELECT RETURN_PERIOD,SUPPLIER_GSTIN,'OUTWARD' AS DATA_TYPE,"
			+ "TAX_DOC_TYPE,AN_TABLE_SECTION AS TABLE_SECTION,DOC_TYPE,"
			+ "COUNT(ID) AS TOT_DOC,SUM(TAXABLE_VALUE)  AS TAXABLE_VALUE,"
			+ "SUM(IGST_AMT+CGST_AMT+SGST_AMT+CESS_AMT_SPECIFIC+CESS_AMT_ADVALOREM) AS TAX_PAYABLE,"
			+ "SUM(DOC_AMT) AS INV_VAL,SUM(IGST_AMT) AS IGST_AMT,SUM(CGST_AMT) AS CGST_AMT, "
			+ "SUM(SGST_AMT) AS SGST_AMT,SUM(CESS_AMT_SPECIFIC+CESS_AMT_ADVALOREM) AS CESS,"
			+ "COUNT(ID) AS MEMOTOT_DOC,SUM(TAXABLE_VALUE)  AS MEMOTAXABLE_VALUE,"
			+ "SUM(MEMO_VALUE_IGST+MEMO_VALUE_CGST+MEMO_VALUE_SGST) AS MEMOTAX_PAYABLE,"
			+ "SUM(DOC_AMT) AS MEMOINV_VAL,SUM(MEMO_VALUE_IGST) AS MEMOIGST,"
			+ "SUM(MEMO_VALUE_CGST) AS MEMOCGST,SUM(MEMO_VALUE_SGST) AS MEMOSGST,"
			+ "SUM(CESS_AMT_SPECIFIC+CESS_AMT_ADVALOREM) AS MEMOCESS "
			+ "FROM ANX_OUTWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
			+ "IS_DELETE = FALSE AND AN_TABLE_SECTION IN ('3A','3B','3C','3D',"
			+ "'3E','3F','3G') AND SUPPLIER_GSTIN = :sgstin "
			+ "GROUP BY RETURN_PERIOD,SUPPLIER_GSTIN,TAX_DOC_TYPE,"
			+ "AN_TABLE_SECTION,DOC_TYPE", nativeQuery = true)
	public List<Object[]> reviewSumryDocsForRevIntegration(
			@Param("sgstin") String sgstin);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.erpBatchId  = :batchId,"
			+ "doc.updatedDate = CURRENT_TIMESTAMP WHERE doc.receivedDate BETWEEN "
			+ ":fromRcvDate AND :toRcvDate AND doc.dataOriginTypeCode IN "
			+ "('A','AI') AND doc.erpBatchId IS NULL AND doc.isDeleted = false")
	public void updateDocsWithErpDataStatusBatchId(
			@Param("batchId") Long batchId,
			@Param("fromRcvDate") LocalDate fromRcvDate,
			@Param("toRcvDate") LocalDate toRcvDate);

	@Query("SELECT COUNT(*) FROM Gstr1AOutwardTransDocument "
			+ "WHERE sgstin=:gstin AND taxperiod = :taxperiod ")
	public int gstinCount(@Param("gstin") String gstin,
			@Param("taxperiod") String taxperiod);

	@Query("SELECT doc.sgstin FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.receivedDate BETWEEN :fromRcvDate AND :toRcvDate AND "
			+ "doc.dataOriginTypeCode IN ('A','AI') AND doc.erpBatchId IS NULL "
			+ "AND doc.isDeleted = false")
	public List<String> findGstinByRecevedDateBatween(
			@Param("fromRcvDate") LocalDate fromRcvDate,
			@Param("toRcvDate") LocalDate toRcvDate);

	/**
	 * @param docType
	 * @param sgstin
	 * @param origDocNo
	 * @param origDocDate
	 */
	@Query("SELECT doc from Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docType =:docType AND doc.sgstin=:sgstin "
			+ "and doc.docNo=:origDocNo "
			+ "and doc.finYear=:finYear AND doc.isDeleted = false")
	public List<Gstr1AOutwardTransDocument> findKeyByOrgDocnoAndOrgDocdate(
			@Param("docType") String docType, @Param("sgstin") String sgstin,
			@Param("finYear") String finYear,
			@Param("origDocNo") String origDocNo);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.irn  = :irn,"
			+ " doc.updatedDate = CURRENT_TIMESTAMP,doc.ackDate = :ackDate,doc.eInvStatus "
			+ "= :eInvStatus WHERE doc.id = :id")
	public void updateEinvoiceIrn(@Param("id") Long id,
			@Param("irn") String irn, @Param("eInvStatus") Integer eInvStatus,
			@Param("ackDate") LocalDateTime ackDate, @Param("id") Long einvId);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.eInvErrorDesc = "
			+ " :eInvErrorDesc, doc.eInvErrorCode = :eInvErrorCode,"
			+ " doc.irnStatus = :irnStatus, doc.aspInvoiceStatus = :aspInvStatus, "
			+ " doc.eInvStatus = :einvStatus " + " WHERE doc.id = :id")
	public void updateEinvoiceError(@Param("id") Long id,
			@Param("eInvErrorDesc") String eInvErrorDesc,
			@Param("eInvErrorCode") String eInvErrorCode,
			@Param("irnStatus") int irnStatus,
			@Param("aspInvStatus") int aspInvStatus,
			@Param("einvStatus") int einvStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.eInvErrorDesc = "
			+ ":eInvErrorDesc, doc.eInvErrorCode = :eInvErrorCode WHERE "
			+ "doc.id = :id")
	public void updateEinvoiceError(@Param("id") Long id,
			@Param("eInvErrorDesc") String eInvErrorDesc,
			@Param("eInvErrorCode") String eInvErrorCode);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.ewbNoresp  = :ewbNo,"
			+ " doc.updatedDate = CURRENT_TIMESTAMP,doc.ewbDateResp = :ewbDt ,"
			+ "doc.ewbStatus = :status, "
			+ "doc.ewbProcessingStatus = :ewbProcessingStatus"
			+ " WHERE doc.id = :id")
	public void updateEwayBillNo(@Param("id") Long id,
			@Param("ewbNo") Long ewbNo, @Param("ewbDt") LocalDateTime ewbDt,
			@Param("status") Integer status,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus);

	@Query("SELECT doc FROM Gstr1AOutwardTransDocument doc WHERE "
			+ "doc.id IN (:ids)")
	public List<Gstr1AOutwardTransDocument> fetchEnivStatusById(
			@Param("ids") List<Long> ids);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.eInvStatus=4 WHERE doc.id=:id")
	void updateEInvStatusFlag(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.ewbStatus=4 WHERE doc.id=:id")
	void updateEwbStatusFlag(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.irnStatus = :irnStatus, doc.eInvStatus = :einvStatus WHERE doc.id = :id")
	void updateEInvStatusByIrn(@Param("id") Long id,
			@Param("irnStatus") Integer status,
			@Param("einvStatus") int einvStatus);

	@Query("SELECT doc.docKey FROM  Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.id = :oldId AND doc.isDeleted= false")
	String findDocKeyById(@Param("oldId") Long oldId);

	@Query("SELECT doc.id FROM  Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey= :docKey AND doc.isDeleted= true AND "
			+ "doc.isProcessed = true ORDER BY ID DESC ")
	List<Object> findIdByDocKey(@Param("docKey") String docKey);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.isDeleted = false WHERE doc.id = :newId")
	void updateOldProcessedInv(@Param("newId") Long newId);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.isDeleted = true WHERE doc.id = :oldId")
	void updateNewErrorInv(@Param("oldId") Long oldId);

	@Query("SELECT doc.id FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false and  "
			+ "doc.isSubmitted = false")
	List<Long> findActiveDocsByDocKeys(@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isDeleted=true,"
			+ "doc.updatedDate=:updatedDate,doc.modifiedBy=:updatedBy "
			+ "WHERE doc.id IN (:ids) and doc.isSubmitted = false")
	public void updateDuplicateDocDeletionByDocKeys(
			@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,"
			+ "doc.updatedDate = :modifiedOn WHERE doc.taxperiod = :retPeriod "
			+ "AND doc.sgstin = :sgstin AND doc.gstnBifurcation = :section "
			+ "AND doc.isDeleted = false AND doc.id <= :hMaxId")
	void updateOldReturnsSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("section") String section,
			@Param("hMaxId") Long hMaxId,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,"
			+ "doc.updatedDate = :modifiedOn WHERE doc.taxperiod = :retPeriod "
			+ "AND doc.sgstin = :sgstin AND doc.gstnBifurcationNew = :section "
			+ "AND doc.isDeleted = false AND doc.id <= :hMaxId")
	void updateNewReturnsSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("section") String section,
			@Param("hMaxId") Long hMaxId,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.ewbErrorCode = :errorCode, "
			+ "doc.ewbErrorDesc = :errorDesc,doc.ewbStatus = :status, "
			+ "doc.ewbProcessingStatus = :ewbProcessingStatus, "
			+ " doc.aspInvoiceStatus = :aspInvStatus " + " WHERE doc.id = :id")
	public void updateErrorEwbResponseById(@Param("id") Long id,
			@Param("errorCode") String errorCode,
			@Param("errorDesc") String errorDesc,
			@Param("status") Integer status,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus,
			@Param("aspInvStatus") int aspInvStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.ewbStatus=:status,doc.ewbProcessingStatus=:ewbProcessingStatus"
			+ " WHERE doc.eWayBillNo=:eWayBillNo")
	void updaEwbStatusByEwbNumber(@Param("eWayBillNo") String eWayBillNo,
			@Param("status") Integer status,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.ewbStatus=:status,doc.ewbProcessingStatus=:ewbProcessingStatus,"
			+ "doc.isDeleted = :isDelete WHERE doc.id=:id")
	void updateEwbStatusByDocId(@Param("id") Long id,
			@Param("status") Integer status,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus,
			@Param("isDelete") boolean isDelete);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.ewbStatus=:status,doc.ewbProcessingStatus=:ewbProcessingStatus"
			+ " WHERE doc.id=:id")
	void updateEwbStatusByDocId(@Param("id") Long id,
			@Param("status") Integer status,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.ewbStatus=:status, doc.ewbErrorCode = :errorCode,"
			+ " doc.ewbErrorDesc = :errorMessage WHERE doc.eWayBillNo=:ewbNo")
	void updateCancelError(@Param("errorCode") String errorCode,
			@Param("errorMessage") String errorMessage,
			@Param("ewbNo") String ewbNo, @Param("status") Integer status);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.ewbStatus=:ewbStatus, doc.vehicleNo = :vehicleNo,"
			+ " doc.vehicleType = :vehicleType,doc.transportDocDate=:transDocDate,"
			+ "doc.transportDocNo=:transDocNo,doc.transportMode=:transMode,"
			+ "doc.ewbProcessingStatus=:ewbProcessingStatus where "
			+ "doc.id = :docHeaderId")
	public void updatePartBSuccessInfo(@Param("docHeaderId") Long docHeaderId,
			@Param("vehicleNo") String vehicleNo,
			@Param("vehicleType") String vehicleType,
			@Param("transDocDate") LocalDate transDocDate,
			@Param("transDocNo") String transDocNo,
			@Param("transMode") String transMode,
			@Param("ewbStatus") Integer ewbStatus,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.ewbProcessingStatus=:ewbProcessingStatus where "
			+ "doc.id = :docHeaderId")
	public void updateProcessingStatusByDocId(
			@Param("docHeaderId") Long docHeaderId,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.vehicleNo = :vehicleNo, "
			+ "doc.transportDocDate=:transDocDate,"
			+ "doc.transportDocNo=:transDocNo,doc.transportMode=:transMode,"
			+ "doc.distance=:remainingDistance,doc.transactionType=:transitType,"
			+ "doc.ewbProcessingStatus=:ewbProcessingStatus where "
			+ "doc.id = :docHeaderId")
	public void updateExtendInfo(@Param("docHeaderId") Long docHeaderId,
			@Param("remainingDistance") Integer remainingDistance,
			@Param("transDocDate") LocalDate transDocDate,
			@Param("transDocNo") String transDocNo,
			@Param("transitType") String transitType,
			@Param("transMode") String transMode,
			@Param("vehicleNo") String vehicleNo,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ " doc.transporterID = :transporterId, "
			+ "doc.ewbProcessingStatus=:ewbProcessingStatus where "
			+ "doc.id = :docHeaderId")
	public void updateTransporterInfo(@Param("docHeaderId") Long docHeaderId,
			@Param("transporterId") String transporterId,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.ewbProcessingStatus= :ewbProcessingStatus WHERE doc.id IN "
			+ "(:docIds)")
	public void updateEwbStatusByDocIdList(@Param("docIds") List<Long> docIds,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.ewbStatus= :ewbStatus WHERE doc.id IN " + "(:docIds)")
	public void updateMasterEwbStatusByDocIdList(
			@Param("docIds") List<Long> docIds,
			@Param("ewbStatus") Integer ewbStatus);

	@Query("SELECT doc.docKey,doc.docDate FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false and  "
			+ "doc.isSubmitted = false")
	List<Object[]> findActiveOrgDocsByDocKeys(
			@Param("docKeys") List<String> docKeys);

	@Query("SELECT doc.docKey,doc.docDate,doc.docAmount FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey = :docKey AND doc.isDeleted = false and  "
			+ "doc.isSubmitted = false")
	List<Object[]> findActiveOrgDocsByDocKeys(@Param("docKey") String docKey);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isSubmitted=true,"
			+ "doc.submittedDate = :modifiedDate,doc.updatedDate=:modifiedOn "
			+ "WHERE doc.taxperiod =:taxperiod AND "
			+ "doc.sgstin =:sgstin AND doc.isDeleted = false")
	public void markSubmittedAsTrue(@Param("taxperiod") String taxperiod,
			@Param("sgstin") String sgstin,
			@Param("modifiedDate") LocalDate modifiedDate,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isFiled=true,"
			+ "doc.filedDate = :modifiedDate,doc.updatedDate=:modifiedOn "
			+ "WHERE doc.taxperiod =:taxperiod AND "
			+ "doc.sgstin =:sgstin AND doc.isDeleted = false")
	public void markFilededAsTrue(@Param("taxperiod") String taxperiod,
			@Param("sgstin") String sgstin,
			@Param("modifiedDate") LocalDate modifiedDate,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("SELECT doc.docKey,doc.docDate,doc.ewbNoresp,doc.ewbDateResp,"
			+ "doc.ewbStatus FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false and  "
			+ "doc.isSubmitted = false and doc.ewbNoresp IS NOT NULL and "
			+ "doc.ewbDateResp IS NOT NULL ")
	List<Object[]> findActiveEwbDocsByDocKeys(
			@Param("docKeys") List<String> docKeys);

	@Query("SELECT MAX(doc.id) FROM Gstr1AOutwardTransDocument doc WHERE doc.sgstin "
			+ "= :sgstin AND doc.isDeleted = false")
	public Long findMaxIdBySgstinAndIsDeletedFalse(
			@Param("sgstin") String sgstin);

	@Query("SELECT doc FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false and  "
			+ "doc.isSubmitted = false AND doc.isProcessed = true")
	List<Gstr1AOutwardTransDocument> findActiveOrgDocumentByDocKeys(
			@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isSent = false, doc.isSaved "
			+ "= false, doc.isGstnError = false, doc.gstnBatchId =null, doc."
			+ "sentToGSTNDate = null, doc.savedToGSTNDate = null, doc."
			+ "gstnErrorCode = null, doc.gstnErrorDesc = null WHERE doc.sgstin "
			+ "= :sgstin AND doc.taxperiod = :retPeriod AND doc.gstnBifurcation "
			+ "IN (:sections) AND doc.isDeleted = false AND doc.isSent = true")
	public void resetSaveGstr1AuditColumns(@Param("sgstin") String sgstin,
			@Param("retPeriod") String retPeriod,
			@Param("sections") List<String> sections);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isSent = false, doc.isSaved "
			+ "= false, doc.isGstnError = false, doc.gstnBatchId =null, doc."
			+ "sentToGSTNDate = null, doc.savedToGSTNDate = null, doc."
			+ "gstnErrorCode = null, doc.gstnErrorDesc = null WHERE doc.sgstin "
			+ "= :sgstin AND doc.taxperiod = :retPeriod AND "
			+ "doc.gstnBifurcation = :gstnBifurcation AND doc.isDeleted = false AND doc.isSent = true "
			+ "AND doc.tableType IN ('15(i)','15(iii)')")
	public int resetSaveGstr1EcomTrans(@Param("sgstin") String sgstin,
			@Param("retPeriod") String retPeriod,
			@Param("gstnBifurcation") String gstnBifurcation);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isSent = false, doc.isSaved "
			+ "= false, doc.isGstnError = false, doc.gstnBatchId =null, doc."
			+ "sentToGSTNDate = null, doc.savedToGSTNDate = null, doc."
			+ "gstnErrorCode = null, doc.gstnErrorDesc = null WHERE doc.sgstin "
			+ "= :sgstin AND doc.taxperiod = :retPeriod AND "
			+ "doc.gstnBifurcation = :gstnBifurcation AND doc.isDeleted = false AND "
			+ "doc.isSent = true and doc.tableType IN ('15(ii)','15(iv)')")
	public int resetSaveGstr1EcomSupSum(@Param("sgstin") String sgstin,
			@Param("retPeriod") String retPeriod,
			@Param("gstnBifurcation") String gstnBifurcation);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isSent = false, doc.isSaved "
			+ "= false, doc.isGstnError = false, doc.gstnBatchId =null, doc."
			+ "sentToGSTNDate = null, doc.savedToGSTNDate = null, doc."
			+ "gstnErrorCode = null, doc.gstnErrorDesc = null WHERE doc.sgstin "
			+ "= :sgstin AND doc.taxperiod = :retPeriod AND "
			+ "doc.gstnBifurcation = :gstnBifurcation AND doc.isDeleted = false AND "
			+ "doc.isSent = true and doc.tableType IN ('4A','5A','7A(1)','7B(1)','9B','6B','6C') "
			+ "and doc.tcsFlag = 'Y' and IFNULL(doc.egstin,'')<>''")
	public void resetSaveGstr1SupEcom(@Param("sgstin") String sgstin,
			@Param("retPeriod") String retPeriod,
			@Param("gstnBifurcation") String gstnBifurcation);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isSent = false, doc.isSaved "
			+ "= false, doc.isGstnError = false, doc.gstnBatchId =null, doc."
			+ "sentToGSTNDate = null, doc.savedToGSTNDate = null, doc."
			+ "gstnErrorCode = null, doc.gstnErrorDesc = null WHERE doc.sgstin "
			+ "= :sgstin AND doc.taxperiod = :retPeriod AND doc.gstnBifurcation "
			+ "IN (:sections) AND (doc.einvGstnSaveStatus IS NULL OR doc.einvGstnSaveStatus='R') "
			+ "AND doc.isDeleted = false AND doc.isSent = true")
	public void resetSaveGstr1AuditColumnsForNonRespondedData(
			@Param("sgstin") String sgstin,
			@Param("retPeriod") String retPeriod,
			@Param("sections") List<String> sections);

	@Query(value = "SELECT doc.ID, doc.SUPPLIER_GSTIN, doc.DOC_NUM, doc.DOC_TYPE, doc.DOC_DATE, "
			+ "doc.IS_PROCESSED, doc.GSTN_ERROR, 'FALSE' as InwdError, doc."
			+ "COMPANY_CODE, doc.FI_YEAR, doc.ACCOUNTING_VOUCHER_NUM,doc.ACCOUNTING_VOUCHER_DATE "
			+ "doc.PAYLOAD_ID, doc.RECEIVED_DATE, "
			// + "CASE WHEN doc.ERROR_CODES IS NOT NULL THEN NULL ELSE
			// err.ITM_NO END "
			// +"CASE WHEN err.ERROR_CODES IS NOT NULL THEN err.ITM_NO ELSE NULL
			// END "
			+ "NULL " + "AS ITEM_NO, '' AS errorField, "
			// + "IFNULL(doc.ERROR_CODES, '') || ',' || IFNULL(err.ERROR_CODES,
			// '') "
			// + "CASE WHEN doc.ERROR_CODES IS NOT NULL THEN doc.ERROR_CODES
			// ELSE err.ERROR_CODES END "
			+ "IFNULL(doc.ERROR_CODES, '') || ',' "
			+ "AS ERROR_CODE, '' AS errorDesc, '' AS type "
			+ "FROM ANX_OUTWARD_DOC_HEADER_1A doc "
			// + "LEFT OUTER JOIN ANX_OUTWARD_DOC_ITEM "
			// + "err ON doc.ID = err.DOC_HEADER_ID "
			+ "WHERE doc.ASP_INVOICE_STATUS=1 AND "
			+ "doc.DATAORIGINTYPECODE = 'A' "
			+ "AND doc.PAYLOAD_ID=:payloadId", nativeQuery = true)
	public List<Object[]> aspErrorDocsForRevIntegrationByPayloadId(
			@Param("payloadId") String payloadId);

	@Query(value = "SELECT doc.ID, doc.SUPPLIER_GSTIN, doc.DOC_NUM, doc.DOC_TYPE, doc.DOC_DATE, "
			+ "doc.IS_PROCESSED, doc.GSTN_ERROR, 'FALSE' as InwdError,"
			+ "doc.COMPANY_CODE, doc.FI_YEAR, doc.ACCOUNTING_VOUCHER_NUM, "
			+ "doc.PAYLOAD_ID, doc.RECEIVED_DATE, "
			+ "NULL AS ITEM_NO, '' AS errorField, "
			+ "IFNULL(doc.ERROR_CODES, '') || ',' "
			+ "AS ERROR_CODE, '' AS errorDesc, '' AS type,doc.ACCOUNTING_VOUCHER_DATE "
			+ "FROM ANX_OUTWARD_DOC_HEADER doc "
			+ "WHERE doc.DATAORIGINTYPECODE = 'B' "
			+ "AND doc.PAYLOAD_ID=:payloadId", nativeQuery = true)
	public List<Object[]> bcApiErrorDocsForRevIntegrationByPayloadId(
			@Param("payloadId") String payloadId);

	@Query(value = "SELECT ITM_NO,IFNULL(ERROR_CODES, '') || ',' "
			+ "FROM ANX_OUTWARD_DOC_ITEM "
			+ "WHERE DOC_HEADER_ID=:docHeaderId", nativeQuery = true)
	public List<Object[]> aspErrorDocItemForDocHeaderId(
			@Param("docHeaderId") Long docHeaderId);

	@Query("SELECT doc.docKey,doc.docDate,doc.irnResponse,doc.ackDate,"
			+ "doc.irnStatus,doc.ackNum FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false and  "
			+ "doc.isSubmitted = false and doc.irnResponse IS NOT NULL "
			+ "and doc.ackDate IS NOT NULL and doc.ackNum IS NOT NULL ")
	List<Object[]> findActiveEinvoiceDocsByDocKeys(
			@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.eInvErrorDesc = "
			+ " :eInvErrorDesc, doc.eInvErrorCode = :eInvErrorCode,"
			+ " doc.ewbStatus= :ewbStatus, "
			+ " doc.ewbProcessingStatus = :ewbProcessingStatusCode,"
			+ "  doc.irnStatus = :irnStatus, doc.aspInvoiceStatus = :aspInvStatus, "
			+ " doc.eInvStatus = :einvStatus " + " WHERE doc.id = :id")
	public void updateEinvoiceEwbError(@Param("id") Long id,
			@Param("eInvErrorDesc") String eInvErrorDesc,
			@Param("eInvErrorCode") String eInvErrorCode,
			@Param("ewbStatus") Integer ewbStatus,
			@Param("ewbProcessingStatusCode") int ewbProcessingStatusCode,
			@Param("irnStatus") int irnStatus,
			@Param("aspInvStatus") int aspInvStatus,
			@Param("einvStatus") int einvStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.ackNum = :ackNum, "
			+ "doc.irnResponse  = :irn, doc.ackDate = :ackDt, "
			+ " doc.updatedDate = CURRENT_TIMESTAMP, "
			+ " doc.irnStatus = :einvStatus, doc.ewbNoresp = :ewbNo, "
			+ " doc.ewbDateResp = :ewbDt,"
			+ " doc.ewbProcessingStatus = :ewbProcessingStatusCode,"
			+ " doc.ewbStatus = :ewbStatusCode, doc.infoErrorCode = :infoErrorCode,"
			+ " doc.infoErrorMsg = :infoErrorMessage WHERE doc.id = :id")
	public void updateDocHeader(@Param("id") Long id,
			@Param("ackNum") String ackNum, @Param("ackDt") LocalDateTime ackDt,
			@Param("irn") String irn, @Param("einvStatus") int einvStatus,
			@Param("ewbNo") Long ewbNo, @Param("ewbDt") LocalDateTime ewbDt,
			@Param("ewbProcessingStatusCode") int ewbProcessingStatusCode,
			@Param("ewbStatusCode") int ewbStatusCode,
			@Param("infoErrorCode") String infoErrorCode,
			@Param("infoErrorMessage") String infoErrorMessage);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.eInvStatus= :eInvStatus WHERE doc.id IN " + "(:docIds)")
	public void updateEInvStatusByDocIdList(@Param("docIds") List<Long> docIds,
			@Param("eInvStatus") Integer eInvStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.eInvStatus=:eInvStatus where " + "doc.id = :docHeaderId")
	public void updateEInvByDocId(@Param("docHeaderId") Long docHeaderId,
			@Param("eInvStatus") Integer ewbProcessingStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.eInvErrorDesc = "
			+ " :eInvErrorDesc, doc.eInvErrorCode = :eInvErrorCode,"
			+ "  doc.aspInvoiceStatus = :aspInvStatus, doc.eInvStatus = :einvStatus  WHERE doc.id = :id")
	public void updateCnlEinvoiceError(@Param("id") Long id,
			@Param("eInvErrorDesc") String eInvErrorDesc,
			@Param("eInvErrorCode") String eInvErrorCode,
			@Param("aspInvStatus") int aspInvStatus,
			@Param("einvStatus") int einvStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.ackNum = :ackNum, "
			+ "doc.irnResponse  = :irn, doc.ackDate = :ackDt, "
			+ " doc.updatedDate = CURRENT_TIMESTAMP, "
			+ " doc.irnStatus = :einvStatus, doc.ewbNoresp = :ewbNo, "
			+ " doc.ewbDateResp = :ewbDt, doc.ewbStatus= :ewbStatus,"
			+ " doc.ewbProcessingStatus = :ewbProcessingStatusCode,"
			+ "  doc.infoErrorCode = :infoErrorCode,"
			+ " doc.infoErrorMsg = :infoErrorMessage, doc.ewbErrorCode = :infoErrorCode,"
			+ " doc.ewbErrorDesc = :infoErrorMessage " + " WHERE doc.id = :id")
	public void updateDocHeaderForEWBFailure(@Param("id") Long id,
			@Param("ackNum") String ackNum, @Param("ackDt") LocalDateTime ackDt,
			@Param("irn") String irn, @Param("einvStatus") int einvStatus,
			@Param("ewbNo") String ewbNo, @Param("ewbDt") LocalDateTime ewbDt,
			@Param("ewbStatus") Integer ewbStatus,
			@Param("ewbProcessingStatusCode") int ewbProcessingStatusCode,
			@Param("infoErrorCode") String infoErrorCode,
			@Param("infoErrorMessage") String infoErrorMessage);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.ackNum = :ackNum, "
			+ "doc.irnResponse  = :irn, doc.ackDate = :ackDt, "
			+ " doc.updatedDate = CURRENT_TIMESTAMP, "
			+ " doc.irnStatus = :einvStatus WHERE doc.id = :id")
	public void updateEinvoiceDocHeader(@Param("id") Long id,
			@Param("ackNum") String ackNum, @Param("ackDt") LocalDateTime ackDt,
			@Param("irn") String irn, @Param("einvStatus") int einvStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.ewbStatus= :ewbStatus, "
			+ "doc.ewbProcessingStatus= :ewbProcessingStatus where "
			+ "doc.id = :docHeaderId")
	public void updateEwbCancelStatusByDocId(
			@Param("docHeaderId") Long docHeaderId,
			@Param("ewbStatus") Integer ewbStatus,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus);

	public int findByIrnStatus(Long docHeaderId);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.ewbErrorCode = :errorCode, "
			+ "doc.ewbErrorDesc = :errorDesc, "
			+ " doc.aspInvoiceStatus = :aspInvStatus, "
			+ " doc.ewbProcessingStatus=:ewbProcessingStatus "
			+ " WHERE doc.id = :id")
	public void updateCancelErrorEwbResponseById(@Param("id") Long id,
			@Param("errorCode") String errorCode,
			@Param("errorDesc") String errorDesc,
			@Param("aspInvStatus") int aspInvStatus,
			@Param("ewbProcessingStatus") Integer ewbProcessingStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.eInvErrorDesc = "
			+ " :eInvErrorDesc, doc.eInvErrorCode = :eInvErrorCode "
			+ " WHERE doc.id = :id")
	public void updateCancelEinvoiceError(@Param("id") Long id,
			@Param("eInvErrorDesc") String eInvErrorDesc,
			@Param("eInvErrorCode") String eInvErrorCode);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET "
			+ "doc.irnStatus = :irnStatus, doc.eInvStatus = :einvStatus, doc.isDeleted = true WHERE doc.id = :id")
	void updateEInvStatusByIrnAndDelete(@Param("id") Long id,
			@Param("irnStatus") Integer status,
			@Param("einvStatus") int einvStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.ackNum = :ackNum, "
			+ "doc.irnResponse  = :irn, doc.ackDate = :ackDt, "
			+ " doc.updatedDate = CURRENT_TIMESTAMP, doc.irnStatus = :einvStatus"
			+ " WHERE doc.id = :id")
	public void updateIrnDetails(@Param("id") Long id,
			@Param("ackNum") String ackNum, @Param("ackDt") LocalDateTime ackDt,
			@Param("irn") String irn, @Param("einvStatus") int einvStatus);

	@Query("SELECT doc FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.acceptanceId = :id  ")
	public List<Gstr1AOutwardTransDocument> findByFileId(@Param("id") Long id);

	@Query("SELECT doc FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.id IN (:ids)  ")
	public List<Gstr1AOutwardTransDocument> findById(@Param("ids") List<Long> ids);

	@Query("SELECT COUNT(*) FROM Gstr1AOutwardTransDocument "
			+ "WHERE sgstin=:gstin AND derivedTaxperiod BETWEEN "
			+ ":derivedTaxPeriodFrom AND :derivedTaxPeriodTo ")
	public int gstinCountByRetPerFromTo(@Param("gstin") String gstin,
			@Param("derivedTaxPeriodFrom") int derivedTaxPeriodFrom,
			@Param("derivedTaxPeriodTo") int derivedTaxPeriodTo);

	@Query("SELECT doc.docNo, doc.sgstin FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.id = :id  ")
	public List<Object[]> findDocNumGstinById(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.ackNum = :ackNum, "
			+ "doc.irnResponse  = :irn, doc.ackDate = :ackDt, "
			+ " doc.updatedDate = CURRENT_TIMESTAMP, doc.irnStatus = :irnStatus,doc.eInvErrorDesc = "
			+ ":eInvErrorDesc, doc.eInvErrorCode = :eInvErrorCode,doc.ewbErrorCode = :ewbErrorCode, "
			+ "doc.ewbErrorDesc = :ewbErrorDesc,"
			+ "doc.aspInvoiceStatus = :aspInvStatus, doc.eInvStatus = :einvStatus  WHERE doc.id = :id")
	public void updateIrnDetails(@Param("id") Long id,
			@Param("ackNum") String ackNum, @Param("ackDt") LocalDateTime ackDt,
			@Param("irn") String irn, @Param("irnStatus") int irnStatus,
			@Param("eInvErrorDesc") String eInvErrorDesc,
			@Param("eInvErrorCode") String eInvErrorCode,
			@Param("ewbErrorCode") String ewbErrorCode,
			@Param("ewbErrorDesc") String errorDesc,
			@Param("aspInvStatus") int aspInvStatus,
			@Param("einvStatus") int einvStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.ackNum = :ackNum, "
			+ "doc.irnResponse  = :irn, doc.ackDate = :ackDt, "
			+ " doc.updatedDate = CURRENT_TIMESTAMP, "
			+ " doc.irnStatus = :irnStatus, doc.ewbNoresp = :ewbNo, "
			+ " doc.ewbDateResp = :ewbDt, doc.ewbStatus= :ewbStatus,"
			+ " doc.ewbProcessingStatus = :ewbProcessingStatusCode,"
			+ "  doc.infoErrorCode = :infoErrorCode,"
			+ " doc.infoErrorMsg = :infoErrorMessage, doc.ewbErrorCode = :infoErrorCode,"
			+ " doc.ewbErrorDesc = :infoErrorMessage,doc.eInvErrorDesc = "
			+ ":eInvErrorDesc, doc.eInvErrorCode = :eInvErrorCode,"
			+ "doc.aspInvoiceStatus = :aspInvStatus, doc.eInvStatus = :einvStatus"
			+ "  WHERE doc.id = :id")
	public void updateDocHeaderForEWBFailure(@Param("id") Long id,
			@Param("ackNum") String ackNum, @Param("ackDt") LocalDateTime ackDt,
			@Param("irn") String irn, @Param("irnStatus") int irnStatus,
			@Param("ewbNo") String ewbNo, @Param("ewbDt") LocalDateTime ewbDt,
			@Param("ewbStatus") Integer ewbStatus,
			@Param("ewbProcessingStatusCode") int ewbProcessingStatusCode,
			@Param("infoErrorCode") String infoErrorCode,
			@Param("infoErrorMessage") String infoErrorMessage,
			@Param("eInvErrorDesc") String eInvErrorDesc,
			@Param("eInvErrorCode") String eInvErrorCode,
			@Param("aspInvStatus") int aspInvStatus,
			@Param("einvStatus") int einvStatus);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.ackNum = :ackNum, "
			+ "doc.irnResponse  = :irn, doc.ackDate = :ackDt, "
			+ " doc.updatedDate = CURRENT_TIMESTAMP, "
			+ " doc.irnStatus = :irnStatus,doc.eInvErrorDesc = "
			+ ":eInvErrorDesc, doc.eInvErrorCode = :eInvErrorCode,"
			+ "doc.aspInvoiceStatus = :aspInvStatus, doc.eInvStatus = :einvStatus WHERE doc.id = :id")
	public void updateEinvoiceDocHeader(@Param("id") Long id,
			@Param("ackNum") String ackNum, @Param("ackDt") LocalDateTime ackDt,
			@Param("irn") String irn, @Param("irnStatus") int irnStatus,
			@Param("eInvErrorDesc") String eInvErrorDesc,
			@Param("eInvErrorCode") String eInvErrorCode,
			@Param("aspInvStatus") int aspInvStatus,
			@Param("einvStatus") int einvStatus);

	@Query("Select docKey, aspInvoiceStatus from Gstr1AOutwardTransDocument "
			+ "where docKey In(:docKeys) AND isDeleted = false")
	public List<Object[]> findAspInvoiceStatusByDocKeys(
			@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isSaved =:isSaved, "
			+ "doc.isSent =:isSent, doc.updatedDate = CURRENT_TIMESTAMP "
			+ "  WHERE doc.docKey In (:docKey) AND isDeleted = false "
			+ " AND aspInvoiceStatus = 2")
	public int updateIsSavetoGstnAndIsSendToGstnIn(
			@Param("isSaved") Boolean isSaved, @Param("isSent") Boolean isSent,
			@Param("docKey") List<String> docKey);

	@Query("SELECT doc.id,doc.isSent,doc.isSaved ,doc.isGstnError from Gstr1AOutwardTransDocument doc where doc.id In (:docId)")
	public List<Object[]> getGstnSaveSentAndErrorFlagsById(
			@Param("docId") List<Long> docId);

	@Query("SELECT doc.id from Gstr1AOutwardTransDocument doc where doc.id IN :ids AND doc.isDeleted = false")
	public List<Long> getActiveIds(@Param("ids") List<Long> ids);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isSaved =:isSaved, "
			+ " doc.isSent =:isSent,doc.einvGstnSaveStatus =:einvStatus, doc.updatedDate = CURRENT_TIMESTAMP,"
			+ " doc.reconResponseId = :reconResponseId,doc.reconResponseSource =:reconResponseSource,"
			+ " doc.gstnBatchId =:gstnBatchId,doc.sentToGSTNDate =:sentToGSTNDate,doc.savedToGSTNDate =:savedToGSTNDate,"
			+ " doc.isGstnError =:isGstnError,doc.gstnErrorCode =:gstnErrorCode,doc.gstnErrorDesc=:gstnErrorDesc  "
			+ " WHERE doc.id In (:docId)")
	public int updateIsSavetoGstnAndIsSendToGstnInToTrueByDocID(
			@Param("isSaved") boolean isSaved, @Param("isSent") boolean isSent,
			@Param("einvStatus") String einvStatus,
			@Param("docId") List<Long> docId,
			@Param("reconResponseId") Long reconResponseId,
			@Param("reconResponseSource") String reconResponseSource,
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("sentToGSTNDate") LocalDate sentToGSTNDate,
			@Param("savedToGSTNDate") LocalDate savedToGSTNDate,
			@Param("isGstnError") boolean isGstnError,
			@Param("gstnErrorCode") String gstnErrorCode,
			@Param("gstnErrorDesc") String gstnErrorDesc);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.isSaved =:isSaved, "
			+ " doc.isSent =:isSent, doc.einvGstnSaveStatus =:einvStatus, doc.updatedDate = CURRENT_TIMESTAMP, "
			+ " doc.reconResponseId = :reconResponseId,doc.reconResponseSource =:reconResponseSource, "
			+ " doc.gstnBatchId =:gstnBatchId,doc.sentToGSTNDate =:sentToGSTNDate,doc.savedToGSTNDate =:savedToGSTNDate,"
			+ " doc.isGstnError =:isGstnError,doc.gstnErrorCode =:gstnErrorCode,doc.gstnErrorDesc=:gstnErrorDesc  "
			+ " WHERE doc.id In (:docId)")
	public int updateIsSavetoGstnAndIsSendToGstnInToFalseByDocID(
			@Param("isSaved") boolean isSaved, @Param("isSent") boolean isSent,
			@Param("einvStatus") String einvStatus,
			@Param("docId") List<Long> docId,
			@Param("reconResponseId") Long reconResponseId,
			@Param("reconResponseSource") String reconResponseSource,
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("sentToGSTNDate") LocalDate sentToGSTNDate,
			@Param("savedToGSTNDate") LocalDate savedToGSTNDate,
			@Param("isGstnError") boolean isGstnError,
			@Param("gstnErrorCode") String gstnErrorCode,
			@Param("gstnErrorDesc") String gstnErrorDesc);

	@Modifying
	@Query("UPDATE Gstr1AOutwardTransDocument doc SET doc.einvGstnSaveStatus =:einvStatus, doc.updatedDate = CURRENT_TIMESTAMP,"
			+ " doc.reconResponseId = :reconResponseId,doc.reconResponseSource =:reconResponseSource "
			+ " WHERE doc.id In (:docId)")
	public int updateEinvGstinSaveStatusByDocID(
			@Param("einvStatus") String einvStatus,
			@Param("docId") List<Long> docId,
			@Param("reconResponseId") Long reconResponseId,
			@Param("reconResponseSource") String reconResponseSource);

	@Query("SELECT doc.docKey,doc.docDate,doc.einvGstnSaveStatus "
			+ "FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDeleted = false and  "
			+ "doc.isSubmitted = false and doc.einvGstnSaveStatus <> 'R' ")
	List<Object[]> findActiveEinvGstinSaveStatus(
			@Param("docKeys") List<String> docKeys);

	@Query("SELECT doc.docKey,doc.gstnBifurcation FROM Gstr1AOutwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.supplyType <> 'CAN' "
			+ "AND doc.aspInvoiceStatus=2 AND doc.isDeleted = false")
	public List<Object[]> findActiveOrgDocs(
			@Param("docKeys") List<String> docKeys);

	@Query(value = "SELECT HDR.ID,COUNT(ITM.DOC_HEADER_ID) "
			+ "FROM ANX_OUTWARD_DOC_HEADER_1A HDR,ANX_OUTWARD_DOC_ITEM_1A ITM "
			+ "WHERE HDR.ID = ITM.DOC_HEADER_ID "
			+ "AND HDR.ID IN (:docIds) GROUP BY HDR.ID ", nativeQuery = true)
	public List<Object[]> findDocsByIds(@Param("docIds") List<Long> docIds);

	@Query("select distinct cgstin from Gstr1AOutwardTransDocument where isDeleted = false "
			+ "and isProcessed = true and  cgstin IS NOT NULL and cgstin <>'URP' and "
			+ "derivedCgstinPan IN (:pans)")
	public List<String> getDistinctCustomerGstin(
			@Param("pans") List<String> pans);

	@Query("select distinct derivedCgstinPan from Gstr1AOutwardTransDocument where isDeleted = false "
			+ "and isProcessed = true and  derivedCgstinPan IS NOT NULL and derivedCgstinPan <>'URP' ")
	public List<String> getDistinctCustomerPans();

	@Query(value = "select distinct cgstin,custOrSuppName "
			+ "from Gstr1AOutwardTransDocument where isDeleted = false "
			+ "and isProcessed = true and  cgstin IS NOT NULL and cgstin <>'URP' and "
			+ "custOrSuppName IS NOT NULL and cgstin IN (:cgstin)")
	public List<Object[]> findCustomerNameByGstin(
			@Param("cgstin") List<String> cgstin);

	@Query("SELECT COUNT(*) FROM Gstr1AOutwardTransDocument  WHERE isProcessed = true "
			+ "AND isDeleted = false and sgstin=:sgstin and taxperiod=:taxperiod")
	public int isInvDataAvaCount(@Param("sgstin") String sgstin,
			@Param("taxperiod") String taxperiod);

	@Query("SELECT COUNT(*) FROM Gstr1AOutwardTransDocument  WHERE isProcessed = true "
			+ "AND isDeleted = false and sgstin=:sgstin and taxperiod=:taxperiod and supplyType <> 'CAN'")
	public int isOutDataAvaCount(@Param("sgstin") String sgstin,
			@Param("taxperiod") String taxperiod);

	@Query("SELECT doc from Gstr1AOutwardTransDocument doc"
			+ " where doc.ewbNoresp = :ewbNoresp AND doc.isDeleted = false")
	public Optional<Gstr1AOutwardTransDocument> findDocByEwbNo(
			@Param("ewbNoresp") Long ewbNoresp);

	@Query("SELECT doc from Gstr1AOutwardTransDocument doc where doc.id=:id AND doc.isDeleted = false")
	public Optional<Gstr1AOutwardTransDocument> getActiveRecord(@Param("id") Long id);
	
	@Query("SELECT concat(doc.taxperiod,doc.docKey) FROM Gstr1AOutwardTransDocument doc"
			+ " WHERE doc.docKey IN (:docs) and doc.isDeleted=false and isProcessed = true")
	public List<String> findByDocKeyInAndIsDeletedFalseWithTaxPeriod(@Param("docs")List<String> docs);
	

}
