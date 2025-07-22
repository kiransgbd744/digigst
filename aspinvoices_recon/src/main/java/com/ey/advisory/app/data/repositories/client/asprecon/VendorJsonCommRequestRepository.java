package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.VendorJsonCommRequestEntity;

@Repository("VendorJsonCommRequestRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface VendorJsonCommRequestRepository
		extends JpaRepository<VendorJsonCommRequestEntity, Long>,
		JpaSpecificationExecutor<VendorJsonCommRequestEntity> {
	
	List<VendorJsonCommRequestEntity> findByRequestIdAndVendorGstin(Long requestId, String gstin);
	
}
