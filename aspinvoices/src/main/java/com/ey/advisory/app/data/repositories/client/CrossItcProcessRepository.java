package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.CrossItcProcessEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Repository("CrossItcProcessRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface CrossItcProcessRepository
		extends JpaRepository<CrossItcProcessEntity, Long>,
		JpaSpecificationExecutor<CrossItcProcessEntity> {

	@Query("SELECT doc.id FROM CrossItcProcessEntity doc "
			+ "WHERE doc.crossItcDocKey IN (:docKeys) AND doc.isDelete = false ")
	List<Long> findActiveDocsByDocKeys(@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE CrossItcProcessEntity doc SET doc.isDelete=true,"
			+ "doc.modifiedOn =:updatedDate,doc.modifiedBy=:updatedBy "
			+ "WHERE doc.id IN (:ids) ")
	public void updateDuplicateDocDeletionByDocKeys(
			@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy);

	@Modifying
	@Query("UPDATE CrossItcProcessEntity doc SET doc.isSaved = true,"
			+ "doc.modifiedOn = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP, "
			+ "doc.gstnBatchId = :gstnBatchId,doc.isSent = true,"
			+ "doc.sentToGSTNDate= CURRENT_TIMESTAMP WHERE doc.isDelete = false")
	public void markDocsAsSavedForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("now") LocalDateTime now);
	
	@Modifying
	@Query("UPDATE CrossItcProcessEntity doc SET doc.isGstnError = true,"
			+ "doc.modifiedOn = :now,doc.savedToGSTNDate = CURRENT_TIMESTAMP,"
			+ "doc.gstnBatchId = :gstnBatchId,doc.isSent = true,"
			+ "doc.sentToGSTNDate= CURRENT_TIMESTAMP WHERE doc.isDelete = false")
	public void markDocsAsErrorForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("now") LocalDateTime now);
	
	// Deactivate DocKey Based On screen Request
	
	@Modifying
	@Query("UPDATE CrossItcProcessEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND  b.crossItcDocKey IN (:docKey) ")
	public void UpdateId(
			@Param("docKey") String docKey);
	
	// Finding Active Records.
	@Query("SELECT doc FROM CrossItcProcessEntity doc WHERE doc.isDelete = FALSE "
			+ "AND doc.crossItcDocKey IN (:docKey)")
	CrossItcProcessEntity findingActiveRecordsByDocKey(@Param("docKey") String docKey);
	
}
