/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr3BTaxPaymentEntity;

/**
 * @author Arun KA
 *
 */

@Repository("Gstr3bTaxPaymentRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3bTaxPaymentRepository
		extends CrudRepository<Gstr3BTaxPaymentEntity, Long> {

	@Modifying
	@Query("Update Gstr3BTaxPaymentEntity SET isActive = false  WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin AND isActive = true" )
		void updateAllActiveFlag(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin);

}
