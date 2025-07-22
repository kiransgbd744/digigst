package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.InwardEinvoiceReconAddlReportsEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("InwardEinvoiceReconAddlReportsRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface InwardEinvoiceReconAddlReportsRepository
		extends JpaRepository<InwardEinvoiceReconAddlReportsEntity, Long>,
		JpaSpecificationExecutor<InwardEinvoiceReconAddlReportsEntity> {

	@Modifying
	@Query("UPDATE InwardEinvoiceReconAddlReportsEntity SET "
			+ " filePath =:filePath, isDownloadable = true WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateReconFilePath(@Param("filePath") String filePath,
			@Param("configId") Long configId,
			@Param("reportType") String reportType);

	@Query("from InwardEinvoiceReconAddlReportsEntity  where"
			+ " configId =:configId AND reportType =:reportType")
	public InwardEinvoiceReconAddlReportsEntity findByConfigId(@Param("configId") 
					Long configId, @Param("reportType") String reportType);
	
	
	@Query("from InwardEinvoiceReconAddlReportsEntity  where configId =:configId")
	public List<InwardEinvoiceReconAddlReportsEntity> getDataList(@Param("configId") 
								Long configId);
	
	@Query("SELECT reportType from InwardEinvoiceReconAddlReportsEntity  "
			+ "where configId =:configId ")
	public List<String> getAddlnReportTypeList(@Param("configId") Long configId);
	
	
	@Modifying
	@Query("UPDATE InwardEinvoiceReconAddlReportsEntity SET isDownloadable = false WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateAndSave(@Param("configId") Long configId,
			@Param("reportType") String reportType);
	
	
	@Query(" from InwardEinvoiceReconAddlReportsEntity WHERE configId =:configId  "
			+ "AND reportType =:reportType")
	public InwardEinvoiceReconAddlReportsEntity  getChunckSizeforReportType(@Param
			("configId") Long configId, @Param("reportType") String reportType);
	
	@Query("from InwardEinvoiceReconAddlReportsEntity  where configId =:configId")
	public List<InwardEinvoiceReconAddlReportsEntity> findByConfigId(@Param("configId")
	Long configId); 
	
	@Modifying
	@Query("UPDATE InwardEinvoiceReconAddlReportsEntity SET isReportProcExecuted = true WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateIsReportProcExecuted(@Param("configId") Long configId,
			@Param("reportType") String reportType);
	
	public int deleteByConfigIdAndReportTypeIn(Long configId, 
			List<String> reportType);
	
	public int deleteByConfigIdAndReportTypeAndReportTypeIdIsNull(Long configId, 
			String reportType);
	
	public int deleteByConfigIdAndReportType(Long configId, String reportType);
	
	@Query("from InwardEinvoiceReconAddlReportsEntity  where filePath =:filePath")
	public InwardEinvoiceReconAddlReportsEntity findByDocId(
			@Param("filePath") String filePath);

}