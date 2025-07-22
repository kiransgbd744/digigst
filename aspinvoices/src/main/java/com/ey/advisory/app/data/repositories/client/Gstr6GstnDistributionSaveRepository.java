package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr6DistributionEntity;

/**
 * 
 * @author Sri Bhavya
 *
 */
@Repository("Gstr6GstnDistributionSaveRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr6GstnDistributionSaveRepository
		extends CrudRepository<Gstr6DistributionEntity, Long> {
	@Modifying
	@Query("UPDATE Gstr6DistributionEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentToGstn=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,"
			+ "doc.modifiedOn = :now WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids, 
			@Param("now") LocalDateTime now);
	
	@Modifying
	@Query("UPDATE Gstr6DistributionEntity doc SET doc.isSaveToGstn = true,"
			+ "doc.modifiedOn = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP "
			+ "WHERE doc.gstnBatchId = :gstnBatchId AND doc.isDelete = false")
	public void markDocsAsSavedForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("now") LocalDateTime now);
	
	@Modifying
	@Query("UPDATE Gstr6DistributionEntity doc SET doc.isError = true,"
			+ "doc.modifiedOn = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP WHERE "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.isDelete = false")
	public void markDocsAsErrorForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("now") LocalDateTime now);
	
	@Query("SELECT doc.id, doc.processKey FROM Gstr6DistributionEntity doc WHERE "
			+ "doc.gstnBatchId = :gstnBatchId and doc.isDelete = false")
	public List<Object[]> findDocIdsByBatchId(@Param("gstnBatchId") Long id);
	
	
	@Query("SELECT doc.id, doc.processKey FROM Gstr6DistributionEntity doc WHERE "
			+ "doc.processKey IN (:invKeyList) AND doc.gstnBatchId = :gstnBatchId "
			+ "and doc.isDelete = false")
	public List<Object[]> findDocIdsForBatchByDocKeys(
			@Param("gstnBatchId") Long id,
			@Param("invKeyList") List<String> invKeyList);
	
	@Modifying
	@Query("UPDATE Gstr6DistributionEntity doc SET doc.isError = true,"
			+ "doc.modifiedOn = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP, "
			+ " doc.isSaveToGstn  = false WHERE "
			+ "doc.processKey IN (:invKeyList) AND doc.gstnBatchId = :gstnBatchId "
			+ "AND doc.isDelete = false")
	public void markDocsAsErrorsForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("invKeyList") List<String> invKeyList,
			@Param("now") LocalDateTime now);
	
	@Modifying
	@Query("UPDATE Gstr6DistributionEntity doc SET doc.isSaveToGstn = true,"
			+ "doc.modifiedOn = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP, "
			+ "doc.isError = false WHERE "
			+ "doc.processKey NOT IN (:erroredDocKeyList) AND "
			+ "doc.gstnBatchId = :gstnBatchId ")
	public void markDocsAsSavedForBatchByErroredDocKeys(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("erroredDocKeyList") List<String> erroredDocKeyList,
			@Param("now") LocalDateTime now);
	
	@Query("SELECT doc FROM Gstr6DistributionEntity doc "
			+ "WHERE doc.processKey = :docKey "
			+ " ORDER BY doc.id DESC ")
	public List<Gstr6DistributionEntity> findDocsByDocKey(
			@Param("docKey") String docKey);
	
	@Query("SELECT doc.processKey FROM Gstr6DistributionEntity doc WHERE "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.recipientGSTIN =:cgstin")
	public List<String> findDocKeysByBatchIdAndCtin(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("cgstin") String cgstin);

	@Query("SELECT doc FROM Gstr6DistributionEntity doc "
			+ "WHERE doc.processKey IN (:invKeySetAsList) AND "
			+ " doc.gstnBatchId = :gstnBatchId ")
	public List<Gstr6DistributionEntity> findByGstnBatchIdAndDocKeyIn(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("invKeySetAsList") List<String> invKeySetAsList);
	
	@Query("SELECT doc.processKey FROM Gstr6DistributionEntity doc "
			+ "WHERE doc.gstnBatchId = :gstnBatchId "
			+ " AND doc.docNum = :docNum "
			+ " AND doc.isdGstin = :isdGstin "
			+ " AND doc.returnPeriod = :returnPeriod")
	public String findDocKeyByBatchID(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("docNum") String docNum,
			@Param("isdGstin") String isdGstin,
			@Param("returnPeriod") String returnPeriod);	
	
	@Modifying
	@Query("UPDATE Gstr6DistributionEntity doc SET doc.isSubmitted=true,"
			+ "doc.submittedDate =:modifiedDate,"
			+ "doc.modifiedOn=:modifiedOn "
			+ "WHERE doc.returnPeriod =:returnPeriod AND "
			+ "doc.isdGstin =:isdGstin AND doc.isDelete = false")
	public void markSubmittedAsTrue(
			@Param("returnPeriod") String taxperiod,
			@Param("isdGstin") String sgstin,
			@Param("modifiedDate") LocalDate modifiedDate,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	
}
