package com.ey.advisory.app.data.repositories.client.asprecon;


import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.VendorGstinDetailsEntity;
/**
 * @author Ravindra
 *
 */
@Repository("VendorGstinDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface VendorGstinDetailsRepository
		extends CrudRepository<VendorGstinDetailsEntity, Long>,
		JpaSpecificationExecutor<VendorGstinDetailsEntity> {

	VendorGstinDetailsEntity findByGstin(String gstin);
	
	@Query("select v.gstin, v.TradeName"
			+ " from VendorGstinDetailsEntity "
			+ " v where v.gstin in :gstin")
	public List<Object[]> getVendorGstinAndVendorTradeName(
			@Param("gstin") List<String> gstin);



}
