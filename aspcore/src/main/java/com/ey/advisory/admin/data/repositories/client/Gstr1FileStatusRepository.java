package com.ey.advisory.admin.data.repositories.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("Gstr1FileStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1FileStatusRepository
		extends JpaRepository<Gstr1FileStatusEntity, Long>,
		JpaSpecificationExecutor<Gstr1FileStatusEntity> {

	@Query("FROM Gstr1FileStatusEntity file WHERE file.fileName = :oriFileName ")
	public List<Gstr1FileStatusEntity> getFileStatusName(
			@Param("oriFileName") String oriFileName);

	public List<Gstr1FileStatusEntity> findByFileType(String fileType,
			Pageable pageReq);

	@Query("FROM Gstr1FileStatusEntity file WHERE file.fileName = :oriFileName ")
	public Gstr1FileStatusEntity getFileName(
			@Param("oriFileName") String oriFileName);

	@Query("FROM Gstr1FileStatusEntity file WHERE file.id = :id ")
	public List<Gstr1FileStatusEntity> findByIds(@Param("id") Long id);

	@Query("SELECT file.fileType, file.dataType FROM Gstr1FileStatusEntity file "
			+ "WHERE file.id = :id ")
	public List<Object[]> getTypes(@Param("id") Long id);

	@Query("SELECT file.dataType FROM Gstr1FileStatusEntity file "
			+ "WHERE file.id = :id ")
	public String getDatatype(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.errorFileName = "
			+ ":errorFileName  where file.id = :id")
	public void updateErrorFieNameById(@Param("id") Long id,
			@Param("errorFileName") String errorFileName);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.total =:totalCount, "
			+ "file.processed =:processedCount,file.error =:errorCount"
			+ " where file.id =:id ")
	public void updateCountSummary(@Param("id") Long id,
			@Param("totalCount") Integer totalCount,
			@Param("processedCount") Integer processedCount,
			@Param("errorCount") Integer errorCount);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.fileStatus = "
			+ ":fileStatus, file.modifiedOn = CURRENT_TIMESTAMP where file.id = :id ")
	public void updateFileStatus(@Param("id") Long id,
			@Param("fileStatus") String fileStatus);

	@Query("SELECT file.fileName FROM Gstr1FileStatusEntity file "
			+ "WHERE file.id = :id ")
	public String getFileName(@Param("id") Long id);

	@Query("SELECT count(*) FROM Gstr1FileStatusEntity file "

			+ "WHERE file.fileType = :fileType ")

	public int findCountByFileType(@Param("fileType") String fileType);

	@Query("FROM Gstr1FileStatusEntity file WHERE file.fileHash = :fileHash ")
	public List<Gstr1FileStatusEntity> getFileHash(
			@Param("fileHash") String fileHash);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.total =:totalCount, "
			+ "file.processed =:processedCount,file.error =:errorCount,file.fileStatus = :fileStatus "
			+ " where file.id =:id ")
	public int updateDuplicateFile(@Param("id") Long id,
			@Param("totalCount") Integer totalCount,
			@Param("processedCount") Integer processedCount,
			@Param("errorCount") Integer errorCount,
			@Param("fileStatus") String fileStatus);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.fileHash = "
			+ ":fileHash where file.id = :id ")
	public int updateFileHashById(@Param("id") Long id,
			@Param("fileHash") String fileStatus);

	@Query("SELECT file.id FROM Gstr1FileStatusEntity file "
			+ "WHERE file.fileStatus = 'Processed' AND "
			+ "file.source = 'SFTP_WEB_UPLOAD' AND file.isRevIntegration = FALSE "
			+ "AND file.isChildCreated = FALSE AND file.dataType = 'OUTWARD' ")
	public List<Long> getIdBySource();

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.isRevIntegration = TRUE "
			+ "where file.id = :id AND file.isRevIntegration = FALSE")
	public void updateRevIntFlagToTrue(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.isChildCreated = TRUE "
			+ "where file.id = :id AND file.isChildCreated = FALSE")
	public void updateChildCreatedFlagToTrue(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.isChildCreated = FALSE "
			+ "where file.id = :id AND file.isChildCreated = TRUE")
	public void updateChildCreatedFlagToFalse(@Param("id") Long id);

	@Query("SELECT file from Gstr1FileStatusEntity file where file.fileType = :fileType"
			+ " AND file.receivedDate between :dataRecvFrom AND :dataRecvTo"
			+ " ORDER BY file.id DESC")
	public List<Gstr1FileStatusEntity> getRecipientMasterStatus(
			@Param("fileType") String fileType,
			@Param("dataRecvFrom") LocalDate dataRecvFrom,
			@Param("dataRecvTo") LocalDate dataRecvTo);

	public Optional<Gstr1FileStatusEntity> findTop1ByFileTypeOrderByIdDesc(
			String fileType);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.fileStatus = "
			+ ":fileStatus, file.errorDesc =:errorDesc, file.total = 0, file.processed = 0, file.error = 0 "
			+ " where file.id =:id ")
	public void updateFailedStatus(@Param("id") Long id,
			@Param("errorDesc") String errMsg,
			@Param("fileStatus") String fileStatus);

	@Query(" SELECT id from Gstr1FileStatusEntity where fileStatus IN ('Uploaded','InProgress') and fileHash =:fileHash ")
	public List<Long> getLogicalFileIds(@Param("fileHash") String fileHash);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.fileStatus = "
			+ ":fileStatus, file.total =:processedCount where file.id = :id ")
	public void updateFileStatusProcessed(@Param("id") Long id,
			@Param("fileStatus") String fileStatus,
			@Param("processedCount") Integer processedCount);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.processedRptId =:requestId "
			+ " where file.id = :id ")
	public void updateProcessedId(@Param("id") Long id,
			@Param("requestId") Long requestId);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.errorRptId =:requestId "
			+ " where file.id = :id ")
	public void updateErrorId(@Param("id") Long id,
			@Param("requestId") Long requestId);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.infoRptId =:requestId "
			+ " where file.id = :id ")
	public void updateInfoId(@Param("id") Long id,
			@Param("requestId") Long requestId);

	@Query("FROM Gstr1FileStatusEntity file WHERE file.docId = :docId ")
	Optional<Gstr1FileStatusEntity> findByDocId(@Param("docId") String docId);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.req =:req "
			+ " where file.id = :id ")
	public void updateReqPayload(@Param("id") Long id,
			@Param("req") String req);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET file.fileStatus = "
			+ ":fileStatus, file.modifiedOn = CURRENT_TIMESTAMP, file.errorDesc =:errorDesc"
			+ " where file.id = :id ")
	public void updateFileStatusAndErrorDesc(@Param("id") Long id,
			@Param("fileStatus") String fileStatus,
			@Param("errorDesc") String errorDesc);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity f SET f.docId =:docId, "
			+ " f.modifiedOn = CURRENT_TIMESTAMP where f.id = :id ")
	public void updateDocId(@Param("id") Long id, @Param("docId") String docId);
	
	@Query(" SELECT file from Gstr1FileStatusEntity file where file.fileType In (:fileType) "
			+ " and fileStatus IN ('Uploaded','InProgress','Job_Posted')")
	public List<Gstr1FileStatusEntity> getFileBasedonFileType(@Param("fileType") List<String> fileType);

	@Query(" SELECT file from Gstr1FileStatusEntity file where file.source=:source "
			+ " and fileStatus IN ('Uploaded','InProgress','Job_Posted')")
	public List<Gstr1FileStatusEntity> getFileBasedonSource(@Param("source") String source);
	
	
	@Query("FROM Gstr1FileStatusEntity file WHERE file.id = :id ")
	public Optional<Gstr1FileStatusEntity> findById(@Param("id") Long id);
	
	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET "
			+ " file.transformationStatus =:transformationStatus, "
			+ " file.modifiedOn = CURRENT_TIMESTAMP where file.id = :id ")
	public void updateTransformationStatus(@Param("id") Long id,
			@Param("transformationStatus") String transformationStatus);
	
	@Query(" SELECT file from Gstr1FileStatusEntity file where file.uuid =:uuid ")
	public Optional<Gstr1FileStatusEntity> getFileBasedonUuid(@Param("uuid") String uuid);
	
	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity f SET f.docId =:docId, "
			+ " f.modifiedOn = CURRENT_TIMESTAMP,f.fileStatus =:fileStatus where f.id = :id ")
	public void updateDocIdAndStatus(@Param("id") Long id, @Param("docId") String docId,
			@Param("fileStatus") String fileStatus);
	
	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity file SET "
			+ " file.errorDesc =:errorDesc "
			+ " where file.id = :id ")
	public void updateErrorDesc(@Param("id") Long id,
			@Param("errorDesc") String errorDesc);
}
