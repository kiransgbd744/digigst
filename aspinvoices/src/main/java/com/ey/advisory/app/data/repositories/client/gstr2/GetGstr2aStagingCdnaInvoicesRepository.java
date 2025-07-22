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

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnaInvoicesHeaderEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("GetGstr2aStagingCdnaInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2aStagingCdnaInvoicesRepository  
extends CrudRepository<GetGstr2aStagingCdnaInvoicesHeaderEntity, Long> {

    @Query("SELECT COUNT(*) FROM GetGstr2aStagingCdnaInvoicesHeaderEntity "
            + "WHERE countergstin=:gstin AND taxPeriod = :taxperiod and isDelete =false ")
    public int gstinCount(@Param("gstin") String gstin,
            @Param("taxperiod") String taxperiod);

    // Curently isDelete is not added in table
    @Modifying
    @Query("UPDATE GetGstr2aStagingCdnaInvoicesHeaderEntity b SET b.isDelete = true WHERE"
            + " b.isDelete = FALSE AND b.countergstin = :cgstin AND b.taxPeriod = :returnPeriod")
    void softlyDeleteCdnaHeader(@Param("cgstin") String cgstin,
            @Param("returnPeriod") String returnPeriod);
    
    @Query("from GetGstr2aStagingCdnaInvoicesHeaderEntity where gstin=:sgstin and countergstin=:cgstin and credDebRefVoucherDate=:invDate and credDebRefVoucherNum=:invNum and credDebRefVoucher=:invType")
	List<GetGstr2aStagingCdnaInvoicesHeaderEntity> findByInvoiceKey(@Param("sgstin") String gstin,@Param("cgstin") String countergstin,
			@Param("invDate") LocalDate credDebRefVoucherDate, @Param("invNum") String credDebRefVoucherNum,
			@Param("invType") String credDebRefVoucher);

	@Modifying
	@Query("UPDATE GetGstr2aStagingCdnaInvoicesHeaderEntity b SET b.isDelete = true, b.modifiedOn=:modifiedOn WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> totalIds,@Param("modifiedOn") LocalDateTime modifiedOn);

}
