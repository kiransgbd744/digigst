package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.EWB3WayReconDownloadReportsEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("EWB3WayDownloadReconReportsRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface EWB3WayDownloadReconReportsRepository
		extends JpaRepository<EWB3WayReconDownloadReportsEntity, Long>,
		JpaSpecificationExecutor<EWB3WayReconDownloadReportsEntity > {

	@Modifying
	@Query("UPDATE EWB3WayReconDownloadReportsEntity SET "
			+ " path =:filePath, isDownloadable = true WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateReconFilePath(@Param("filePath") String filePath,
			@Param("configId") Long configId,
			@Param("reportType") String reportType);
	
	@Modifying
	@Query("UPDATE EWB3WayReconDownloadReportsEntity SET isDownloadable = false WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateAndSave(@Param("configId") Long configId,
			@Param("reportType") String reportType);
	
	
	@Query("Select configEntity from EWB3WayReconDownloadReportsEntity configEntity where"
			+ " configId =:configId")
	public EWB3WayReconDownloadReportsEntity findByConfigId(@Param("configId") Long configId);

	@Query("from EWB3WayReconDownloadReportsEntity  where configId =:configId")
	public List<EWB3WayReconDownloadReportsEntity> getDataList(@Param("configId") 
								Long configId);

	Optional <EWB3WayReconDownloadReportsEntity> findByConfigIdAndReportType(Long configId, String reportType);
	
	@Modifying
	@Query("UPDATE EWB3WayReconDownloadReportsEntity SET "
			+ " path =:filePath, isDownloadable = true, docId =:docId WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateReconFilePath(@Param("filePath") String filePath,
			@Param("configId") Long configId,
			@Param("reportType") String reportType,@Param("docId") String docId);
	
}