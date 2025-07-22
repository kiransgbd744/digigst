/*package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1B2bInvoicesEntity;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*
@Repository("gstr1GetB2bB2baAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1GetB2bB2baAtGstnRepository extends 
	CrudRepository<GetGstr1B2bInvoicesEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1B2bInvoicesEntity b SET b.isDelete = true WHERE"
			+ " b.sgstin =:sGstin AND b.returnPeriod =:taxPeriod")
	void softlyDeleteB2bHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);
}


*/