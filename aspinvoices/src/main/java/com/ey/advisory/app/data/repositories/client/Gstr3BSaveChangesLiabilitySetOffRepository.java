package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr3b.Gstr3BSaveChangesLiabilitySetOffEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr3BSaveChangesLiabilitySetOffRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3BSaveChangesLiabilitySetOffRepository
		extends CrudRepository<Gstr3BSaveChangesLiabilitySetOffEntity, Long>, 
		JpaSpecificationExecutor<Gstr3BSaveChangesLiabilitySetOffEntity> {
	
	@Modifying
	@Query("Update Gstr3BSaveChangesLiabilitySetOffEntity SET isActive = "
			+ " false WHERE taxPeriod = :taxPeriod AND gstin =:gstin ")
		void updateActiveFlag(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin);
	
		
	@Query(" from Gstr3BSaveChangesLiabilitySetOffEntity WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin AND isActive = true ")
	public Gstr3BSaveChangesLiabilitySetOffEntity 
	findByGstinAndTaxPeriodAndIsActive(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin);


	List<Gstr3BSaveChangesLiabilitySetOffEntity> findByGstinInAndTaxPeriodAndIsActive(
			List<String> gstinsList, String taxPeriod, Boolean active);
	
}