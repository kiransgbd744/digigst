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

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdaInvoicesHeaderEntity;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Repository("GetGstr2aStagingIsdaInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2aStagingIsdaInvoicesRepository 
extends CrudRepository<GetGstr2aStagingIsdaInvoicesHeaderEntity, Long> {

    // Curently isDelete is not added in table
    @Modifying
    @Query("UPDATE GetGstr2aStagingIsdaInvoicesHeaderEntity b SET b.isDelete = true ,"
            + "b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
            + " b.isDelete = FALSE AND b.gstin = :gstin AND "
            + "b.returnPeriod = :returnPeriod")
    void softlyDeleteIsdaHeader(@Param("gstin") String gstin,
            @Param("returnPeriod") String returnPeriod,
            @Param("modifiedOn") LocalDateTime modifiedOn);

    @Query("SELECT COUNT(*) FROM GetGstr2aStagingIsdaInvoicesHeaderEntity "
            + "WHERE cgstin=:gstin AND returnPeriod = :taxperiod and isDelete =false")
    public int gstinCount(@Param("gstin") String gstin,
            @Param("taxperiod") String taxperiod);

    @Query("from GetGstr2aStagingIsdaInvoicesHeaderEntity where gstin=:sgstin and cgstin=:cgstin and documentDate=:invDate and documentNumber=:invNum and isdDocumentType=:invType")
    List<GetGstr2aStagingIsdaInvoicesHeaderEntity> findByInvoiceKey(
            @Param("sgstin") String gstin, @Param("cgstin") String cgstin,
            @Param("invDate") LocalDate documentDate,
            @Param("invNum") String documentNumber,
            @Param("invType") String isdDocumentType);

    @Modifying
    @Query("UPDATE GetGstr2aStagingIsdaInvoicesHeaderEntity b SET b.isDelete = true, b.modifiedOn=:modifiedOn WHERE"
            + " b.isDelete = FALSE AND b.id IN (:totalIds)")
    void updateSameRecords(@Param("totalIds") List<Long> totalIds,@Param("modifiedOn") LocalDateTime modifiedOn);

}