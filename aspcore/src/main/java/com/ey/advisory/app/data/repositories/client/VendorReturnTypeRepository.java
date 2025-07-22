package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.VendorReturnTypeEntity;

@Repository("VendorReturnTypeRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface VendorReturnTypeRepository
		extends JpaRepository<VendorReturnTypeEntity, Long>,
		JpaSpecificationExecutor<VendorReturnTypeEntity> {

	VendorReturnTypeEntity findByGstin(String gstin);

	List<VendorReturnTypeEntity> findByGstinInAndReturnTypesContaining(
			List<String> vendorGstin, String returnType);

	List<VendorReturnTypeEntity> findByGstinInAndResponseIsNull(
			List<String> vendorGstins);

	List<VendorReturnTypeEntity> findByGstinInAndResponseIsNotNull(
			List<String> vendorGstins);
}
