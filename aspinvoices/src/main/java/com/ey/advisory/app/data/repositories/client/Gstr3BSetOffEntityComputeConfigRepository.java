package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffEntityComputeConfigEntity;

/**
 * @author Hema G M
 *
 */

@Repository("Gstr3BSetOffEntityComputeConfigRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3BSetOffEntityComputeConfigRepository
		extends CrudRepository<Gstr3BSetOffEntityComputeConfigEntity, Long>,
		JpaSpecificationExecutor<Gstr3BSetOffEntityComputeConfigEntity> {

	List<Gstr3BSetOffEntityComputeConfigEntity> findByGstinInAndTaxPeriodAndIsActive(
			List<String> gstinsList, String taxPeriod, Boolean active);
	
	@Modifying
	@Query("Update Gstr3BSetOffEntityComputeConfigEntity SET isActive = false  "
			+ "WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin AND isActive = true" )
		void softDelete(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin);
	
	@Modifying
	@Query("Update Gstr3BSetOffEntityComputeConfigEntity SET "
			+ "computeStatus = 'Computed',updatedOn =:now WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin AND isActive = true")
	void updateStatus(@Param("taxPeriod") String taxPeriod,
			@Param("gstin") String gstin,
			@Param("now") LocalDateTime now);

}