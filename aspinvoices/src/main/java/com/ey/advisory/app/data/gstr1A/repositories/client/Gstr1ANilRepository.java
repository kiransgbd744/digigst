package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilDetailsEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr1ANilRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1ANilRepository
		extends JpaRepository<Gstr1ANilDetailsEntity, Long>,
		JpaSpecificationExecutor<Gstr1ANilDetailsEntity> {

	@Modifying
	@Query("UPDATE Gstr1ANilDetailsEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND  b.nKey IN (:entityKey) ")
	public void UpdateSameInvKey(@Param("entityKey") List<String> entityKey);

	@Modifying
	@Query("UPDATE Gstr1ANilDetailsEntity b SET b.isDelete=TRUE "
			+ "WHERE b.isDelete=FALSE AND  b.id IN (:id) "
			+ "and b.returnPeriod = :taxPeriod and b.sgstin = :gstin and b.nKey IN (:docKey) ")
	public int UpdateListId(@Param("id") List<Long> id,
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("docKey") List<String> docKey);

	@Transactional
	@Modifying
	@Query("UPDATE Gstr1ANilDetailsEntity SET isDelete = true WHERE "
			+ " isDelete = false and id in (:id) and derivedRetPeriod = :taxPeriod"
			+ " and sgstin = :gstin and nKey = :docKey")
	public void updateAllfromDelete(@Param("id") Long id,
			@Param("taxPeriod") int taxPeriod, @Param("gstin") String gstin,
			@Param("docKey") String docKey);

	@Query("SELECT max(b.id) FROM Gstr1ANilDetailsEntity b "
			+ "WHERE b.isDelete= FALSE AND  b.nKey IN(:entityKey) and b.derivedRetPeriod = :taxPeriod"
			+ " and b.sgstin = :gstin  ")
	public long findByNKey(@Param("entityKey") String entityKey,
			@Param("taxPeriod") int taxPeriod, @Param("gstin") String gstin);

	@Modifying
	@Query("UPDATE Gstr1ANilDetailsEntity doc SET doc.isGstnError=true, "
			+ "doc.isSaved=false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE Gstr1ANilDetailsEntity doc SET doc.isSaved=true, "
			+ "doc.isGstnError =false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE Gstr1ANilDetailsEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.modifiedOn = "
			+ "CURRENT_TIMESTAMP WHERE doc.returnPeriod = :retPeriod AND doc.sgstin = :sgstin "
			+ "AND doc.isDelete = false AND doc.fileId <= :fileId")
	void updateSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("fileId") Long fileId);

}
