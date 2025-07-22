package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2aCdnInvoicesHeaderEntity;

@Repository("GetGstr2aCdnInvoicesHeaderRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public interface GetGstr2aCdnInvoicesHeaderRepository
		extends CrudRepository<GetGstr2aCdnInvoicesHeaderEntity, Long> {
	
	@Modifying
	@Transactional
	@Query("UPDATE GetGstr2aCdnInvoicesHeaderEntity SET "
			+ "sentToErpDate=:sentToErpDate,isSentToErp=true WHERE "
			+ "countergstin=:cgstin AND cdnBatchIdGstr2a=:batchId "
			+ "AND taxPeriod=:returnPeriod AND erpBatchId=:erpBatchId ")
	public void updateSentErp(@Param("cgstin") String cgstin,
			@Param("batchId") Long batchId,
			@Param("returnPeriod") String returnPeriod,
			@Param("sentToErpDate") LocalDate date,
			@Param("erpBatchId") Long erpBatchId);
	
	@Query("SELECT distinct erpBatchId FROM GetGstr2aCdnInvoicesHeaderEntity WHERE "
			+ "countergstin=:cgstin AND cdnBatchIdGstr2a=:batchId "
			+ "AND taxPeriod=:returnPeriod and deltaInStatus IN('N','M','D')")
	public List<Long> getErpBatchIds(@Param("cgstin") String cgstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("batchId") Long batchId);

}
