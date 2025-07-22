package com.ey.advisory.app.data.repositories.client.gstr2;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingAmdhistHeaderEntity;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Repository("GetGstr2aStagingAmdhistRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2aStagingAmdhistRepository 
	extends CrudRepository<GetGstr2aStagingAmdhistHeaderEntity, Long> {

    @Modifying
    @Query("UPDATE GetGstr2aStagingAmdhistHeaderEntity b SET b.isDelete = true ,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn   WHERE"
            + " b.gstin = :cgstin AND b.retPeriod = :retPeriod")
    void softlyDeleteByGstnRetPeriod(@Param("cgstin") String cgstin,
            @Param("retPeriod") String retPeriod,
            @Param("modifiedOn") LocalDateTime modifiedOn);

    @Query("SELECT COUNT(*) FROM GetGstr2aStagingAmdhistHeaderEntity "
            + "WHERE gstin=:gstin AND retPeriod = :taxperiod and isDelete =false")
    public int gstinCount(@Param("gstin") String gstin,
            @Param("taxperiod") String taxperiod);
    
    @Modifying
    @Query("UPDATE GetGstr2aStagingAmdhistHeaderEntity b SET b.isDelete = true ,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn   WHERE"
            + " b.batchId NOT IN :batchId AND b.isDelete = false AND b.parentSection =:parentSection")
    void softlyDeleteByBatchIdNotInAndParentSection(@Param("batchId") Long batchId,
    		@Param("parentSection") String parentSection,
            @Param("modifiedOn") LocalDateTime modifiedOn);
}
