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

import com.ey.advisory.app.data.entities.client.asprecon.InwardEinvReconChildReportTypeEntity;

/**
 * @author Sakshi.jain
 *
 */

@Repository("InwardEinvoiceChildRReportTypeRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface InwardEinvoiceChildRReportTypeRepository
		extends JpaRepository<InwardEinvReconChildReportTypeEntity, Long>,
		JpaSpecificationExecutor<InwardEinvReconChildReportTypeEntity> {

	@Modifying
	@Query("UPDATE InwardEinvReconChildReportTypeEntity SET "
			+ " filePath =:filePath, isDownloadable = true WHERE id =:id")
	public void updateFilePath(@Param("filePath") String filePath,
			@Param("id") Long id);

	public List<InwardEinvReconChildReportTypeEntity> findByReportDwnldIdIn(
			List<Long> reportDwnldId);
	
	public int deleteByReportDwnldIdIn(List<Long> reportDwnldId);
	
	@Query("from InwardEinvReconChildReportTypeEntity  where filePath =:filePath")
	public InwardEinvReconChildReportTypeEntity findByDocId(
			@Param("filePath") String filePath);
	
	@Modifying
	@Query("UPDATE InwardEinvReconChildReportTypeEntity SET "
			+ " filePath =:filePath, docId =:docId,"
			+ " isDownloadable = true WHERE id =:id")
	public void updateFilePathAndDocId(@Param("filePath") String filePath,
			@Param("docId") String docId,
			@Param("id") Long id);
}
