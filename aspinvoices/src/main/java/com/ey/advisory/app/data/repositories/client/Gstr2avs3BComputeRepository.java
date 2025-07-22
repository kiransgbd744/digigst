package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr2avs3BComputeEntity;

/**
 * 
 * @author Sasidhar *
 */

@Repository("Gstr2avs3BComputeRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2avs3BComputeRepository
        extends CrudRepository<Gstr2avs3BComputeEntity, Long> {

    @Query("SELECT COUNT(*) FROM Gstr2avs3BComputeEntity "
            + "WHERE gstin=:gstin AND taxPeriod BETWEEN :taxPeriodFrom AND :taxPeriodTo ")
    public int gstinCount(@Param("gstin") String gstin,
            @Param("taxPeriodFrom") String taxPeriodFrom,
            @Param("taxPeriodTo") String taxPeriodTo);

}
