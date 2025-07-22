package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadProcessedHeaderEntity;

@Repository("EwbUploadProcessedHeaderRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EwbUploadProcessedHeaderRepository
		extends CrudRepository<EwbUploadProcessedHeaderEntity, Long>,
		JpaSpecificationExecutor<EwbUploadProcessedHeaderEntity>{

	@Modifying
	@Query("Update EwbUploadProcessedHeaderEntity SET isDelete = true  WHERE "
			+ "docKey in (:docKeyList) AND isDelete = false")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList);

	@Modifying
	@Query("Update EwbUploadProcessedHeaderEntity hdr SET hdr.isDelete = true,"
			+ " hdr.errorCode = :errorCode, hdr.errorDesc = :errorDesc  WHERE "
			+ "hdr.docKey in (:docKeyList) AND hdr.isDelete = false")
	int updateIsDeleteFlagDropOut(@Param("docKeyList") List<String> docKeyList,
			@Param("errorCode") String errCode,
			@Param("errorDesc") String errorDesc);

	public List<EwbUploadProcessedHeaderEntity> findByFileId(Long fileId);

	public Optional<EwbUploadProcessedHeaderEntity> findByDocKeyAndIsDeleteFalse(
			String docKey);

}
