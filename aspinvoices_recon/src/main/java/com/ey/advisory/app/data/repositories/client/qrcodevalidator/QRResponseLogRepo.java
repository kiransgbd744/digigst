package com.ey.advisory.app.data.repositories.client.qrcodevalidator;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseLogEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("QRResponseLogRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface QRResponseLogRepo
		extends JpaRepository<QRResponseLogEntity, Long> {
	
	 @Query("SELECT q.jsonPdfReconResponse FROM QRResponseLogEntity q WHERE q.fileId = :fileId")
	    List<String> findJsonPdfReconResponseByFileId(@Param("fileId") Long fileId);

}
