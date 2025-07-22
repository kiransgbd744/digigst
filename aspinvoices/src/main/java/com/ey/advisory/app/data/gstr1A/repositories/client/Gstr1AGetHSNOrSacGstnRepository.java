package com.ey.advisory.app.data.gstr1A.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1HSNOrSACInvoicesEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AHSNOrSACInvoicesEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("Gstr1AGetHSNOrSacGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1AGetHSNOrSacGstnRepository
		extends CrudRepository<GetGstr1AHSNOrSACInvoicesEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1AHSNOrSACInvoicesEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = false AND b.gstinOfTaxPayer =:sGstin AND b.returnPeriod =:taxPeriod")
	int softlyDeleteHsnHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);

}
