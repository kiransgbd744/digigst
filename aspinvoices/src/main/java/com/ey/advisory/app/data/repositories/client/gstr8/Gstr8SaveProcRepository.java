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

import com.ey.advisory.app.data.entities.gstr8.Gstr8SaveProcEntity;

/**
 * @author Siva.Reddy
 *
 */
@Repository("Gstr8SaveProcRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr8SaveProcRepository
		extends JpaRepository<Gstr8SaveProcEntity, Long>,
		JpaSpecificationExecutor<Gstr8SaveProcEntity> {

	@Modifying
	@Query("UPDATE Gstr8SaveProcEntity doc SET doc.batchId=:batchId,"
			+ "doc.isSentToGstn=true, doc.modifiedBy = 'SYSTEM', "
			+ "doc.modifiedOn = CURRENT_TIMESTAMP WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("batchId") Long gstnBatchId,
			@Param("ids") List<Long> ids);
}