package com.ey.advisory.app.data.repositories.client.gstr2;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingImpgSezHeaderEntity;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Repository("GetGstr2aStagingImpgSezRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2aStagingImpgSezRepository 
extends CrudRepository<GetGstr2aStagingImpgSezHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE GetGstr2aStagingImpgSezHeaderEntity b SET b.isDelete = true ,b.modifiedBy = 'SYSTEM' ,b.modifiedOn =:modifiedOn   WHERE"
			+ " b.gstin = :cgstin AND b.retPeriod = :retPeriod")
	void softlyDeleteByGstnRetPeriod(@Param("cgstin") String cgstin, @Param("retPeriod") String retPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("SELECT COUNT(*) FROM GetGstr2aStagingImpgSezHeaderEntity "
			+ "WHERE gstin=:gstin AND retPeriod = :taxperiod and isDelete =false")
	public int gstinCount(@Param("gstin") String gstin, @Param("taxperiod") String taxperiod);

	@Query("from GetGstr2aStagingImpgSezHeaderEntity where gstin=:gstin and sgstin=:sgstin and portCode=:portCode and boeCreatedDate=:boeCreatedDate and boeNum=:boeNum and isDelete =false")
	List<GetGstr2aStagingImpgSezHeaderEntity> findByInvoiceKey(@Param("gstin") String gstin, @Param("sgstin") String sgstin, @Param("portCode") String portCode,
			@Param("boeCreatedDate") String boeCreatedDate, @Param("boeNum") Long boeNum);

	@Modifying
	@Query("UPDATE GetGstr2aStagingImpgSezHeaderEntity b SET b.isDelete = TRUE, b.modifiedOn=:modifiedOn WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> totalIds,@Param("modifiedOn") LocalDateTime modifiedOn);
}
