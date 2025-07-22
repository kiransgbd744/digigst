package com.ey.advisory.app.data.repositories.client.gstr7;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr7ProcessedTdsEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("Gstr7ProcessedRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr7ProcessedRepository extends CrudRepository<Gstr7ProcessedTdsEntity, Long> {

	@Modifying
	@Query("update Gstr7ProcessedTdsEntity set isDelete = true where tdsInvKey in (:tdsInvKeys)")
	void updateSameInvKey(@Param("tdsInvKeys") List<String> tdsInvKeys);

	@Modifying
	@Query("UPDATE Gstr7ProcessedTdsEntity doc SET doc.isSavedGstn = true,"
			+ "doc.modifiedOn = :now,doc.savedGstnDate = CURRENT_TIMESTAMP "
			+ "WHERE doc.gstnBatchId = :id AND doc.isDelete = false")
	public void markDocsAsSavedForBatch(@Param("id") Long id, @Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Gstr7ProcessedTdsEntity doc SET doc.gstnError = true,"
			+ "doc.modifiedOn = :now,doc.savedGstnDate = CURRENT_TIMESTAMP WHERE "
			+ " doc.gstnBatchId = :id AND doc.isDelete = false")
	public void markDocsAsErrorForBatch(@Param("id") Long id, @Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Gstr7ProcessedTdsEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentGstn=true,doc.sentGstnDate = CURRENT_TIMESTAMP," + "doc.modifiedOn = :now WHERE doc.id IN (:ids)")
	public void updateBatchId(@Param("gstnBatchId") Long gstnBatchId, @Param("ids") List<Long> ids,
			@Param("now") LocalDateTime now);
	
	@Modifying
	@Query("UPDATE Gstr7ProcessedTdsEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentGstn=true,doc.sentGstnDate = CURRENT_TIMESTAMP,"
			+ "doc.modifiedOn = :modifiedOn WHERE doc.returnPeriod = :retPeriod "
			+ "AND doc.tdsGstin = :sgstin AND doc.tabNum = :section "
			+ "AND doc.isDelete = false AND doc.id <= :hMaxId AND (doc.actType != 'CAN' OR doc.actType IS NULL)")
	public void updateNewReturnsSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("section") String section,
			@Param("hMaxId") Long hMaxId,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	@Query("SELECT doc FROM Gstr7ProcessedTdsEntity doc "
			+ " WHERE doc.gstinkey = :docKey AND doc.isDelete = false ")
	public List<Gstr7ProcessedTdsEntity> findDocsByDocKey(
			@Param("docKey") String docKey);
	
	@Query("SELECT doc.gstinkey FROM Gstr7ProcessedTdsEntity doc WHERE "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.tdsGstin =:cgstin")
	public List<String> findDocKeysByBatchIdAndCtin(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("cgstin") String cgstin);
	
	@Modifying
	@Query("UPDATE Gstr7ProcessedTdsEntity doc SET doc.gstnError = true,"
			+ "doc.modifiedOn = :now,doc.savedGstnDate = CURRENT_TIMESTAMP,"
			+ "doc.isSavedGstn  = false WHERE "
			+ "doc.gstinkey IN (:invKeyList) AND doc.gstnBatchId = :gstnBatchId "
			+ "AND doc.isDelete = false")
	public void markDocsAsErrorsForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("invKeyList") List<String> invKeyList,  
			@Param("now") LocalDateTime now);
	
	@Modifying
	@Query("UPDATE Gstr7ProcessedTdsEntity doc SET doc.isSavedGstn = true,"
			+ "doc.modifiedOn = :now,doc.savedGstnDate = CURRENT_TIMESTAMP, "
			+ "doc.gstnError = false WHERE "
			+ "doc.gstinkey NOT IN (:erroredDocKeyList) AND "
			+ "doc.gstnBatchId = :gstnBatchId AND doc.isDelete = false")
	public void markDocsAsSavedForBatchByErroredDocKeys(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("erroredDocKeyList") List<String> erroredDocKeyList,
			@Param("now") LocalDateTime now);

	
	@Query("SELECT doc FROM Gstr7ProcessedTdsEntity doc "
			+ "WHERE doc.gstinkey IN (:invKeySetAsList) AND "
			+ " doc.gstnBatchId = :gstnBatchId AND "
			+ " doc.isDelete = false ")
	public List<Gstr7ProcessedTdsEntity> findByGstnBatchIdAndDocKeyIn(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("invKeySetAsList") List<String> invKeySetAsList);
	
	
	@Query("SELECT doc.gstinkey FROM Gstr7ProcessedTdsEntity doc "
			+ "WHERE doc.gstnBatchId = :gstnBatchId AND "
			+ " doc.tdsGstin = :tdsGstin AND doc.tabNum = :tabNum")
	public List<String> findDocKeyByBatchID(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("tdsGstin") String tdsGstin,
			@Param("tabNum") String tabNum);
	
	
	@Modifying
	@Query("UPDATE Gstr7ProcessedTdsEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentGstn=true,doc.sentGstnDate = CURRENT_TIMESTAMP,"
			+ "doc.modifiedOn = :modifiedOn WHERE doc.returnPeriod = :retPeriod "
			+ "AND doc.tdsGstin = :sgstin AND doc.tabNum = :section "
			+ "AND doc.isDelete = false AND doc.id <= :hMaxId AND doc.actType = 'CAN'")
	public void updateNewReturnsSumryBatchIdForCan(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("section") String section,
			@Param("hMaxId") Long hMaxId,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	

	@Query("SELECT doc.tdsInvKey,isSubmitted FROM Gstr7ProcessedTdsEntity doc "
			+ "WHERE doc.tdsInvKey IN (:docKeys) AND doc.isDelete = false")
	public List<Object[]> findCancelDocsCountsByDocKeys(
			@Param("docKeys") List<String> docKeys);
	
	@Query("SELECT doc.returnPeriod FROM Gstr7ProcessedTdsEntity doc "
			+ "WHERE doc.tdsGstin IN (:tdsGstin)  and doc.actType= 'CAN' "
			+ "AND doc.isDelete = false")
	public List<String> ReturnPeriodBygstin(
			@Param("tdsGstin") String tdsGstin);
	
	@Query("SELECT count(*) FROM Gstr7ProcessedTdsEntity doc "
			+ "WHERE doc.gstnError= true AND doc.tdsGstin =:tdsGstin "
			+ " AND doc.returnPeriod =:returnPeriod "
			+ " AND doc.gstnBatchId =:batchId ")
	public Integer countOfBatchRecords(
			@Param("tdsGstin") String tdsGstin, 
			@Param("returnPeriod") String returnPeriod,
			@Param("batchId") Long batchId);
}
