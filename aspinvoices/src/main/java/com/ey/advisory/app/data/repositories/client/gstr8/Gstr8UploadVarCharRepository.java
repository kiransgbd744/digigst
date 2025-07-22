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

import com.ey.advisory.app.data.entities.gstr8.Gstr8UploadVarCharEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("Gstr8UploadVarCharRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr8UploadVarCharRepository
		extends JpaRepository<Gstr8UploadVarCharEntity, Long>,
		JpaSpecificationExecutor<Gstr8UploadVarCharEntity> {

	@Modifying
	@Query("Update Gstr8UploadVarCharEntity SET isActive = false  WHERE "
			+ "docKey in (:docKeyList) AND isActive = true")
	int updateIsActiveFlag(@Param("docKeyList") List<String> docKeyList);

	public List<Gstr8UploadVarCharEntity> findByFileId(Long fileId);
}
