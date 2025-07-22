package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesExpHeaderEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("Gstr1GetEInvoiceExpGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1GetEInvoiceExpGstnRepository
		extends CrudRepository<GetGstr1EInvoicesExpHeaderEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1EInvoicesExpHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	int softlyDeleteExpHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("from GetGstr1EInvoicesExpHeaderEntity where gstin=:gstin and invDate=:invDate and invNum=:invNum and exportType=:invType")
	List<GetGstr1EInvoicesExpHeaderEntity> findByInvoiceKey(
			@Param("gstin") String gstin, @Param("invDate") String invDate,
			@Param("invNum") String invNum,
			@Param("invType") String exportType);

	@Modifying
	@Query("UPDATE GetGstr1EInvoicesExpHeaderEntity b SET b.isDelete = true  WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> totalIds);

}
