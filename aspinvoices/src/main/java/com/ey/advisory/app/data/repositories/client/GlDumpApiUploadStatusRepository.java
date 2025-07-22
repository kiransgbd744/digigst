package com.ey.advisory.app.data.repositories.client;

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

import com.ey.advisory.app.data.entities.client.GlreconDumpApiUploadStatusEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("GlDumpApiUploadStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GlDumpApiUploadStatusRepository
		extends JpaRepository<GlreconDumpApiUploadStatusEntity, String>,
		JpaSpecificationExecutor<GlreconDumpApiUploadStatusEntity> {

	@Modifying
	@Query("UPDATE GlreconDumpApiUploadStatusEntity file SET file.apiStatus = "
			+ ":apiStatus where file.refId = :refId ")
	public void updateFileStatus(@Param("refId") String refId,
			@Param("apiStatus") String apiStatus);

	@Modifying
	@Query("UPDATE GlreconDumpApiUploadStatusEntity file SET file.errorDesc = "
			+ ":errorDesc where file.refId = :refId ")
	public void updateErrorStatus(@Param("refId") String refId,
			@Param("errorDesc") String apiStatus);
	
	public Optional<GlreconDumpApiUploadStatusEntity> findByRefId(String refId);

	public List<GlreconDumpApiUploadStatusEntity> findByCreatedByIn(
			List<String> createdBy, Pageable pageReq);

	@Query(" Select count(*) from GlreconDumpApiUploadStatusEntity ")
	public int getCount();
}
