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

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2GenerateReportTypeEntity;

@Repository("Gstr2GenerateReportTypeRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface Gstr2GenerateReportTypeRepository
		extends JpaRepository<Gstr2GenerateReportTypeEntity, Long>,
		JpaSpecificationExecutor<Gstr2GenerateReportTypeEntity> {

	List<Gstr2GenerateReportTypeEntity> findByReportDwnldIdOrderByIdAsc(
			Long reportDwnldId);

	@Modifying
	@Query("UPDATE Gstr2GenerateReportTypeEntity SET filePath =:filePath, "
			+ "docId =:docId, isDownloadable = true WHERE id =:id")
	public void updateDocId(@Param("filePath") String filePath,
			@Param("docId") String docId, @Param("id") Long id);

}
