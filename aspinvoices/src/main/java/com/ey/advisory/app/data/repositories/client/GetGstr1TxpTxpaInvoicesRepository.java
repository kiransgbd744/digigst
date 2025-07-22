/*package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1TXPInvoicesEntity;

@Repository("getGstr1TxpTxpaInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr1TxpTxpaInvoicesRepository
		extends CrudRepository<GetGstr1TXPInvoicesEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1TXPInvoicesEntity b SET b.isDelete = true WHERE"
			+ " b.gstinOfTheTaxpayer =:sGstin AND b.retPeriod =:taxPeriod")
	void softlyDeleteB2clHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);
}
*/