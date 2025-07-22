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

import com.ey.advisory.app.data.entities.client.Gstr2BGenerateReportTypeEntity;

@Repository("Gstr2BGenerateReportTypeRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface Gstr2BGenerateReportTypeRepository
		extends JpaRepository<Gstr2BGenerateReportTypeEntity, Long>,
		JpaSpecificationExecutor<Gstr2BGenerateReportTypeEntity> {

	List<Gstr2BGenerateReportTypeEntity> findByReportDwnldIdOrderByIdAsc(
			Long reportDwnldId);

	@Modifying
	@Query("UPDATE Gstr2BGenerateReportTypeEntity SET filePath =:filePath, "
			+ "docId =:docId, isDownloadable = true WHERE id =:id")
	public void updateDocId(@Param("filePath") String filePath,
			@Param("docId") String docId, @Param("id") Long id);

}
