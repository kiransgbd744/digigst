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

import com.ey.advisory.app.data.entities.simplified.client.GetAnx2ItcSummaryInvoicesEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("GetAnx2ItcSumryInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetAnx2ItcSumryInvoicesRepository
		extends CrudRepository<GetAnx2ItcSummaryInvoicesEntity, Long> {

	@Modifying
	@Query("UPDATE GetAnx2ItcSummaryInvoicesEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = false AND b.cgstin = :cgstin AND b.returnPeriod ="
			+ " :returnPeriod")
	void softlyDeleteItcSumryHeader(@Param("cgstin") String cgstin,
			@Param("returnPeriod") String returnPeriod);
}
