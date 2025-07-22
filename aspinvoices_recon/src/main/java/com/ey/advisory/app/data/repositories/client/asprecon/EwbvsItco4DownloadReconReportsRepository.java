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

import com.ey.advisory.app.reconewbvsitc04.EwbvsItc04ReconDownloadReportsEntity;

/**
 * @author Ravindra V S
 *
 */

@Repository("EwbvsItco4DownloadReconReportsRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface EwbvsItco4DownloadReconReportsRepository
		extends JpaRepository<EwbvsItc04ReconDownloadReportsEntity, Long>,
		JpaSpecificationExecutor<EwbvsItc04ReconDownloadReportsEntity > {

	@Modifying
	@Query("UPDATE EwbvsItc04ReconDownloadReportsEntity SET "
			+ " path =:filePath, isDownloadable = true WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateReconFilePath(@Param("filePath") String filePath,
			@Param("configId") Long configId,
			@Param("reportType") String reportType);
	
	@Modifying
	@Query("UPDATE EwbvsItc04ReconDownloadReportsEntity SET "
			+ " path =:filePath, isDownloadable = true, docId =:docId WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateReconFilePath(@Param("filePath") String filePath,
			@Param("configId") Long configId,
			@Param("reportType") String reportType,
			@Param("docId") String docId);
	
	@Modifying
	@Query("UPDATE EwbvsItc04ReconDownloadReportsEntity SET isDownloadable = false WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateAndSave(@Param("configId") Long configId,
			@Param("reportType") String reportType);
	
	
	@Query("Select configEntity from EwbvsItc04ReconDownloadReportsEntity configEntity where"
			+ " configId =:configId")
	public EwbvsItc04ReconDownloadReportsEntity findByConfigId(@Param("configId") Long configId);

	@Query("from EwbvsItc04ReconDownloadReportsEntity  where configId =:configId")
	public List<EwbvsItc04ReconDownloadReportsEntity> getDataList(@Param("configId") 
								Long configId);

	Optional <EwbvsItc04ReconDownloadReportsEntity> findByConfigIdAndReportType(Long configId, String reportType);
	
}