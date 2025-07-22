/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.GlCodeMasterEntity;

@Repository("GlCodeMasterRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GlCodeMasterRepo
		extends CrudRepository<GlCodeMasterEntity, Long> {

	List<GlCodeMasterEntity> findByEntityIdAndIsActiveTrue(Long entityId);

	List<GlCodeMasterEntity> findByFileTypeAndIsActiveTrue(String fileType);

	@Modifying
	@Query("UPDATE GlCodeMasterEntity e SET e.isActive = false WHERE e.id IN :ids")
	void softDeleteByIds(@Param("ids") List<Long> ids);

	@Query("SELECT e.id FROM GlCodeMasterEntity e WHERE e.fileType = :fileType AND e.isActive = true")
	List<Long> findActiveIdsByFileType(@Param("fileType") String fileType);

	List<GlCodeMasterEntity> findByFileIdAndFileTypeAndIsActiveFalse(
			String fileId, String fileType);

	List<GlCodeMasterEntity> findByIsActiveTrue();

}
