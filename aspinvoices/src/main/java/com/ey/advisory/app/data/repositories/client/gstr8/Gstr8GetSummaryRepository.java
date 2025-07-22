package com.ey.advisory.app.data.repositories.client.gstr8;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr8.GetGstr8SummaryEntity;

@Repository("Gstr8GetSummaryRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr8GetSummaryRepository
		extends CrudRepository<GetGstr8SummaryEntity, Long> {

	@Modifying
	@Query("UPDATE GetGstr8SummaryEntity b SET b.isdelete = true "
			+ "WHERE b.isdelete = false AND "
			+ "b.gstIn =:sGstin AND b.taxperiod =:taxPeriod and b.section = :section")
	void softlyDeleteActiveRecords(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("section") String section);

	@Modifying
	@Query("UPDATE GetGstr8SummaryEntity b SET b.isdelete = true "
			+ "WHERE b.isdelete = false AND "
			+ "b.gstIn =:sGstin AND b.taxperiod =:taxPeriod")
	void softlyDeleteActiveRecords(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);
}
