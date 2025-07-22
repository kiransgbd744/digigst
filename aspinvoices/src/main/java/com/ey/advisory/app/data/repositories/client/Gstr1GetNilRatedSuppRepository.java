/*package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1NilDetailsInvoicesEntity;

@Repository("gstr1GetNilRatedSuppRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1GetNilRatedSuppRepository
		extends CrudRepository<GetGstr1NilDetailsInvoicesEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1NilDetailsInvoicesEntity b SET b.isDelete = true WHERE"
			+ " b.taxPayerOfGstn =:sGstin AND b.ret_period =:taxPeriod")
	void softlyDeleteNilHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);

}
*/