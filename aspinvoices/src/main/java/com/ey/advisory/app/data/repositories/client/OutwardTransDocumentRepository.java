package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;

@Repository("outwardTransDocumentRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface OutwardTransDocumentRepository
		extends CrudRepository<OutwardTransDocument, Long> {

	/*
	 * @Query("SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,doc.docAmount,"
	 * + "doc.pos,doc.reverseCharge,doc.egstin,doc.diffPercent,rate.lineNo," +
	 * "rate.taxRate,rate.taxValue,rate.igstAmt,rate.cgstAmt,rate.sgstAmt," +
	 * "rate.cessAmt,doc.id,doc.supplyType,doc.taxPayable,doc.docDate " +
	 * "FROM OutwardTransDocument doc JOIN DocRateSummary rate ON " +
	 * "doc.id=rate.docHeaderId AND doc.receivedDate IN (:criteria) AND " +
	 * "doc.isProcessed=true AND doc.isSent=false ORDER BY doc.sgstin")
	 * List<Object[]> findByCriteria(@Param("criteria") List<LocalDate>
	 * criteria);
	 * 
	 * @Modifying
	 * 
	 * @Query("UPDATE OutwardTransDocument doc SET doc.gstnBatchId=:gstnBatchId,"
	 * + "doc.isSent=true WHERE doc.id IN (:ids)") void
	 * updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
	 * 
	 * @Param("ids") List<Long> ids);
	 */

	/*
	 * @Query("SELECT doc.sgstin from OutwardTransDocument doc " +
	 * "WHERE doc.gstnBatchId = :id ") String getBatchId(@Param("id") Long id);
	 * 
	 * 
	 * @Modifying
	 * 
	 * @Query("UPDATE OutwardTransDocument doc SET doc.isError = true WHERE " +
	 * "doc.docKey IN (:invKeyList) AND doc.gstnBatchId = :gstnBatchId " +
	 * "AND doc.isDeleted = false") public void
	 * markDocsAsErrorsForBatch(@Param("gstnBatchId") Long id,
	 * 
	 * @Param("invKeyList") List<String> invKeyList);
	 * 
	 *//**
		 * This method will mark all the documents for a batch as saved, by
		 * updating the isSAved flag to true.
		 * 
		 * @param id
		 */
	/*
	 * @Modifying
	 * 
	 * @Query("UPDATE OutwardTransDocument doc SET doc.isSaved  = true " +
	 * "WHERE doc.gstnBatchId = :id AND doc.isDeleted = false") public void
	 * markDocsAsSavedForBatch(@Param("id") Long id);
	 * 
	 *//**
		 * This method takes the list of invoice keys for which GSTN returned
		 * errors and marks all the other invoices in the specified batch as
		 * succeeded, by updating the 'isSavedToGstn' flag as true.
		 * 
		 * @param id
		 * @param erroredDocKeyList
		 */
	/*
	 * @Modifying
	 * 
	 * @Query("UPDATE OutwardTransDocument doc SET doc.isSaved = true WHERE " +
	 * "doc.docKey NOT IN (:erroredDocKeyList) AND " +
	 * "doc.gstnBatchId = :gstnBatchId AND doc.isDeleted = false") public void
	 * markDocsAsSavedForBatchByErroredDocKeys(
	 * 
	 * @Param("gstnBatchId") Long id,
	 * 
	 * @Param("erroredDocKeyList") List<String> erroredDocKeyList);
	 * 
	 *//**
		 * This method accepts a list of doc keys and returns an object array
		 * where the first element of the array is the document header id and
		 * the second element is the invoice key. We are selecting the invoice
		 * key back because, since we're not doing an ORDER BY, the invoices
		 * returned may not be in the order in which we passed the invoice keys.
		 * Hence, we need both the invoice key and the document header id as
		 * return.
		 * 
		 * @param id
		 * @param invKeyList
		 * @return
		 *//*
		 * @Query("SELECT doc.id, doc.docKey FROM OutwardTransDocument doc " +
		 * "WHERE doc.docKey IN " +
		 * "(:invKeyList) AND doc.gstnBatchId = :gstnBatchId and " +
		 * "doc.isDeleted = false") public List<Object[]>
		 * findDocIdsForBatchByDocKeys(
		 * 
		 * @Param("gstnBatchId") Long id,
		 * 
		 * @Param("invKeyList") List<String> invKeyList );
		 */
	@Modifying
	@Query("UPDATE OutwardTransDocument outWardEntity SET "
			+ "outWardEntity.isDeleted = TRUE WHERE "
			+ "outWardEntity.sgstin = :sgstin  AND "
			+ "outWardEntity.cgstin = :csgstin AND "
			+ "outWardEntity.docNo = :docNumber AND "
			+ "outWardEntity.docDate = :docDate AND "
			+ " outWardEntity.isSaved =  TRUE ")
	int findOrginalDetails(@Param("sgstin") String sgstin,
			@Param("csgstin") String csgstin,
			@Param("docNumber") String docNumber,
			@Param("docDate") LocalDate docDate);

	Optional<OutwardTransDocument> findByEwbNorespAndIsDeleted(Long ewbNo,
			boolean b);

	// @Query("SELECT doc from OutwardTransDocument doc WHERE id = :id")
	public Optional<OutwardTransDocument> findById(/* @Param ("id") */ Long id);

	Optional<OutwardTransDocument> findByIdAndIsDeleted(Long docHeaderId,
			boolean b);

	@Query("SELECT doc FROM OutwardTransDocument doc "
			+ "WHERE doc.docKey IN (:docKeys) "
			+ "AND doc.isDeleted = true AND doc.aspInvoiceStatus=2 order by id desc ")
	public List<OutwardTransDocument> findInactiveRecordsByDocKey(
			@Param("docKeys") String docKeys);
	
	@Query("SELECT doc FROM OutwardTransDocument doc "
			+ "WHERE doc.ewbNoresp IN (:ewbNo) "
			+ "AND doc.isDeleted = false AND doc.aspInvoiceStatus=2 ")
	List<OutwardTransDocument> findByEwayBillNo(@Param("ewbNo") List<Long> ewbNo);

}
