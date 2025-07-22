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

import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnrInvoicesHeaderEntity;

/**
 * @author Ravindra
 *
 */
@Repository("GetGstr2bStagingCdnrInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2bStagingCdnrInvoicesRepository
		extends CrudRepository<GetGstr2bStagingCdnrInvoicesHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE GetGstr2bStagingCdnrInvoicesHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.rGstin = :rGstin AND b.taxPeriod = :taxPeriod")

	void softlyDeleteCdnrHeader(@Param("rGstin") String rGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("from GetGstr2bStagingCdnrInvoicesHeaderEntity where sGstin=:sGstin and rGstin=:rGstin and noteDate=:noteDate and noteNumber=:noteNumber and noteType=:noteType")
	List<GetGstr2bStagingCdnrInvoicesHeaderEntity> findByInvoiceKey(@Param("sGstin") String sGstin, 
			@Param("rGstin") String rGstin,
			@Param("noteDate") LocalDateTime noteDate, 
			@Param("noteNumber") String noteNumber, 
			@Param("noteType") String noteType);

	@Modifying
	@Query("UPDATE GetGstr2bStagingCdnrInvoicesHeaderEntity b SET b.isDelete = true, b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> partion, @Param("modifiedOn") LocalDateTime modifiedOn);

}
