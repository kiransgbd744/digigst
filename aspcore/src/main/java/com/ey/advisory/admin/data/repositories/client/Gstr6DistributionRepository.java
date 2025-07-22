package com.ey.advisory.admin.data.repositories.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr6DistributionEntity;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Repository("Gstr6DistributionRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr6DistributionRepository
		extends JpaRepository<Gstr6DistributionEntity, Long>,
		JpaSpecificationExecutor<Gstr6DistributionEntity> {

	@Query("select g.id,g.returnPeriod,g.isdGstin,g.recipientGSTIN,g.stateCode,g.documentType,"
			+ "g.supplyType,g.docNum,g.docDate,g.origDocNumber,g.origDocDate,g.eligibleIndicator,"
			+ "g.igstAsIgst,g.igstAsSgst,g.igstAsCgst,g.sgstAsSgst,g.sgstAsIgst,g.cgstAsCgst,"
			+ "g.cgstAsIgst,g.cessAmount, "
			+ "g.processKey,g.fileId,g.asEnterTableId "
			+ "FROM Gstr6DistributionEntity g WHERE "
			+ "g.documentType IN ('INV','CR') and g.eligibleIndicator IN ('IS','E')"
			+ "AND g.isDelete = false "
			+ "AND g.derivedRetPeriod=:returnPeriod AND g.isdGstin IN (:gstin) AND g.supplyType IS NULL ")
	List<Object[]> getGstr6DistributedSummaryData(
			@Param("returnPeriod") Integer returnPeriod,
			@Param("gstin") List<String> gstinList);
	
	//pagination for summery1
	@Query("select g.id,g.returnPeriod,g.isdGstin,g.recipientGSTIN,g.stateCode,g.documentType,"
			+ "g.supplyType,g.docNum,g.docDate,g.origDocNumber,g.origDocDate,g.eligibleIndicator,"
			+ "g.igstAsIgst,g.igstAsSgst,g.igstAsCgst,g.sgstAsSgst,g.sgstAsIgst,g.cgstAsCgst,"
			+ "g.cgstAsIgst,g.cessAmount, "
			+ "g.processKey,g.fileId,g.asEnterTableId "
			+ "FROM Gstr6DistributionEntity g WHERE "
			+ "g.documentType IN ('INV','CR') and g.eligibleIndicator IN ('IS','E')"
			+ "AND g.isDelete = false "
			+ "AND g.derivedRetPeriod=:returnPeriod AND g.isdGstin IN (:gstin) AND g.supplyType IS NULL ")
	List<Object[]> getGstr6DistributedSummaryDataPagination(
			@Param("returnPeriod") Integer returnPeriod,
			@Param("gstin") List<String> gstinList,Pageable pageReq);

	@Query("select g.id,g.returnPeriod,g.isdGstin,g.recipientGSTIN,g.stateCode,g.documentType,"
			+ "g.supplyType,g.docNum,g.docDate,g.origDocNumber,g.origDocDate,g.eligibleIndicator,"
			+ "g.igstAsIgst,g.igstAsSgst,g.igstAsCgst,g.sgstAsSgst,g.sgstAsIgst,g.cgstAsCgst,"
			+ "g.cgstAsIgst,g.cessAmount,"
			+ "g.processKey,g.fileId,g.asEnterTableId "
			+ " FROM Gstr6DistributionEntity g WHERE "
			+ "g.documentType IN ('INV','CR') and g.eligibleIndicator IN ('NO','IE')"
			+ "AND g.isDelete = false "
			+ "AND g.derivedRetPeriod=:returnPeriod AND g.isdGstin IN (:gstin) AND g.supplyType IS NULL ")
	List<Object[]> getGstr6DistributedSummaryIneligibleData(
			@Param("returnPeriod") Integer returnPeriod,
			@Param("gstin") List<String> gstinList);
	
	@Query("select g.id,g.returnPeriod,g.isdGstin,g.recipientGSTIN,g.stateCode,g.documentType,"
			+ "g.supplyType,g.docNum,g.docDate,g.origDocNumber,g.origDocDate,g.eligibleIndicator,"
			+ "g.igstAsIgst,g.igstAsSgst,g.igstAsCgst,g.sgstAsSgst,g.sgstAsIgst,g.cgstAsCgst,"
			+ "g.cgstAsIgst,g.cessAmount,"
			+ "g.processKey,g.fileId,g.asEnterTableId "
			+ " FROM Gstr6DistributionEntity g WHERE "
			+ "g.documentType IN ('INV','CR') and g.eligibleIndicator IN ('NO','IE')"
			+ "AND g.isDelete = false "
			+ "AND g.derivedRetPeriod=:returnPeriod AND g.isdGstin IN (:gstin) AND g.supplyType IS NULL ")
	List<Object[]> getGstr6DistributedSummaryIneligibleDataPagination(
			@Param("returnPeriod") Integer returnPeriod,
			@Param("gstin") List<String> gstinList,Pageable pageReq);

	@Query("select g.id,g.returnPeriod,g.isdGstin,g.recipientGSTIN,g.stateCode,g.documentType,"
			+ "g.supplyType,g.docNum,g.docDate,g.origDocNumber,g.origDocDate,g.eligibleIndicator,"
			+ "g.igstAsIgst,g.igstAsSgst,g.igstAsCgst,g.sgstAsSgst,g.sgstAsIgst,g.cgstAsCgst,"
			+ "g.cgstAsIgst,g.cessAmount,"
			+ "g.processKey,g.fileId,g.asEnterTableId,g.origCrNoteNumber,g.origCrNoteDate,g.originalRecipeintGstin,g.originalStatecode "
			+ "FROM Gstr6DistributionEntity g WHERE "
			+ "g.documentType IN ('RNV','RCR') and g.eligibleIndicator IN ('IS','E')"
			+ "AND g.isDelete = false "
			+ "AND g.derivedRetPeriod=:returnPeriod AND g.isdGstin IN (:gstin) AND g.supplyType IS NULL ")
	List<Object[]> getGstr6ReDistributedSummaryData(
			@Param("returnPeriod") Integer returnPeriod,
			@Param("gstin") List<String> gstinList);
	
	@Query("select g.id,g.returnPeriod,g.isdGstin,g.recipientGSTIN,g.stateCode,g.documentType,"
			+ "g.supplyType,g.docNum,g.docDate,g.origDocNumber,g.origDocDate,g.eligibleIndicator,"
			+ "g.igstAsIgst,g.igstAsSgst,g.igstAsCgst,g.sgstAsSgst,g.sgstAsIgst,g.cgstAsCgst,"
			+ "g.cgstAsIgst,g.cessAmount,"
			+ "g.processKey,g.fileId,g.asEnterTableId,g.origCrNoteNumber,g.origCrNoteDate,g.originalRecipeintGstin,g.originalStatecode "
			+ "FROM Gstr6DistributionEntity g WHERE "
			+ "g.documentType IN ('RNV','RCR') and g.eligibleIndicator IN ('IS','E')"
			+ "AND g.isDelete = false "
			+ "AND g.derivedRetPeriod=:returnPeriod AND g.isdGstin IN (:gstin) AND g.supplyType IS NULL ")
	List<Object[]> getGstr6ReDistributedSummaryDataPagination(
			@Param("returnPeriod") Integer returnPeriod,
			@Param("gstin") List<String> gstinList,Pageable pageReq);

	@Query("select g.id,g.returnPeriod,g.isdGstin,g.recipientGSTIN,g.stateCode,g.documentType,"
			+ "g.supplyType,g.docNum,g.docDate,g.origDocNumber,g.origDocDate,g.eligibleIndicator,"
			+ "g.igstAsIgst,g.igstAsSgst,g.igstAsCgst,g.sgstAsSgst,g.sgstAsIgst,g.cgstAsCgst,"
			+ "g.cgstAsIgst,g.cessAmount,"
			+ "g.processKey,g.fileId,g.asEnterTableId,g.origCrNoteNumber,g.origCrNoteDate,g.originalRecipeintGstin,g.originalStatecode "
			+ " FROM Gstr6DistributionEntity g WHERE "
			+ "g.documentType IN ('RNV','RCR') and g.eligibleIndicator IN ('NO','IE')"
			+ "AND g.isDelete = false "
			+ "AND g.derivedRetPeriod=:returnPeriod AND g.isdGstin IN (:gstin) AND g.supplyType IS NULL ")
	List<Object[]> getGstr6ReDistributedInEligibleSummaryData(
			@Param("returnPeriod") Integer returnPeriod,
			@Param("gstin") List<String> gstinList);
	
	@Query("select g.id,g.returnPeriod,g.isdGstin,g.recipientGSTIN,g.stateCode,g.documentType,"
			+ "g.supplyType,g.docNum,g.docDate,g.origDocNumber,g.origDocDate,g.eligibleIndicator,"
			+ "g.igstAsIgst,g.igstAsSgst,g.igstAsCgst,g.sgstAsSgst,g.sgstAsIgst,g.cgstAsCgst,"
			+ "g.cgstAsIgst,g.cessAmount,"
			+ "g.processKey,g.fileId,g.asEnterTableId,g.origCrNoteNumber,g.origCrNoteDate,g.originalRecipeintGstin,g.originalStatecode "
			+ " FROM Gstr6DistributionEntity g WHERE "
			+ "g.documentType IN ('RNV','RCR') and g.eligibleIndicator IN ('NO','IE')"
			+ "AND g.isDelete = false "
			+ "AND g.derivedRetPeriod=:returnPeriod AND g.isdGstin IN (:gstin) AND g.supplyType IS NULL ")
	List<Object[]> getGstr6ReDistributedInEligibleSummaryDataPagination(
			@Param("returnPeriod") Integer returnPeriod,
			@Param("gstin") List<String> gstinList,Pageable pageReq);

	@Transactional
	@Modifying
	@Query("UPDATE Gstr6DistributionEntity b SET b.isDelete = TRUE "
			+ "WHERE b.processKey IN (:b2cInvKey) ")
	public void updateSameInvKey(@Param("b2cInvKey") String b2cInvKey);

	@Transactional
	@Modifying
	@Query("UPDATE Gstr6DistributionEntity  SET isDelete = true WHERE id IN (:id)")
	public void deleteEntityByIds(@Param("id") List<Long> id);

	@Query("SELECT doc.processKey,isSubmitted FROM Gstr6DistributionEntity doc "
			+ "WHERE doc.processKey IN (:docKeys) AND doc.isDelete = false")
	public List<Object[]> findCancelDocsCountsByDocKeys(
			@Param("docKeys") List<String> docKeys);

	@Query("SELECT returnPeriod FROM Gstr6DistributionEntity "
			+ "WHERE isdGstin=:gstin AND supplyType='CAN' AND isDelete=false ")
	public List<String> getRetPeriodsByGstin(@Param("gstin") String gstin);
	
	@Modifying
	@Query("UPDATE Gstr6DistributionEntity doc SET doc.isSentToGstn = false, doc.isSaveToGstn "
			+ "= false, doc.isError = false, doc.gstnBatchId =null, doc."
			+ "sentToGSTNDate = null, doc.savedToGSTNDate = null, doc."
			+ "gstnErrorCode = null, doc.gstnErrorDesc = null WHERE doc.isdGstin "
			+ "= :isdGstin AND doc.returnPeriod = :retPeriod AND "
			+ "doc.isDelete = false AND doc.isSentToGstn = true")
	public void resetSaveGstr6AuditColumns(@Param("isdGstin") String isdGstin,
			@Param("retPeriod") String retPeriod);
	
	@Query("SELECT isdGstin, documentType, docNum, docDate, recipientGSTIN FROM Gstr6DistributionEntity " +
	           "WHERE derivedRetPeriod BETWEEN :startPeriod AND :endPeriod " +
	           "AND isdGstin = :isdGstin " +
	           "AND docNum = :orgDocNum " +
	           "AND docDate = :orgDocDate AND isDelete = false")
	public List<Object[]> getDistributionData(@Param("startPeriod") Integer startPeriod,
	                                       @Param("endPeriod") Integer endPeriod,
	                                       @Param("isdGstin") String isdGstin,
	                                       @Param("orgDocNum") String orgDocNum,
	                                       @Param("orgDocDate") LocalDate orgDocDate);
	
	@Query("SELECT eligibleIndicator, documentType FROM Gstr6DistributionEntity " +
		       "WHERE derivedRetPeriod BETWEEN :startPeriod AND :endPeriod " +
		       "AND isdGstin = :isdGstin " +
		       "AND docNum = :orgDocNum " +
		       "AND docDate = :orgDocDate AND isDelete = false")
	public List<Object[]> getEligibilityIndicators(@Param("startPeriod") Integer startPeriod,
		                                             @Param("endPeriod") Integer endPeriod,
		                                             @Param("isdGstin") String isdGstin,
		                                             @Param("orgDocNum") String orgDocNum,
		                                             @Param("orgDocDate") LocalDate orgDocDate);

}
