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

import com.ey.advisory.app.data.entities.client.GetGstr2bB2bEcomaHeaderEntity;

/**
 * @author kiran s
 *
 */
@Repository("GetGstr2bB2bEcomaHeaderRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2bB2bEcomaHeaderRepository
		extends CrudRepository<GetGstr2bB2bEcomaHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE GetGstr2bB2bEcomaHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.rGstin = :rGstin AND b.taxPeriod = :taxPeriod")

	void softlyDeleteB2baHeader(@Param("rGstin") String rGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	
	@Query("from GetGstr2bB2bEcomaHeaderEntity where sGstin=:sGstin and rGstin=:rGstin and invoiceDate=:invoiceDate and invoiceNumber=:invoiceNumber and invoiceType=:invoiceType")
	List<GetGstr2bB2bEcomaHeaderEntity> findByInvoiceKey(@Param("sGstin") String sGstin,
			@Param("rGstin") String rGstin,
			@Param("invoiceDate") LocalDateTime invoiceDate,
			@Param("invoiceNumber") String invoiceNumber,
			@Param("invoiceType") String invoiceType);

	
	@Modifying
	@Query("UPDATE GetGstr2bB2bEcomaHeaderEntity b SET b.isDelete = true, b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> partion, @Param("modifiedOn") LocalDateTime modifiedOn);


}
