package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1vsEinvReconRespProcessedEntity;

/**
 * 
 * @author Jithendra Kumar B
 *
 */
@Repository("Gstr1vsEinvReconRespProcessedRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1vsEinvReconRespProcessedRepository
		extends JpaRepository<Gstr1vsEinvReconRespProcessedEntity, Long>,
		JpaSpecificationExecutor<Gstr1vsEinvReconRespProcessedEntity> {

	@Modifying
	@Query("UPDATE Gstr1vsEinvReconRespProcessedEntity entity SET entity.isDelete=true,"
			+ " entity.updatedOn = CURRENT_TIMESTAMP where entity.isDelete=false and "
			+ " entity.docKeySR in :invoiceKeys")
	public int softDeleteDuplicateInvBydocKeySR(
			@Param("invoiceKeys") List<String> invoiceKeys);

	@Modifying
	@Query("UPDATE Gstr1vsEinvReconRespProcessedEntity entity SET entity.isDelete=true,"
			+ " entity.updatedOn = CURRENT_TIMESTAMP where entity.isDelete=false and "
			+ " entity.docKeyEinv in :invoiceKeys")
	public int softDeleteDuplicateInvBydocKeyEinv(
			@Param("invoiceKeys") List<String> invoiceKeys);

	@Query("SELECT doc.id,doc.sgstinEinv,doc.retPeriodEinv,doc.cgstinEinv,doc.docNumEinv,"
			+ "doc.docDateEinv,doc.docTypeEinv,doc.tableTypeEinv FROM Gstr1vsEinvReconRespProcessedEntity"
			+ " doc WHERE doc.sgstinEinv =:sgstin AND doc.retPeriodEinv = :returnPeriod AND "
			+ "doc.tableTypeEinv IN (:tableTypeEinv) AND doc.isProcessed = true AND "
			+ "doc.isDelete = false AND doc.userResponse = 'D' AND doc.isSent = false")
	public List<Object[]> findDocsByGstinAndRetPeriodAndTableTypes(
			@Param("sgstin") String sgstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("tableTypeEinv") List<String> tableTypeEinv);

	@Modifying
	@Query("UPDATE Gstr1vsEinvReconRespProcessedEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids);

	/**
	 * This method will mark all the documents for a batch as saved, by updating
	 * the isSAved flag to true.
	 * 
	 * @param id
	 */
	@Modifying
	@Query("UPDATE Gstr1vsEinvReconRespProcessedEntity doc SET doc.isSaved  = true,"
			+ "doc.savedToGSTNDate = CURRENT_TIMESTAMP,doc.isGstnError = false "
			+ "WHERE doc.gstnBatchId = :gstnBatchId")
	public void markDocsAsSavedForBatch(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE Gstr1vsEinvReconRespProcessedEntity doc SET doc.isGstnError = true,"
			+ "doc.savedToGSTNDate = CURRENT_TIMESTAMP,doc.isSaved  = false WHERE "
			+ "doc.gstnBatchId = :gstnBatchId")
	public void markDocsAsErrorForBatch(@Param("gstnBatchId") Long gstnBatchId);

	@Query("SELECT doc FROM Gstr1vsEinvReconRespProcessedEntity doc "
			+ "WHERE doc.docKeyEinv = :docKey AND doc.isDelete = false ")
	public List<Gstr1vsEinvReconRespProcessedEntity> findDocsByDocKey(
			@Param("docKey") String docKey);

	@Query("SELECT doc.docKeyEinv FROM Gstr1vsEinvReconRespProcessedEntity doc WHERE "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.cgstinEinv =:cgstin")
	public List<String> findDocKeysByBatchIdAndCtin(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("cgstin") String cgstin);

	// and doc.isDelete = false is removed from below Query because this is
	// already been selected and sent to Gstn So even if it is deleted update
	// should happen.
	@Modifying
	@Query("UPDATE Gstr1vsEinvReconRespProcessedEntity doc SET doc.isSaved = true,"
			+ "doc.savedToGSTNDate = CURRENT_TIMESTAMP, doc.isGstnError = false WHERE "
			+ "doc.docKeyEinv NOT IN (:erroredDocKeyList) AND doc.gstnBatchId ="
			+ " :gstnBatchId")
	public void markDocsAsSavedForBatchByErroredDocKeys(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("erroredDocKeyList") List<String> erroredDocKeyList);

	public List<Gstr1vsEinvReconRespProcessedEntity> findByGstnBatchIdAndDocKeyEinvIn(
			Long gstnBatchId, List<String> docKey);

	@Query(value = "select SGSTIN_SR,REPORT_CATEGORY,count(*)  AS  REPORT_COUNT"
			+ " FROM TBL_EINV_RECON_RESP_PSD WHERE REPORT_CATEGORY in (3,4,5) "
			+ "AND  is_processed = true AND  SGSTIN_SR in (:sgstinList) AND "
			+ "RET_PERIOD_SR=:returnPeriodSR AND IS_DELETE=false "
			+ "group by SGSTIN_SR,REPORT_CATEGORY", nativeQuery = true)
	public List<Object[]> getGstr1vsEinvReportCountSR(
			@Param("sgstinList") List<String> gstnsList,
			@Param("returnPeriodSR") String returnPer);

	@Query(value = "select SGSTIN_EINV,REPORT_CATEGORY,count(*)  AS  REPORT_COUNT"
			+ " FROM TBL_EINV_RECON_RESP_PSD WHERE REPORT_CATEGORY = 1 "
			+ "AND  is_processed = true AND  SGSTIN_EINV in (:sgstinList) AND "
			+ "RET_PERIOD_EINV=:returnPeriodEinv AND IS_DELETE=false "
			+ "group by SGSTIN_EINV, REPORT_CATEGORY", nativeQuery = true)
	public List<Object[]> getGstr1vsEinvReportCountEinv(
			@Param("sgstinList") List<String> gstnsList,
			@Param("returnPeriodEinv") String returnPer);

	@Query(value = "SELECT IFNULL(SGSTIN_SR,SGSTIN_EINV) AS GSTIN, "
			+ "REPORT_CATEGORY,COUNT(*) AS REPORT_COUNT "
			+ "FROM TBL_EINV_RECON_RESP_PSD WHERE REPORT_CATEGORY = 6 "
			+ "AND IS_PROCESSED = TRUE AND IS_DELETE=FALSE "
			+ "AND IFNULL(SGSTIN_SR,SGSTIN_EINV) IN (:sgstinList) "
			+ "AND IFNULL(RET_PERIOD_SR,RET_PERIOD_EINV)=:returnPeriod "
			+ "AND TRIM(USER_RESPONSE) IS NOT NULL "
			+ "GROUP BY IFNULL(SGSTIN_SR,SGSTIN_EINV),REPORT_CATEGORY", nativeQuery = true)
	public List<Object[]> getGstr1vsEinvDeleteCount(
			@Param("sgstinList") List<String> gstnsList,
			@Param("returnPeriod") String returnPer);

}
