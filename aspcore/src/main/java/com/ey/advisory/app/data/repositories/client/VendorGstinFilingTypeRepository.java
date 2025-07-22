package com.ey.advisory.app.data.repositories.client;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.VendorGstinFilingTypeEntity;

@Repository("VendorGstinFilingTypeRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface VendorGstinFilingTypeRepository
		extends JpaRepository<VendorGstinFilingTypeEntity, Long>,
		JpaSpecificationExecutor<VendorGstinFilingTypeEntity> {

	List<VendorGstinFilingTypeEntity> findByGstinInAndFyIn(
			List<String> vendorGstins, List<String> fy);

	@Query("SELECT count(*) from VendorGstinFilingTypeEntity")
	int getOverallCount();

	List<VendorGstinFilingTypeEntity> findByGstinInAndFy(
			List<String> vendorGstins, String fy);

	List<VendorGstinFilingTypeEntity> findByGstinInAndFyOrderByQuarterAsc(
			List<String> vendorGstins, String fy);
	
	Optional<VendorGstinFilingTypeEntity> findByGstinAndFyAndReturnType(
			String vendorGstin, String fy, String returnType);

	@Query("Select doc.gstin from VendorGstinFilingTypeEntity doc where doc.gstin IN (:gstins)"
			+ " and doc.fy =:fy and doc.quarter =:quarter")
	List<String> findByGstinInAndFyAndQuarter(
			@Param("gstins") List<String> gstins, @Param("fy") String fy,
			@Param("quarter") String quarter);
	
	List<VendorGstinFilingTypeEntity> findByGstinInAndFyAndReturnTypeInOrderByQuarterAsc(
			List<String> vendorGstins, String fy, List<String> returnTypes);
}
