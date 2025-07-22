package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.simplified.client.Gstr6CrossItcSaveComputeEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("Gstr6CrossItcSaveComputeRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr6CrossItcSaveComputeRepository
		extends CrudRepository<Gstr6CrossItcSaveComputeEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr6CrossItcSaveComputeEntity b SET b.isDelete = true WHERE "
			+ "b.isDelete = false AND b.isDigiGstCompute =:isDigiGstCompute AND "
			+ "b.isSavedToGstn = :isSavedToGstn AND b.isGstnError =:isGstnError AND "
			+ "b.gstin =:gstin AND b.taxPeriod =:taxPeriod")
	void softlyDelete(@Param("isDigiGstCompute") boolean isDigiGstCompute,
			@Param("isSavedToGstn") boolean isSavedToGstn,
			@Param("isGstnError") boolean isGstnError,
			@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod);
	
}
