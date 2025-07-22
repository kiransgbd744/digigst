package com.ey.advisory.app.data.repositories.client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.
                                         Gstr1AdvanceRecivedFileUploadEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Gstr1ATRepository")
public interface Gstr1ATRepository extends 
                JpaRepository<Gstr1AdvanceRecivedFileUploadEntity, Long>,
                JpaSpecificationExecutor<Gstr1AdvanceRecivedFileUploadEntity> {
	@Transactional
	@Modifying
	@Query("UPDATE Gstr1AdvanceRecivedFileUploadEntity at SET "
			+ "at.isDelete= TRUE  WHERE at.atKey IN (:docKey) ")
	public void updateDocKey(@Param("docKey") String docKey);

}
