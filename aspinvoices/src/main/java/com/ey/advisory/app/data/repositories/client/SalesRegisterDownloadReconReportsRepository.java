package com.ey.advisory.app.data.repositories.client;

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

import com.ey.advisory.service.gstr1.sales.register.SalesRegisterReconDownloadReportsEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("SalesRegisterDownloadReconReportsRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface SalesRegisterDownloadReconReportsRepository
		extends JpaRepository<SalesRegisterReconDownloadReportsEntity, Long>,
		JpaSpecificationExecutor<SalesRegisterReconDownloadReportsEntity > {

	@Modifying
	@Query("UPDATE SalesRegisterReconDownloadReportsEntity SET "
			+ " path =:filePath, isDownloadable = true WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateReconFilePath(@Param("filePath") String filePath,
			@Param("configId") Long configId,
			@Param("reportType") String reportType);
	
	@Modifying
	@Query("UPDATE SalesRegisterReconDownloadReportsEntity SET isDownloadable = false WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateAndSave(@Param("configId") Long configId,
			@Param("reportType") String reportType);
	
	
	@Query("Select configEntity from SalesRegisterReconDownloadReportsEntity configEntity where"
			+ " configId =:configId")
	public SalesRegisterReconDownloadReportsEntity findByConfigId(@Param("configId") Long configId);

	@Query("from SalesRegisterReconDownloadReportsEntity  where configId =:configId")
	public List<SalesRegisterReconDownloadReportsEntity> getDataList(@Param("configId") 
								Long configId);

	Optional <SalesRegisterReconDownloadReportsEntity> findByConfigIdAndReportType(Long configId, String reportType);
	
//	@Query("Select configEntity from SalesRegisterReconDownloadReportsEntity configEntity where"
//			+ " configId =:configId and reportType =:reportType")
//	public SalesRegisterReconDownloadReportsEntity findByConfigIdAndReportType(@Param("configId") Long configId,
//			String reportType); 
	
}