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

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr2ReconAddlReportsRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface Gstr2ReconAddlReportsRepository
		extends JpaRepository<Gstr2ReconAddlReportsEntity, Long>,
		JpaSpecificationExecutor<Gstr2ReconAddlReportsEntity> {

	@Modifying
	@Query("UPDATE Gstr2ReconAddlReportsEntity SET "
			+ " filePath =:filePath, isDownloadable = true WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateReconFilePath(@Param("filePath") String filePath,
			@Param("configId") Long configId,
			@Param("reportType") String reportType);

	@Query("from Gstr2ReconAddlReportsEntity  where"
			+ " configId =:configId AND reportType =:reportType")
	public Gstr2ReconAddlReportsEntity findByConfigId(
			@Param("configId") Long configId,
			@Param("reportType") String reportType);

	@Query("from Gstr2ReconAddlReportsEntity  where configId =:configId")
	public List<Gstr2ReconAddlReportsEntity> getDataList(
			@Param("configId") Long configId);

	@Query("SELECT reportType from Gstr2ReconAddlReportsEntity  "
			+ "where configId =:configId ")
	public List<String> getAddlnReportTypeList(
			@Param("configId") Long configId);

	@Modifying
	@Query("UPDATE Gstr2ReconAddlReportsEntity SET isDownloadable = false WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateAndSave(@Param("configId") Long configId,
			@Param("reportType") String reportType);

	@Query(" from Gstr2ReconAddlReportsEntity WHERE configId =:configId  "
			+ "AND reportType =:reportType")
	public Gstr2ReconAddlReportsEntity getChunckSizeforReportType(
			@Param("configId") Long configId,
			@Param("reportType") String reportType);

	public Gstr2ReconAddlReportsEntity findByReportTypeAndConfigId(
			String reportType, Long configId);

	@Query("from Gstr2ReconAddlReportsEntity  where configId =:configId")
	public List<Gstr2ReconAddlReportsEntity> findByConfigId(
			@Param("configId") Long configId);

	@Modifying
	@Query("UPDATE Gstr2ReconAddlReportsEntity SET isReportProcExecuted = true WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateIsReportProcExecuted(@Param("configId") Long configId,
			@Param("reportType") String reportType);

	@Query("from Gstr2ReconAddlReportsEntity  where configId IN (:configId)")
	public List<Gstr2ReconAddlReportsEntity> findReportListData(
			@Param("configId") List<Long> configId);

	@Modifying
	@Query("UPDATE Gstr2ReconAddlReportsEntity SET reportTypeId =:reportTypeId"
			+ " WHERE configId =:configId AND reportType =:reportType")
	public void updateReportTypeId(@Param("configId") Long configId,
			@Param("reportType") String reportType,
			@Param("reportTypeId") Integer reportTypeId);

	public int deleteByConfigIdAndReportTypeIn(Long configId,
			List<String> reportType);

	public int deleteByConfigIdAndReportType(Long configId, String reportType);
	

	@Query("from Gstr2ReconAddlReportsEntity  where filePath =:filePath")
	public Gstr2ReconAddlReportsEntity findByDocId(
			@Param("filePath") String filePath);
	
	@Modifying
	@Query("UPDATE Gstr2ReconAddlReportsEntity SET filePath =:filePath, "
			+ "docId =:docId, isDownloadable = true WHERE "
			+ "configId =:configId AND reportType =:reportType")
	public void updateReconFilePathAndDocIdByReportName(
			@Param("filePath") String filePath, @Param("docId") String docId,
			@Param("configId") Long configId,
			@Param("reportType") String reportType);
}