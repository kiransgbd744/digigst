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

import com.ey.advisory.app.data.entities.client.EinvReconReportDownloadEntity;

/**
 * @author Rajesh N K
 *
 */

@Repository("EinvReconReportDownloadRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface EinvReconReportDownloadRepository
		extends JpaRepository<EinvReconReportDownloadEntity, Long>,
		JpaSpecificationExecutor<EinvReconReportDownloadEntity> {

	EinvReconReportDownloadEntity findByConfigIdAndReportType(Long configId,
			String reportType);

	@Modifying
	@Query("UPDATE EinvReconReportDownloadEntity SET isDownloadable = false WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateAndSave(@Param("configId") Long configId,
			@Param("reportType") String reportType);

	@Modifying
	@Query("UPDATE EinvReconReportDownloadEntity SET "
			+ " filePath =:filePath, isDownloadable = true, docId =:docId WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateReconFilePath(@Param("filePath") String filePath,
			@Param("configId") Long configId,
			@Param("reportType") String reportType,@Param("docId") String docId);

	@Query("Select e.reportType, e.isDownloadable, e.filePath, e.docId"
			+ " from EinvReconReportDownloadEntity e where e.configId =:configId")
	public List<Object[]> getDataList(@Param("configId") Long configId);

}
