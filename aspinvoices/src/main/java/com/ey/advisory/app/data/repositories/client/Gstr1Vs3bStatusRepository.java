package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1vs3bStatusEntity;

@Repository("Gstr1Vs3bStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1Vs3bStatusRepository
        extends CrudRepository<Gstr1vs3bStatusEntity, Long> {

    @Modifying
    @Query("UPDATE Gstr1vs3bStatusEntity SET isDelete=true "
            + " WHERE gstin=:gstin AND "
            + "deriverdRetPeriodFrom=:derivedRetPerFrom and "
            + "deriverdRetPeriodTo=:derivedRetPerTo AND isDelete=false ")
    void gstr1vs3bInActiveUpdate(@Param("gstin") String gstin,
            @Param("derivedRetPerFrom") Integer derivedRetPerFrom,
            @Param("derivedRetPerTo") Integer derivedRetPerTo);

    @Query("SELECT COUNT(*) FROM Gstr1vs3bStatusEntity "
            + "WHERE gstin=:gstin AND deriverdRetPeriodFrom=:derivedRetPerFrom and "
            + "deriverdRetPeriodTo=:derivedRetPerTo  ")
    public int gstinCount(@Param("gstin") String gstin,
            @Param("derivedRetPerFrom") Integer derivedRetPerFrom,
            @Param("derivedRetPerTo") Integer derivedRetPerTo);

    @Query("select e.status,MAX(e.createdOn),e.gstin FROM Gstr1vs3bStatusEntity e WHERE e.gstin = :gstin and "
            + "e.deriverdRetPeriodFrom=:derivedRetPerFrom and "
            + "e.deriverdRetPeriodTo=:derivedRetPerTo AND e.isDelete=false "
            + " group by e.status,e.createdOn,e.gstin order by 1 desc ")
    public List<Object[]> findStatusByGstin(@Param("gstin") String gstin,
            @Param("derivedRetPerFrom") Integer derivedRetPerFrom,
            @Param("derivedRetPerTo") Integer derivedRetPerTo);
    
    @Query(" select gstin from Gstr1vs3bStatusEntity where configId =:configId")
    public List<String> getAllGstinsByConfigId(@Param("configId") Long configid);
    
    @Modifying
    @Query(" update Gstr1vs3bStatusEntity set status =:status where configId =:configId and gstin =:gstin")
    public void updateGstinStatus (@Param("status") String status, @Param("configId") Long configId, @Param("gstin") String gstin);
    
}
