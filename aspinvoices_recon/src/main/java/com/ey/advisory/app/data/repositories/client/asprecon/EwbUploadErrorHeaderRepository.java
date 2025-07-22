package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadErrorHeaderEntity;

@Repository("EwbUploadErrorHeaderRepository")
public interface EwbUploadErrorHeaderRepository
		extends CrudRepository<EwbUploadErrorHeaderEntity, Long>,
		JpaSpecificationExecutor<EwbUploadErrorHeaderEntity>{
	
	@Modifying
	@Query("Update EwbUploadErrorHeaderEntity SET isDelete = true  WHERE "
			+ "docKey in (:docKeyList) AND isDelete = false")
		int updateIsDeleteFlag(
				@Param("docKeyList")List<String> docKeyList);
	
	public List<EwbUploadErrorHeaderEntity> findByFileId(Long fileId);

}
