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

import com.ey.advisory.app.data.entities.client.GetGstr2bStagingIsdInvoicesHeaderEntity;

/**
 * @author Harsh
 *
 */
@Repository("GetGstr2bStagingIsdInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2bStagingIsdInvoicesRepository
		extends CrudRepository<GetGstr2bStagingIsdInvoicesHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE GetGstr2bStagingIsdInvoicesHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.rGstin = :rGstin AND b.taxPeriod = :taxPeriod")

	void softlyDeleteIsdHeader(@Param("rGstin") String rGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	
	@Query("from GetGstr2bStagingIsdInvoicesHeaderEntity where sGstin=:sGstin and docDate=:docDate and docNumber=:docNumber and isdDocType=:isdDocType")
	List<GetGstr2bStagingIsdInvoicesHeaderEntity> findByInvoiceKey(
			@Param("sGstin") String sGstin,
			@Param("docDate") LocalDateTime docDate,
			@Param("docNumber") String docNumber,
			@Param("isdDocType") String isdDocType);

	
	@Modifying
	@Query("UPDATE GetGstr2bStagingIsdInvoicesHeaderEntity b SET b.isDelete = true, b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> partion, @Param("modifiedOn") LocalDateTime modifiedOn);


}
