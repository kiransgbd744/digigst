package com.ey.advisory.app.data.repositories.client.gstr2;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingTcsInvoicesEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("GetGstr2aStagingTcsInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2aStagingTcsInvoicesRepository 
extends CrudRepository<GetGstr2aStagingTcsInvoicesEntity, Long> {

	@Modifying
	@Query("UPDATE GetGstr2aStagingTcsInvoicesEntity b SET b.isDelete = true ,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn   WHERE"
			+ " b.cgstin = :cgstin AND b.retPeriod = :retPeriod")
	void softlyDeleteByGstnRetPeriod(@Param("cgstin") String cgstin,
			@Param("retPeriod") String retPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);
}
