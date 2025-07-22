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

import com.ey.advisory.app.data.entities.client.GetGstr2aImpgSezHeaderEntity;

/**
 * 
 * @author Anand3.M
 *
 */
@Repository("GetGstr2aImpgSezRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2aImpgSezRepository
		extends CrudRepository<GetGstr2aImpgSezHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE GetGstr2aImpgSezHeaderEntity b SET b.isDelete = true ,b.modifiedBy = 'SYSTEM' ,b.modifiedOn =:modifiedOn   WHERE"
			+ " b.gstin = :cgstin AND b.retPeriod = :retPeriod")
	void softlyDeleteByGstnRetPeriod(@Param("cgstin") String cgstin,
			@Param("retPeriod") String retPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("SELECT COUNT(*) FROM GetGstr2aImpgSezHeaderEntity "
			+ "WHERE gstin=:gstin AND retPeriod = :taxperiod and isDelete =false")
	public int gstinCount(@Param("gstin") String gstin,
			@Param("taxperiod") String taxperiod);

	@Query("from GetGstr2aImpgSezHeaderEntity where gstin=:gstin and sgstin=:sgstin and portCode=:portCode and boeCreatedDate=:boeCreatedDate and boeNum=:boeNum and isDelete =false")
	List<GetGstr2aImpgSezHeaderEntity> findByInvoiceKey(
			@Param("gstin") String gstin, @Param("sgstin") String sgstin,
			@Param("portCode") String portCode,
			@Param("boeCreatedDate") String boeCreatedDate,
			@Param("boeNum") Long boeNum);

	@Modifying
	@Query("UPDATE GetGstr2aImpgSezHeaderEntity b SET b.isDelete = TRUE , b.modifiedOn=:modifiedOn WHERE"
			+ " b.isDelete = FALSE AND b.id IN (:totalIds)")
	void updateSameRecords(@Param("totalIds") List<Long> totalIds,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("SELECT amdHistKey FROM GetGstr2aImpgSezHeaderEntity "
			+ "WHERE batchId =:batchId and isDelete =false")
	public List<String> findInvKeyByBatchId(@Param("batchId") Long batchId);
}
