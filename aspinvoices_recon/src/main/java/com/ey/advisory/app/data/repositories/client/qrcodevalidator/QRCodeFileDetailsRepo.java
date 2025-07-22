package com.ey.advisory.app.data.repositories.client.qrcodevalidator;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRCodeFileDetailsEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("QRCodeFileDetailsRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface QRCodeFileDetailsRepo
		extends JpaRepository<QRCodeFileDetailsEntity, Long> {

	@Query("SELECT doc FROM QRCodeFileDetailsEntity doc "
			+ "WHERE doc.fileId = :fileId ")
	public List<QRCodeFileDetailsEntity> retrieveFileNameById(
			@Param("fileId") Long fileId);
}
