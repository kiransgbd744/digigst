/*package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1HSNSummaryInvoicesEntity;

@Repository("gstr1GetHSNSammaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1GetHSNSammaryAtGstnRepository
		extends CrudRepository<GetGstr1HSNSummaryInvoicesEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1HSNSummaryInvoicesEntity b SET b.isDelete = true WHERE"
			+ " b.gstinOfTaxPayer =:sGstin AND b.returnPeriod =:taxPeriod")
	void softlyDeleteHsnHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);

}
*/