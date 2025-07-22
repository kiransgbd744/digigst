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

import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnurHeaderEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("Gstr1GetEInvoiceCdnurGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1GetEInvoiceCdnurGstnRepository
		extends CrudRepository<GetGstr1EInvoicesCdnurHeaderEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1EInvoicesCdnurHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn   WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	int softlyDeleteCdnurHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("from GetGstr1EInvoicesCdnurHeaderEntity where gstin=:gstin and noteDate=:invDate and noteNum=:invNum and noteType=:invType")
	List<GetGstr1EInvoicesCdnurHeaderEntity> findByInvoiceKey(
			@Param("gstin") String gstin, @Param("invDate") String noteDate,
			@Param("invNum") String noteNum, @Param("invType") String noteType);

	@Modifying
	@Query("UPDATE GetGstr1EInvoicesCdnurHeaderEntity b SET b.isDelete = true  WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> totalIds);

}
