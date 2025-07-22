/**
 * 
 */
package com.ey.advisory.app.data.repositories.client.gstr2;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2aErpCdnInvoicesHeaderEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("GetGstr2aErpCdnInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2aErpCdnInvoicesRepository 
		extends CrudRepository<GetGstr2aErpCdnInvoicesHeaderEntity, Long> {

	// currently isDelete is not added in table
	/*
	 * @Modifying
	 * 
	 * @Query("UPDATE GetGstr2aErpCdnInvoicesHeaderEntity b SET b.isDelete = true WHERE"
	 * +
	 * " b.isDelete = FALSE AND b.cgstin = :cgstin AND b.returnPeriod = :returnPeriod"
	 * ) void softlyDeleteCdnCdnaHeader(@Param("cgstin") String cgstin,
	 * 
	 * @Param("returnPeriod") String returnPeriod);
	 */

	@Query("SELECT COUNT(*) FROM GetGstr2aErpCdnInvoicesHeaderEntity "
			+ "WHERE gstin=:gstin AND taxPeriod = :taxperiod ")
	public int gstinCount(@Param("gstin") String gstin,
			@Param("taxperiod") String taxperiod);

	// Curently isDelete is not added in table
	@Modifying
	@Query("UPDATE GetGstr2aCdnInvoicesHeaderEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = FALSE AND b.countergstin = :cgstin AND b.taxPeriod = :returnPeriod")
	void softlyDeleteCdnHeader(@Param("cgstin") String cgstin,
			@Param("returnPeriod") String returnPeriod);

}
