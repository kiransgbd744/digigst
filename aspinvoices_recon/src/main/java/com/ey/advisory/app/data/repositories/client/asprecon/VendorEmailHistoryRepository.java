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

import com.ey.advisory.app.data.entities.client.asprecon.VendorEmailHistoryEntity;

@Repository("VendorEmailHistoryRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface VendorEmailHistoryRepository
		extends JpaRepository<VendorEmailHistoryEntity, Long>,
		JpaSpecificationExecutor<VendorEmailHistoryEntity> {

	List<VendorEmailHistoryEntity> findByRequestId(Long requestId);

	@Modifying
	@Query("UPDATE VendorEmailHistoryEntity SET emailStatus =:status "
			+ "WHERE requestId =:reqId AND vendorGstin =:vendorGstin")
	void updateStatusByGstinAndRequestId(@Param("status") String status,
			@Param("reqId") Long reqId,
			@Param("vendorGstin") String vendorGstin);

	@Modifying
	@Query("UPDATE VendorEmailHistoryEntity SET emailStatus =:emailStatus, counter =counter+1, createdOn = CURRENT_TIMESTAMP "
			+ "WHERE requestId =:reqId AND vendorGstin in :vendorGstin")
	void updateEmailSentStatus(@Param("reqId") Long requestId,
			@Param("vendorGstin") List<String> vendorGstin,
			@Param("emailStatus") String emailStatus);

	@Modifying
	@Query("UPDATE VendorEmailHistoryEntity SET emailStatus =:emailStatus, createdOn = CURRENT_TIMESTAMP "
			+ "WHERE requestId =:reqId AND vendorGstin in :vendorGstin")
	void updateEmailFailureStatus(@Param("reqId") Long requestId,
			@Param("vendorGstin") List<String> vendorGstin,
			@Param("emailStatus") String emailStatus);
}
