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

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2APRReportTypeEntity;

/**
 * @author Vishal.verma
 *
 */

@Repository("Gstr2Recon2APRReportTypeRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface Gstr2Recon2APRReportTypeRepository
		extends JpaRepository<Gstr2Recon2APRReportTypeEntity, Long>,
		JpaSpecificationExecutor<Gstr2Recon2APRReportTypeEntity> {

	@Modifying
	@Query("UPDATE Gstr2Recon2APRReportTypeEntity SET "
			+ " filePath =:filePath, isDownloadable = true WHERE id =:id")
	public void updateFilePath(@Param("filePath") String filePath,
			@Param("id") Long id);

	public List<Gstr2Recon2APRReportTypeEntity> findByReportDwnldIdIn(
			List<Long> reportDwnldId);
	
	public int deleteByReportDwnldIdIn(List<Long> reportDwnldId);
	
	@Query("from Gstr2Recon2APRReportTypeEntity  where filePath =:filePath")
	public Gstr2Recon2APRReportTypeEntity findByDocId(
			@Param("filePath") String filePath);
	
	@Modifying
	@Query("UPDATE Gstr2Recon2APRReportTypeEntity SET "
			+ " filePath =:filePath, docId =:docId, "
			+ " isDownloadable = true WHERE id =:id")
	public void updateFilePathAndDocId(@Param("filePath") String filePath,
			@Param("docId") String docId,
			@Param("id") Long id);
}
