package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr2XProcessedTcsTdsEntity;

/**
 * 
 * @author SriBhavya
 *
 */

@Repository("Gstr2XProcessedTcsTdsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2XProcessedTcsTdsRepository
		extends JpaRepository<Gstr2XProcessedTcsTdsEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr2XProcessedTcsTdsEntity doc SET doc.isSavedToGstn = true,"
			+ "doc.modifiedOn = :now,doc.savedToGstnDate = CURRENT_TIMESTAMP "
			+ "WHERE doc.batchId = :gstnBatchId AND doc.isDelete = false")
	public void markDocsAsSavedForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Gstr2XProcessedTcsTdsEntity doc SET doc.gstnError = true,"
			+ "doc.modifiedOn = :now,doc.savedToGstnDate = CURRENT_TIMESTAMP WHERE "
			+ "doc.batchId = :gstnBatchId AND doc.isDelete = false")
	public void markDocsAsErrorForBatch(@Param("gstnBatchId") Long gstnBatchId,
			@Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Gstr2XProcessedTcsTdsEntity doc SET doc.isSavedToGstn = true,"
			+ "doc.modifiedOn = :now,doc.savedToGstnDate = CURRENT_TIMESTAMP, "
			+ "doc.gstnError = false WHERE "
			+ "doc.docKey NOT IN (:erroredDocKeyList) AND "
			+ "doc.batchId = :gstnBatchId ")
	public void markDocsAsSavedForBatchByErroredDocKeys(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("erroredDocKeyList") List<String> erroredDocKeyList,
			@Param("now") LocalDateTime now);

	@Query("SELECT doc FROM Gstr2XProcessedTcsTdsEntity doc "
			+ "WHERE doc.docKey IN (:invKeySetAsList) AND "
			+ " doc.batchId = :gstnBatchId ")
	public List<Gstr2XProcessedTcsTdsEntity> findByGstnBatchIdAndDocKeyIn(
			@Param("gstnBatchId") Long gstnBatchId,
			@Param("invKeySetAsList") List<String> invKeySetAsList);

	@Query("SELECT doc.id FROM Gstr2XProcessedTcsTdsEntity doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isDelete = false ")
	List<Long> findActiveDocsByDocKeys(@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Gstr2XProcessedTcsTdsEntity doc SET doc.isDelete=true,"
			+ "doc.modifiedOn =:updatedDate,doc.modifieddBy =:updatedBy "
			+ "WHERE doc.id IN (:ids) ")
	public void updateDuplicateDocDeletionByDocKeys(
			@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy);

	@Modifying
	@Query("UPDATE Gstr2XProcessedTcsTdsEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND  b.docKey = :docKey ")
	public void softDeleteSameDocKey(@Param("docKey") String docKey);

	@Modifying
	@Query("UPDATE Gstr2XProcessedTcsTdsEntity doc SET doc.batchId=:batchId,"
			+ "doc.isSentToGstn=true,doc.sentToGstnDate = CURRENT_TIMESTAMP,"
			+ "doc.modifiedOn = :now WHERE doc.id IN (:ids)")
	public void updateBatchId(@Param("batchId") Long batchId,
			@Param("ids") List<Long> ids, @Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Gstr2XProcessedTcsTdsEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND  b.psKey = :docKey ")
	public void softDeleteSamePsDocKey(@Param("docKey") String docKey);

	@Query("SELECT doc FROM Gstr2XProcessedTcsTdsEntity doc "
			+ "WHERE doc.docKey = :docKey AND doc.isDelete = false ")
	Gstr2XProcessedTcsTdsEntity findActiveDocByDocKey(@Param("docKey") String docKey);

}
