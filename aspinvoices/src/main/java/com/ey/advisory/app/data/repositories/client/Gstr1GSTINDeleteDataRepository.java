/**
 * 
 */
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

import com.ey.advisory.app.data.entities.client.Gstr1GSTINDeleteDataEntity;

/**
 * @author siva.reddy
 *
 */

@Repository("Gstr1GSTINDeleteDataRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr1GSTINDeleteDataRepository
		extends JpaRepository<Gstr1GSTINDeleteDataEntity, Long>,
		JpaSpecificationExecutor<Gstr1GSTINDeleteDataEntity> {

	@Query("SELECT doc.id,doc.sgstin,doc.returnPeriod,doc.cgstin,doc.documentNumber,"
			+ "doc.documentDate,doc.documentType,doc.tableType FROM Gstr1GSTINDeleteDataEntity"
			+ " doc WHERE doc.sgstin =:sgstin AND doc.returnPeriod = :returnPeriod AND "
			+ "doc.tableType IN (:tableType) AND doc.isProcessed = true AND "
			+ "doc.isDelete = false AND doc.isSent = false")
	public List<Object[]> findDocsByGstinAndRetPeriodAndTableTypes(
			@Param("sgstin") String sgstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("tableType") List<String> tableType);

	@Modifying
	@Query("UPDATE Gstr1GSTINDeleteDataEntity doc SET doc.gstnBatchId=:gstnBatchId,"
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
	@Query("UPDATE Gstr1GSTINDeleteDataEntity doc SET doc.isSaved  = true,"
			+ "doc.savedToGSTNDate = CURRENT_TIMESTAMP,doc.isGstnError = false "
			+ "WHERE doc.gstnBatchId = :gstnBatchId")
	public void markDocsAsSavedForBatch(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE Gstr1GSTINDeleteDataEntity doc SET doc.isGstnError = true,"
			+ "doc.savedToGSTNDate = CURRENT_TIMESTAMP,doc.isSaved  = false WHERE "
			+ "doc.gstnBatchId = :gstnBatchId")
	public void markDocsAsErrorForBatch(@Param("gstnBatchId") Long gstnBatchId);

	@Query("SELECT doc FROM Gstr1GSTINDeleteDataEntity doc "
			+ "WHERE doc.docKey = :docKey AND doc.isDelete = false ")
	public List<Gstr1GSTINDeleteDataEntity> findDocsByDocKey(
			@Param("docKey") String docKey);

	@Query("SELECT doc.docKey FROM Gstr1GSTINDeleteDataEntity doc WHERE "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.cgstin =:cgstin")
	public List<String> findDocKeysByBatchIdAndCtin(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("cgstin") String cgstin);

	// and doc.isDelete = false is removed from below Query because this is
	// already been selected and sent to Gstn So even if it is deleted update
	// should happen.
	@Modifying
	@Query("UPDATE Gstr1GSTINDeleteDataEntity doc SET doc.isSaved = true,"
			+ "doc.savedToGSTNDate = CURRENT_TIMESTAMP, doc.isGstnError = false WHERE "
			+ "doc.docKey NOT IN (:erroredDocKeyList) AND doc.gstnBatchId ="
			+ " :gstnBatchId")
	public void markDocsAsSavedForBatchByErroredDocKeys(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("erroredDocKeyList") List<String> erroredDocKeyList);

	public List<Gstr1GSTINDeleteDataEntity> findByGstnBatchIdAndDocKeyIn(
			Long gstnBatchId, List<String> docKey);

	@Modifying
	@Query("UPDATE Gstr1GSTINDeleteDataEntity log SET log.isDelete = true,log.updatedDate = CURRENT_TIMESTAMP"
			+ " WHERE log.docKey in :docKey")
	int updateIsDeleted(@Param("docKey") List<String> docKey);

	public List<Gstr1GSTINDeleteDataEntity> findByFileIdAndIsProcessedFalse(
			Integer fileId);

	public List<Gstr1GSTINDeleteDataEntity> findByFileIdAndIsProcessedTrue(
			Integer fileId);

	
	
}
