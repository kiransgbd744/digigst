package com.ey.advisory.app.data.repositories.client.pdfreader;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.pdfreader.PDFReaderFileDetailsEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("PDFReaderFileDetailsRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface PDFReaderFileDetailsRepo
		extends JpaRepository<PDFReaderFileDetailsEntity, Long> {

	@Query("SELECT doc FROM PDFReaderFileDetailsEntity doc "
			+ "WHERE doc.fileId = :fileId ")
	public List<PDFReaderFileDetailsEntity> retrieveFileNameById(
			@Param("fileId") Long fileId);
}
