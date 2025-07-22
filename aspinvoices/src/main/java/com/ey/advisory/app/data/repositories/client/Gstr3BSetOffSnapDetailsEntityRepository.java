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

import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffSnapDetailsEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr3BSetOffSnapDetailsEntityRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3BSetOffSnapDetailsEntityRepository
		extends CrudRepository<Gstr3BSetOffSnapDetailsEntity, Long>,
		JpaSpecificationExecutor<Gstr3BSetOffSnapDetailsEntity> {
	
	@Modifying
	@Query("Update Gstr3BSetOffSnapDetailsEntity SET isDelete = true "
			+ " WHERE taxPeriod = :taxPeriod AND gstin =:gstin AND "
			+ " isDelete = false" )
		void softDelete(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin);
		
	List<Gstr3BSetOffSnapDetailsEntity> findByGstinAndTaxPeriodAndIsDelete(
			String gstinsList, String taxPeriod, Boolean active);
}
