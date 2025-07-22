package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.NonCompVendorRequestEntity;

@Repository("NonCompVendorRequestRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface NonCompVendorRequestRepository
		extends JpaRepository<NonCompVendorRequestEntity, Long>,
		JpaSpecificationExecutor<NonCompVendorRequestEntity> {

	@Modifying
	@Query("UPDATE NonCompVendorRequestEntity SET status = :status,"
			+ " filePath = :filePath, updatedOn =:updatedOn"
			+ " where id  = :id")
	void updateStatus(@Param("id") Long id, @Param("status") String status,
			@Param("filePath") String filePath,
			@Param("updatedOn") LocalDateTime updatedOn);

	public NonCompVendorRequestEntity findByRequestId(
			@Param("requestId") Long requestId);

	public List<NonCompVendorRequestEntity> findByCreatedBy(String createdBy);
	
	@Query("select nonCompVendorRequestEntity.filePath from NonCompVendorRequestEntity nonCompVendorRequestEntity "
			+ "  where nonCompVendorRequestEntity.requestId = :requestId")
	public String getFilePath(@Param("requestId") Long requestId);

}
