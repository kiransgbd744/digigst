package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.ReportDownloadPathEntity;

/**
 * @author vishal.verma
 *
 */
@Repository("Gstr2PathRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface Gstr2PathRepository
		extends JpaRepository<ReportDownloadPathEntity, Long>,
		JpaSpecificationExecutor<ReportDownloadPathEntity> {

	@Modifying
	@Query("UPDATE ReportDownloadPathEntity SET proceesedPath =:proceesedPath,"
			+ " errorPath =:errorPath, totalPath =:totalPath, fileId =:fileId, "
			+ "infoPath =:infoPath"
			+ " where batchId =:batchId")
	void updatePath(@Param("batchId") Long batchId,
			@Param("proceesedPath") String proceesedPath,
			@Param("errorPath") String errorPath,
			@Param("totalPath") String totalPath, @Param("fileId") Long fileId,
			@Param("infoPath") String infoPath);
	

	@Query("from ReportDownloadPathEntity where fileId  =:fileId ")
    ReportDownloadPathEntity findByFileId(@Param("fileId") Long fileId);

}
