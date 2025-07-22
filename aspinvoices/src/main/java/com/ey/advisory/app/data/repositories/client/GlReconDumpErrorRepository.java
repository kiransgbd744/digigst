package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GlDumpErrorEntity;

@Repository("GlReconDumpErrorRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GlReconDumpErrorRepository
		extends JpaRepository<GlDumpErrorEntity, Long>,
		JpaSpecificationExecutor<GlDumpErrorEntity> {

	@Modifying
	@Query("Update GlDumpErrorEntity SET isDelete = true  WHERE "
			+ "docKey in (:docKeyList) AND isDelete = false")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList);

	public List<GlDumpErrorEntity> findAllByFileId(Long id);

}
