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

import com.ey.advisory.app.data.entities.client.AsyncFileStatusRptTypeEntity;

/**
 * @author Sakshi.jain
 *
 */

@Repository("AsyncFileStatusReportTypeRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface AsyncFileStatusReportTypeRepository
		extends JpaRepository<AsyncFileStatusRptTypeEntity, Long>,
		JpaSpecificationExecutor<AsyncFileStatusRptTypeEntity> {

	
	List<AsyncFileStatusRptTypeEntity> findByReportDwnldIdOrderByIdAsc(Long configId);
	
	@Modifying
	@Query("UPDATE AsyncFileStatusRptTypeEntity SET "
			+ " filePath =:filePath, isDownloadable = true WHERE id =:id")
	public void updateFilePath(@Param("filePath") String filePath,
			@Param("id") Long id);
	
	@Modifying
	@Query("UPDATE AsyncFileStatusRptTypeEntity SET "
	        + "filePath = :filePath, isDownloadable = true, docId = :docId "
	        + "WHERE id = :id")
	public void updateFilePathDocId(
	        @Param("filePath") String filePath,
	        @Param("id") Long id,
	        @Param("docId") String docId);

	
	@Modifying
	@Query("UPDATE AsyncFileStatusRptTypeEntity SET "
			+ " filePath =:filePath, isDownloadable = true, docId =:docId WHERE id =:id")
	public void updateFilePath(@Param("filePath") String filePath,
			@Param("id") Long id,@Param("docId") String docId);
	
}
