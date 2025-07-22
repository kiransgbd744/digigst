/**
 * 
 */
package com.ey.advisory.app.data.repositories.client.gstr8;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr8.Gstr8UploadErrorEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("Gstr8ErrorRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr8ErrorRepository
		extends JpaRepository<Gstr8UploadErrorEntity, Long>,
		JpaSpecificationExecutor<Gstr8UploadErrorEntity> {

	@Modifying
	@Query("Update Gstr8UploadErrorEntity SET isActive = false  WHERE "
			+ "docKey in (:docKeyList) AND isActive = true")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList);

	public List<Gstr8UploadErrorEntity> findByFileId(Long fileId);

}
