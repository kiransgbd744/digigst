/**
 * 
 */
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

import com.ey.advisory.service.gstr3bSummary.Gstr3bSummaryUploadErrorEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("Gstr3bSummaryErrorRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3bSummaryErrorRepository
		extends JpaRepository<Gstr3bSummaryUploadErrorEntity, Long>,
		JpaSpecificationExecutor<Gstr3bSummaryUploadErrorEntity> {
	
	@Modifying
	@Query("Update Gstr3bSummaryUploadErrorEntity SET isActive = false  WHERE "
			+ "docKey in (:docKeyList) AND isActive = true")
		int updateIsDeleteFlag(
				@Param("docKeyList")List<String> docKeyList);
	
	public List<Gstr3bSummaryUploadErrorEntity> findByFileId(Long fileId);

}
