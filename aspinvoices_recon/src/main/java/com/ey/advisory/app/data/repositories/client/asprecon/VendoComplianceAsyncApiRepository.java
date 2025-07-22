package com.ey.advisory.app.data.repositories.client.asprecon;

import java.sql.Clob;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.VendorComplianceRatingAsyncApiResponseEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("VendoComplianceAsyncApiRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface VendoComplianceAsyncApiRepository extends
		JpaRepository<VendorComplianceRatingAsyncApiResponseEntity, Long>,
		JpaSpecificationExecutor<VendorComplianceRatingAsyncApiResponseEntity> {

	public Optional<VendorComplianceRatingAsyncApiResponseEntity> findById(
			Long id);

	public Optional<VendorComplianceRatingAsyncApiResponseEntity> findByReferenceId(
			String referenceId);
	
	public List<VendorComplianceRatingAsyncApiResponseEntity> findByEntityIdOrderByIdDesc(
			Long entityId);
	
	@Modifying
	@Query("UPDATE VendorComplianceRatingAsyncApiResponseEntity file SET file.responsePayload = "
			+ ":response where file.id = :id ")
	public void updateResponse(@Param("id") Long id,
			@Param("response") Clob response);
	
	@Modifying
	@Query("UPDATE VendorComplianceRatingAsyncApiResponseEntity file SET file.responsePayload = "
			+ ":response,file.status =:status,modifiedOn = CURRENT_TIMESTAMP  "
			+ " where file.id =:id ")
	public void updateStatusSummary(@Param("id") Long id,
			@Param("response") Clob response,
			@Param("status") String status);
}
