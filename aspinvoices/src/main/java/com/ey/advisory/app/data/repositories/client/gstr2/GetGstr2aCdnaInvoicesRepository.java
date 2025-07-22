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

import com.ey.advisory.app.data.entities.client.GetGstr2aCdnaInvoicesHeaderEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("GetGstr2aCdnaInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2aCdnaInvoicesRepository
        extends CrudRepository<GetGstr2aCdnaInvoicesHeaderEntity, Long> {

    // currently isDelete is not added in table
    /*
     * @Modifying
     * 
     * @Query("UPDATE GetGstr2aCdnCdnaInvoicesHeaderEntity b SET b.isDelete = true WHERE"
     * +
     * " b.isDelete = FALSE AND b.cgstin = :cgstin AND b.returnPeriod = :returnPeriod"
     * ) void softlyDeleteCdnCdnaHeader(@Param("cgstin") String cgstin,
     * 
     * @Param("returnPeriod") String returnPeriod);
     */

    @Query("SELECT COUNT(*) FROM GetGstr2aCdnaInvoicesHeaderEntity "
            + "WHERE countergstin=:gstin AND taxPeriod = :taxperiod and isDelete =false ")
    public int gstinCount(@Param("gstin") String gstin,
            @Param("taxperiod") String taxperiod);

    // Curently isDelete is not added in table
    @Modifying
    @Query("UPDATE GetGstr2aCdnaInvoicesHeaderEntity b SET b.isDelete = true WHERE"
            + " b.isDelete = FALSE AND b.countergstin = :cgstin AND b.taxPeriod = :returnPeriod")
    void softlyDeleteCdnaHeader(@Param("cgstin") String cgstin,
            @Param("returnPeriod") String returnPeriod);

}
