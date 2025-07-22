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

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRAddlReportsEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr2Recon2BPRAddlReportsRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface Gstr2Recon2BPRAddlReportsRepository
		extends JpaRepository<Gstr2Recon2BPRAddlReportsEntity, Long>,
		JpaSpecificationExecutor<Gstr2Recon2BPRAddlReportsEntity> {

	@Modifying
	@Query("UPDATE Gstr2Recon2BPRAddlReportsEntity SET "
			+ " filePath =:filePath, isDownloadable = true WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateReconFilePath(@Param("filePath") String filePath,
			@Param("configId") Long configId,
			@Param("reportType") String reportType);

	@Query("from Gstr2Recon2BPRAddlReportsEntity  where"
			+ " configId =:configId AND reportType =:reportType")
	public Gstr2Recon2BPRAddlReportsEntity findByConfigId(@Param("configId") 
					Long configId, @Param("reportType") String reportType);
	
	
	@Query("from Gstr2Recon2BPRAddlReportsEntity  where configId =:configId")
	public List<Gstr2Recon2BPRAddlReportsEntity> getDataList(@Param("configId") 
								Long configId);
	
	@Query("SELECT reportType from Gstr2Recon2BPRAddlReportsEntity  "
			+ "where configId =:configId ")
	public List<String> getAddlnReportTypeList(@Param("configId") Long configId);
	
	
	@Modifying
	@Query("UPDATE Gstr2Recon2BPRAddlReportsEntity SET isDownloadable = false WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateAndSave(@Param("configId") Long configId,
			@Param("reportType") String reportType);
	
	
	@Query(" from Gstr2Recon2BPRAddlReportsEntity WHERE configId =:configId  "
			+ "AND reportType =:reportType")
	public Gstr2Recon2BPRAddlReportsEntity  getChunckSizeforReportType(@Param
			("configId") Long configId, @Param("reportType") String reportType);
	
	@Query("from Gstr2Recon2BPRAddlReportsEntity  where configId =:configId")
	public List<Gstr2Recon2BPRAddlReportsEntity> findByConfigId(@Param("configId")
	Long configId); 
	
	@Modifying
	@Query("UPDATE Gstr2Recon2BPRAddlReportsEntity SET isReportProcExecuted = true WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateIsReportProcExecuted(@Param("configId") Long configId,
			@Param("reportType") String reportType);
	
	public int deleteByConfigIdAndReportTypeIn(Long configId, 
			List<String> reportType);
	
	public int deleteByConfigIdAndReportTypeAndReportTypeIdIsNull(Long configId, 
			String reportType);
	
	public int deleteByConfigIdAndReportType(Long configId, String reportType);
	
	@Query("from Gstr2Recon2BPRAddlReportsEntity  where filePath =:filePath")
	public Gstr2Recon2BPRAddlReportsEntity findByDocId(
			@Param("filePath") String filePath);

}