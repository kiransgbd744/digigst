package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.ims.handlers.ImsERPRequestEntity;

/**
 * @author Ravindra V S
 *
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface ImsERPRequestRepository
		extends JpaRepository<ImsERPRequestEntity, Long>,
		JpaSpecificationExecutor<ImsERPRequestEntity> {
	
	
	public List<ImsERPRequestEntity> findByBatchIdAndStatusIn(
			Long batchId, List<String> status);
	
	public ImsERPRequestEntity findByBatchId(Long batchId);

	public List<ImsERPRequestEntity> findByStatusIn(List<String> status);
	
	@Modifying
	@Query("update ImsERPRequestEntity set status =:status, "
			+ " updatedOn = CURRENT_TIMESTAMP where "
			+ "gstin =:gstin and batchId =:batchId")
	public void updateStatus(@Param("batchId") Long batchId,
			@Param("gstin") String gstin, @Param("status") String status);
	
	@Modifying
	@Query("update ImsERPRequestEntity set status =:status, "
			+ "updatedOn = CURRENT_TIMESTAMP, isErpPush =:isErpPush where"
			+ " batchId =:batchId")
	public void updateStatusByBatchId(@Param("batchId") Long batchId,
			@Param("status") String status,
			@Param("isErpPush") boolean isErpPush);

}
