/**
 * 
 */
package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.AnxErpSFTPBatchIdLogEntity;

/**
 * @author Sakshi.jain
 *
 */
@Repository("AnxErpSFTPBatchIdLogRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface AnxErpSFTPBatchIdLogRepository
		extends CrudRepository<AnxErpSFTPBatchIdLogEntity, Long> {

	public List<AnxErpSFTPBatchIdLogEntity> findTop50BySftpPushStatusInAndIsDeleteFalseOrderByIdAsc(
			List<String> status);

	int countByBatchIdAndIsDeleteFalse(Long batchId);

	@Modifying
	@Query(" Update AnxErpSFTPBatchIdLogEntity set isDelete = true where batchId =:batchId and isDelete = false")
	public void softDeleteBatchId(@Param("batchId") Long batchId);
	
	@Modifying
	@Query(" update AnxErpSFTPBatchIdLogEntity set sftpPushStatus =:status, updatedOn = CURRENT_TIMESTAMP where batchId =:batchId and erpBatchId =:erpBatchId and isDelete = false ")
	public void updateSftpPushStatus(@Param("erpBatchId") Long erpBatchId,
			@Param("status") String status, @Param("batchId") Long batchId);

}
