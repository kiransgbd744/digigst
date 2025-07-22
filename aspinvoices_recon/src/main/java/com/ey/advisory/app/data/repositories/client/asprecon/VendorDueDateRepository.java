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

import com.ey.advisory.app.data.entities.client.asprecon.VendorDueDateEntity;

/**
 * 
 * @author Jithendra.B
 *
 */
@Repository("VendorDueDateRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface VendorDueDateRepository
		extends JpaRepository<VendorDueDateEntity, Long>,
		JpaSpecificationExecutor<VendorDueDateEntity> {

	@Modifying
	@Query("UPDATE VendorDueDateEntity SET isDelete = true , "
			+ " updatedOn = CURRENT_TIMESTAMP , updatedBy =:updatedBy  WHERE "
			+ " isDelete = false and key in (:keys) ")
	int softDeleteActiveRecords(@Param("updatedBy") String updatedBy,
			@Param("keys") List<String> keys);

	public List<VendorDueDateEntity> findByEntityIdAndIsDeleteFalse(
			Long entityId);

	List<VendorDueDateEntity> findByReturnTypeAndEntityIdAndTaxPeriodInAndIsDeleteFalse(
			String returnType, Long entityId, List<String> taxPeriods);

	List<VendorDueDateEntity> findByReturnTypeInAndEntityIdAndTaxPeriodInAndIsDeleteFalse(
			List<String> returnType, Long entityId, List<String> taxPeriods);
}
