package com.ey.advisory.app.data.repositories.client.qrcodevalidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("QRUploadFileStatusRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface QRUploadFileStatusRepo
		extends JpaRepository<QRUploadFileStatusEntity, Long> {

	Optional<QRUploadFileStatusEntity> findById(Long id);

	@Query("SELECT SUM(m.totUplDoc) FROM QRUploadFileStatusEntity m WHERE m.id IN (:ids)")
	int getTotalDocCount(@Param("ids") List<Long> id);

	@Query("SELECT SUM(m.errMatch) FROM QRUploadFileStatusEntity m WHERE m.id IN (:ids)")
	int getErrDocCount(@Param("ids") List<Long> id);

	@Modifying
	@Query("UPDATE QRUploadFileStatusEntity log SET log.fileStatus = :fileStatus "
			+ "WHERE  log.id = :id ")
	void updateFileStatus(@Param("fileStatus") String fileStatus,
			@Param("id") Long id);

	@Modifying
	@Query("UPDATE QRUploadFileStatusEntity log SET log.totUplDoc = :totUplDoc,log.procQr = :procQr, "
			+ "log.sigMisMat = :sigMisMat,log.fullMatch = :fullMatch, log.errMatch = :errMatch, "
			+ "log.startTime = :startTime, log.endTime = :endTime "
			+ "WHERE log.id = :id")
	void updateCount(@Param("totUplDoc") int fileStatus,
			@Param("procQr") int processedQR,
			@Param("sigMisMat") int signatureMisMatch,
			@Param("fullMatch") int fullMatch, @Param("errMatch") int errMatch,
			@Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime, @Param("id") Long id);

	@Query("SELECT doc from QRUploadFileStatusEntity doc "
			+ " WHERE doc.source = 'SFTP' AND doc.filePath IS NOT NULL AND "
			+ " doc.fileStatus = 'COMPLETED' AND doc.isReverseInt = false")
	List<QRUploadFileStatusEntity> getAllReverseIntgRecords();

	@Modifying
	@Query("UPDATE QRUploadFileStatusEntity SET isReverseInt = true "
			+ " WHERE id IN :ids")
	void updateReverseIntegrated(@Param("ids") Set<Long> ids);

/*	@Query("SELECT q.fileStatus FROM QRUploadFileStatusEntity q WHERE q.id = :fileId")
	String findFileStatusByFileId(@Param("fileId") Long fileId);*/
	
	@Query("SELECT q.fileStatus FROM QRUploadFileStatusEntity q WHERE q.id = :fileId AND q.optionOpted = :optionOpted")
	String findFileStatusByFileIdAndOptionOpted(@Param("fileId") Long fileId, @Param("optionOpted") String optionOpted);
	
	@Query("SELECT f FROM QRUploadFileStatusEntity f WHERE f.entityId = :entityId OR f.entityId IS NULL")
	List<QRUploadFileStatusEntity> findByEntityIdOrEntityIdIsNull(@Param("entityId") Long entityId,Pageable pageReq);
	
	@Query("SELECT count(f) FROM QRUploadFileStatusEntity f WHERE f.entityId = :entityId OR f.entityId IS NULL")
	int countByEntityIdOrEntityIdIsNull(@Param("entityId") Long entityId);
	
	@Modifying
	@Query("UPDATE QRUploadFileStatusEntity log SET log.entityId = :entityId "
			+ "WHERE  log.id = :id ")
	void updateEntityId(@Param("entityId") Long entityId,
			@Param("id") Long id);
	
}
