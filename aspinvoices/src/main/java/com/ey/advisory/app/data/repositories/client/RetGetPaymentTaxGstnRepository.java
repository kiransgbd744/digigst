package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.ret.GetRetPaymentOfTaxEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("RetGetPaymentTaxGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface RetGetPaymentTaxGstnRepository
		extends CrudRepository<GetRetPaymentOfTaxEntity, Long> {
	@Modifying
	@Query("UPDATE GetRetPaymentOfTaxEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	void softlyDeletePaymentTaxHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);

}
