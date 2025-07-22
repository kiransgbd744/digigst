package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1vs3BComputeEntity;

/**
 * 
 * @author Sasidhar
 *
 */

@Repository("Gstr1vs3BComputeRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1vs3BComputeRepository
        extends CrudRepository<Gstr1vs3BComputeEntity, Long> {

    @Query("SELECT COUNT(*) FROM Gstr1vs3BComputeEntity "
            + "WHERE gstin=:gstin AND derivedRetPeriod BETWEEN :taxPeriodFrom AND :taxPeriodTo ")
    public int gstinCount(@Param("gstin") String gstin,
            @Param("taxPeriodFrom") Integer taxPeriodFrom,
            @Param("taxPeriodTo") Integer taxPeriodTo);

}
