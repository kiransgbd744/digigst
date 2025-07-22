package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr2bpr.reconresponse.upload.GSTR2bprReportDwnldPathEntity;

/**
 * @author Jithendra.B
 *
 */
@Repository("Gstr2BPRPathRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface Gstr2BPRPathRepository
		extends JpaRepository<GSTR2bprReportDwnldPathEntity, Long>,
		JpaSpecificationExecutor<GSTR2bprReportDwnldPathEntity> {

	@Modifying
	@Query("UPDATE GSTR2bprReportDwnldPathEntity SET proceesedPath =:proceesedPath,"
			+ " errorPath =:errorPath, totalPath =:totalPath, fileId =:fileId, "
			+ "infoPath =:infoPath" + " where batchId =:batchId")
	void updatePath(@Param("batchId") Long batchId,
			@Param("proceesedPath") String proceesedPath,
			@Param("errorPath") String errorPath,
			@Param("totalPath") String totalPath, @Param("fileId") Long fileId,
			@Param("infoPath") String infoPath);

	@Query("from GSTR2bprReportDwnldPathEntity where fileId  =:fileId ")
	public GSTR2bprReportDwnldPathEntity findByFileId(
			@Param("fileId") Long fileId);
}
