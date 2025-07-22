package com.ey.advisory.app.data.repositories.client.gstr2;

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

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2bInvoicesHeaderEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("GetGstr2StagingB2bInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2StagingB2bInvoicesRepository
		extends CrudRepository<GetGstr2aStagingB2bInvoicesHeaderEntity, Long> {

	// Curently isDelete is not added in table
	@Modifying
	@Query("UPDATE GetGstr2aStagingB2bInvoicesHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.cgstin = :cgstin AND b.returnPeriod = :returnPeriod")

	void softlyDeleteB2bHeader(@Param("cgstin") String cgstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("from GetGstr2aStagingB2bInvoicesHeaderEntity where sgstin=:sgstin and cgstin=:cgstin and invDate=:invDate and invNum=:invNum and invType=:invType")
	List<GetGstr2aStagingB2bInvoicesHeaderEntity> findByInvoiceKey(
			@Param("sgstin") String sgstin, @Param("cgstin") String cgstin,
			@Param("invDate") LocalDate invDate, @Param("invNum") String invNum,
			@Param("invType") String invType);

	@Query("SELECT COUNT(*) FROM GetGstr2aStagingB2bInvoicesHeaderEntity "
			+ "WHERE cgstin=:gstin AND returnPeriod = :taxperiod and isDelete = false")
	public int gstinCount(@Param("gstin") String gstin,
			@Param("taxperiod") String taxperiod);

	@Modifying
	@Query("UPDATE GetGstr2aStagingB2bInvoicesHeaderEntity b SET b.isDelete = true, b.modifiedOn=:modifiedOn WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> totalIds,
			@Param("modifiedOn") LocalDateTime modifiedOn);
}
