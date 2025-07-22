package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2bHeaderEntity;

@Repository("GetGstr6aStagingB2bHeaderRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr6aStagingB2bHeaderRepository
		extends CrudRepository<GetGstr6aStagingB2bHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE GetGstr6aStagingB2bHeaderEntity b SET b.isDelete = true,"
			+ " b.modifiedBy = 'SYSTEM' , b.modifiedOn =:modifiedOn WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.taxPeriod =:taxPeriod")
	void softlyDeleteB2bHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	@Query("from GetGstr6aStagingB2bHeaderEntity where gstin=:sgstin and ctin=:cgstin and invDate=:invDate and invNum=:invNum and invType=:invType")
	List<GetGstr6aStagingB2bHeaderEntity> findByInvoiceKey(
			@Param("sgstin") String sgstin, @Param("cgstin") String cgstin,
			@Param("invDate") LocalDate invDate, @Param("invNum") String invNum,
			@Param("invType") String invType);

	@Modifying
	@Query("UPDATE GetGstr6aStagingB2bHeaderEntity b SET b.isDelete = true, b.modifiedOn=:modifiedOn WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> totalIds,
			@Param("modifiedOn") LocalDateTime modifiedOn);
}
