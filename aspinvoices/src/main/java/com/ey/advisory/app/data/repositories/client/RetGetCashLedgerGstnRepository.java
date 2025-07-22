package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.ret.GetRefundClaimedEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("RetGetCashLedgerGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface RetGetCashLedgerGstnRepository
		extends CrudRepository<GetRefundClaimedEntity, Long> {
	@Modifying
	@Query("UPDATE GetRefundClaimedEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	void softlyDeleteCashLedgerHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);

}
