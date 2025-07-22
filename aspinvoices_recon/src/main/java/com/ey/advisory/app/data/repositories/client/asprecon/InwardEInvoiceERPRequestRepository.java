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

import com.ey.advisory.app.inward.einvoice.InwardEInvoiceERPRequestEntity;

/**
 * @author vishal.verma
 *
 */
@Repository("InwardEInvoiceERPRequestRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface InwardEInvoiceERPRequestRepository
		extends JpaRepository<InwardEInvoiceERPRequestEntity, Long>,
		JpaSpecificationExecutor<InwardEInvoiceERPRequestEntity> {
	
	
	public List<InwardEInvoiceERPRequestEntity> findByBatchIdAndStatusIn(
			Long batchId, List<String> status);
	
	public InwardEInvoiceERPRequestEntity findByBatchId(Long batchId);

	public List<InwardEInvoiceERPRequestEntity> findByStatusIn(List<String> status);
	
	@Modifying
	@Query("update InwardEInvoiceERPRequestEntity set status =:status, "
			+ " updatedOn = CURRENT_TIMESTAMP where "
			+ "gstin =:gstin and batchId =:batchId")
	public void updateStatus(@Param("batchId") Long batchId,
			@Param("gstin") String gstin, @Param("status") String status);
	
	@Modifying
	@Query("update InwardEInvoiceERPRequestEntity set status =:status, "
			+ "updatedOn = CURRENT_TIMESTAMP, isErpPush =:isErpPush where"
			+ " batchId =:batchId")
	public void updateStatusByBatchId(@Param("batchId") Long batchId,
			@Param("status") String status,
			@Param("isErpPush") boolean isErpPush);

}
