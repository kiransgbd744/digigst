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

import com.ey.advisory.service.days.revarsal180.ITCReversal180ComputeRptDownloadEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("ITCReversal180ComputeRptDownloadRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface ITCReversal180ComputeRptDownloadRepository
		extends JpaRepository<ITCReversal180ComputeRptDownloadEntity, Long>,
		JpaSpecificationExecutor<ITCReversal180ComputeRptDownloadEntity> {

	@Modifying
	@Query("UPDATE ITCReversal180ComputeRptDownloadEntity SET "
			+ " filePath =:filePath, isDownloadable = true WHERE "
			+ "computeId =:computeId AND reportType =:reportType")
	public void updateReconFilePath(@Param("filePath") String filePath,
			@Param("computeId") Long computeId,
			@Param("reportType") String reportType);
	
	@Modifying
	@Query("UPDATE ITCReversal180ComputeRptDownloadEntity SET "
	        + "filePath = :filePath, isDownloadable = true, docId = :docId "
	        + "WHERE computeId = :computeId AND reportType = :reportType")
	public void updateReconFilePathDocId(
	        @Param("filePath") String filePath,
	        @Param("computeId") Long computeId,
	        @Param("reportType") String reportType,
	        @Param("docId") String docId);


	@Query("from ITCReversal180ComputeRptDownloadEntity  where"
			+ " computeId =:computeId AND reportType =:reportType")
	public ITCReversal180ComputeRptDownloadEntity findBycomputeId(@Param("computeId") 
					Long computeId, @Param("reportType") String reportType);
	
	
	@Query("from ITCReversal180ComputeRptDownloadEntity  where computeId =:computeId")
	public List<ITCReversal180ComputeRptDownloadEntity> getDataList(@Param("computeId") 
								Long computeId);
	
	@Query("SELECT reportType from ITCReversal180ComputeRptDownloadEntity  "
			+ "where computeId =:computeId ")
	public List<String> getAddlnReportTypeList(@Param("computeId") Long computeId);
	
	
	@Modifying
	@Query("UPDATE ITCReversal180ComputeRptDownloadEntity SET isDownloadable = false WHERE "
			+ "computeId =:computeId AND reportType =:reportType")
	public void updateAndSave(@Param("computeId") Long computeId,
			@Param("reportType") String reportType);
	
	
	@Query(" from ITCReversal180ComputeRptDownloadEntity WHERE computeId =:computeId  "
			+ "AND reportType =:reportType")
	public ITCReversal180ComputeRptDownloadEntity  getChunckSizeforReportType(@Param
			("computeId") Long computeId, @Param("reportType") String reportType);
	
	

}