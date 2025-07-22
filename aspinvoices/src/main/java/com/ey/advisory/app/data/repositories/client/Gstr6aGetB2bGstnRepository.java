package com.ey.advisory.app.data.repositories.client;

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

import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aB2bHeaderEntity;

/**
 * 
 * @author Anand3.M
 *
 */
@Repository("Gstr6aGetB2bGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr6aGetB2bGstnRepository extends CrudRepository<GetGstr6aB2bHeaderEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr6aB2bHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.taxPeriod =:taxPeriod")
	void softlyDeleteB2bHeader(@Param("sGstin") String sGstin, @Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	@Query("SELECT distinct erpBatchId FROM GetGstr6aB2bHeaderEntity WHERE "
			+ "ctin=:ctin AND batchId=:batchId "
			+ "AND taxPeriod=:taxPeriod and deltaInStatus IN('N','M','D')")
	public List<Long> getErpBatchIds(@Param("ctin") String ctin,
			@Param("taxPeriod") String taxPeriod,
			@Param("batchId") Long batchId);
	
	@Modifying
	@Transactional
	@Query("UPDATE GetGstr6aB2bHeaderEntity SET "
			+ "sentToErpDate=:sentToErpDate,isSentToErp=true WHERE "
			+ "ctin=:ctin AND batchId=:batchId "
			+ "AND taxPeriod=:taxPeriod AND erpBatchId=:erpBatchId ")
	public void updateSentErp(@Param("ctin") String ctin,
			@Param("batchId") Long batchId,
			@Param("taxPeriod") String taxPeriod,
			@Param("sentToErpDate") LocalDate date,
			@Param("erpBatchId") Long erpBatchId);


}
