package com.ey.advisory.app.data.repositories.client.asprecon;

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

import com.ey.advisory.app.itcmatching.vendorupload.VendorApiUploadStatusEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Repository("VendorApiUploadStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface VendorApiUploadStatusRepository
		extends JpaRepository<VendorApiUploadStatusEntity, String>,
		JpaSpecificationExecutor<VendorApiUploadStatusEntity> {

	@Modifying
	@Query("UPDATE VendorApiUploadStatusEntity file SET file.apiStatus = "
			+ ":apiStatus where file.refId = :refId ")
	public void updateFileStatus(@Param("refId") String refId,
			@Param("apiStatus") String apiStatus);

	public Optional<VendorApiUploadStatusEntity> findByRefId(String refId);

	public List<VendorApiUploadStatusEntity> findByCreatedByIn(List<String> createdBy,
			Pageable pageReq);

	@Query(" Select count(*) from VendorApiUploadStatusEntity ")
	public int getCount();
}
