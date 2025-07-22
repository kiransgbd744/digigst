/*package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1B2CLInvoicesEntity;

*//**
 * 
 * @author Umesha.M
 *
 *//*
@Repository("gstr1GetB2CLInvoicesAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1GetB2CLInvoicesAtGstnRepository
		extends CrudRepository<GetGstr1B2CLInvoicesEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1B2CLInvoicesEntity b SET b.isDelete = true WHERE"
			+ " b.sgstin =:sGstin AND b.returnPeriod =:taxPeriod")
	void softlyDeleteB2clHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);
}*/