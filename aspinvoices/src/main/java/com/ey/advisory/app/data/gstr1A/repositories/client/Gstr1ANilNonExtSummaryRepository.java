package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExmptSummaryEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr1ANilNonExtSummaryRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1ANilNonExtSummaryRepository
		extends JpaRepository<Gstr1ANilNonExmptSummaryEntity, Long>,
		JpaSpecificationExecutor<Gstr1ANilNonExmptSummaryEntity> {

	@Modifying
	@Query("UPDATE Gstr1ANilNonExmptSummaryEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND  b.nKey IN (:entityKey) ")
	public void UpdateSameInvKey(@Param("entityKey") List<String> entityKey);

	@Query("SELECT doc FROM Gstr1ANilNonExmptSummaryEntity doc "
			+ "WHERE doc.fileId = :id  ")
	public List<Gstr1ANilNonExmptSummaryEntity> findByFileId(
			@Param("id") Long id);

	@Transactional
	@Modifying
	@Query("UPDATE Gstr1ANilNonExmptSummaryEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND  b.asProcessedId = :id "
			+ "and b.returnPeriod = :taxPeriod and b.sgstin = :gstin and b.nKey =:docKey ")
	public void updateAllfromDelete(@Param("id") Long id,
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("docKey") String docKey);

	@Modifying
	@Query("UPDATE Gstr1ANilNonExmptSummaryEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE  and  b.asProcessedId IN (:id) "
			+ "and b.returnPeriod = :taxPeriod and b.sgstin = :gstin and b.nKey IN (:docKey) ")
	public int UpdateListId(@Param("id") List<Long> id,
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("docKey") List<String> docKey);

	@Query("SELECT doc FROM Gstr1ANilNonExmptSummaryEntity doc "
			+ "WHERE doc.asProcessedId = :id  ")
	public List<Gstr1ANilNonExmptSummaryEntity> findByProcessId(
			@Param("id") Long id);

	@Modifying
	@Query("UPDATE Gstr1ANilNonExmptSummaryEntity doc SET doc.isGstnError=true, "
			+ "doc.isSaved=false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE Gstr1ANilNonExmptSummaryEntity doc SET doc.isSaved=true, "
			+ "doc.isGstnError =false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE Gstr1ANilNonExmptSummaryEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.modifiedOn = "
			+ "CURRENT_TIMESTAMP WHERE doc.returnPeriod = :retPeriod AND doc.sgstin = :sgstin "
			+ "AND doc.isDelete = false AND doc.id <= :vMaxId")
	void updateSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("vMaxId") Long vMaxId);

	@Query("SELECT COUNT(*) FROM Gstr1ANilNonExmptSummaryEntity  WHERE  "
			+ " isDelete = false and sgstin=:sgstin and returnPeriod=:returnPeriod")
	public int isNilDataAvail(@Param("sgstin") String sgstin,
			@Param("returnPeriod") String returnPeriod);

}
