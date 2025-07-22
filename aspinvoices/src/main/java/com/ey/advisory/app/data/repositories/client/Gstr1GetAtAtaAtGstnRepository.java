/*package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1ATEntity;
*//**
 * 
 * @author Hemasundar.J
 *
 *//*
@Repository("gstr1GetAtAtaAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1GetAtAtaAtGstnRepository extends 
	CrudRepository<GetGstr1ATEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1ATEntity b SET b.isDelete = true WHERE"
			+ " b.sgstin =:sGstin AND b.returnPeriod =:taxPeriod")
	void softlyDeleteAtaHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);

}
*/