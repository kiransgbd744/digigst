package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr2Avs3bReconEntity;

/**
 * 
 * @author Kiran
 *
 */
@Repository("Gstr2Avs3BReconRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2Avs3BReconRepository
		extends JpaRepository<Gstr2Avs3bReconEntity, Long> {
	
	@Modifying
	@Query("UPDATE Gstr2Avs3bReconEntity r SET r.status =:status, "
	        + "r.filePath =:filePath, r.docId =:docId, r.completedOn =:completedOn "
	        + "WHERE r.requestId =:requestId")
	void UpdateGstr2Avs3BRecon(
	        @Param("status") String status, 
	        @Param("filePath") String filePath,
	        @Param("completedOn") LocalDateTime completedOn, 
	        @Param("requestId") Long requestId,
	        @Param("docId") String docId);

}
