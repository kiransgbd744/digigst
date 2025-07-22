/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr2BReconResultRespPsdEntity;

/**
 * @author Siva Krishna
 *
 */

@Repository("Gstr2BReconResultRespPsdRepository")
@Transactional(value = "clientTransactionManager", 
propagation = Propagation.REQUIRED)
public interface Gstr2BReconResultRespPsdRepository
		extends JpaRepository<Gstr2BReconResultRespPsdEntity, Long>,
		JpaSpecificationExecutor<Gstr2BReconResultRespPsdEntity> {

	@Query("select e.invoicekeyPR,fMResponse,rspTaxPeriod3B from "
			+ "Gstr2BReconResultRespPsdEntity e where e.invoicekeyPR in(:keys) "
			+ "and e.endDtm IS NULL and  e.rGSTINPR IS NOT NULL")
	public List<Object[]> getDataByDocKey(@Param("keys") List<String> keys);
	
	@Query("select e.invoicekeyPR from Gstr2BReconResultRespPsdEntity e where e.invoicekeyPR =:key "
			+ "and e.endDtm IS NULL and  e.rGSTINPR IS NOT NULL "
			+ "and (UPPER(e.fMResponse) in('LOCK','LOCK2') or e.rspTaxPeriod3B IS NOT NULL)")
	public String islocked(@Param("key") String key);
}
