package com.ey.advisory.app.data.repositories.client.gstr2b;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2bInvoicesHeaderEntity;

/**
 * @author Ravindra V S
 *
 */
@Repository("GetGstr2bStagingB2bInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2bStagingB2bInvoicesRepository
		extends CrudRepository<GetGstr2bStagingB2bInvoicesHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE GetGstr2bStagingB2bInvoicesHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.rGstin = :rGstin AND b.taxPeriod = :taxPeriod")
	void softlyDeleteB2bHeader(@Param("rGstin") String rGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("from GetGstr2bStagingB2bInvoicesHeaderEntity where sGstin=:sGstin and rGstin=:rGstin and invoiceDate=:invoiceDate and invoiceNumber=:invoiceNumber and invoiceType=:invoiceType")
	List<GetGstr2bStagingB2bInvoicesHeaderEntity> findByInvoiceKey(
			@Param("sGstin") String sGstin,
			@Param("rGstin") String rGstin,
			@Param("invoiceDate") LocalDateTime invoiceDate,
			@Param("invoiceNumber") String invoiceNumber,
			@Param("invoiceType") String invoiceType);

	@Modifying
	@Query("UPDATE GetGstr2bStagingB2bInvoicesHeaderEntity b SET b.isDelete = true, b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> totalIds,@Param("modifiedOn") LocalDateTime modifiedOn);
	

}
