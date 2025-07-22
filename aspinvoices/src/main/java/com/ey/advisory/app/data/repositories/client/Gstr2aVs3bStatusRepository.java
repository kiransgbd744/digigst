package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr2avs3bStatusEntity;

@Repository("Gstr2aVs3bStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2aVs3bStatusRepository
        extends CrudRepository<Gstr2avs3bStatusEntity, Long> {

    @Modifying
    @Query("UPDATE Gstr2avs3bStatusEntity SET isDelete=true "
            + " WHERE gstin=:gstin AND "
            + "deriverdRetPeriodFrom=:derivedRetPerFrom and "
            + "deriverdRetPeriodTo=:derivedRetPerTo AND isDelete=false ")
    void gstr2avs3bInActiveUpdate(@Param("gstin") String gstin,
            @Param("derivedRetPerFrom") Integer derivedRetPerFrom,
            @Param("derivedRetPerTo") Integer derivedRetPerTo);
}
