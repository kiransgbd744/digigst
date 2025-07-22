/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr2ReconResultRespPsdEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr2ReconResultRespPsdRepository")
@Transactional(value = "clientTransactionManager", 
propagation = Propagation.REQUIRED)
public interface Gstr2ReconResultRespPsdRepository
		extends JpaRepository<Gstr2ReconResultRespPsdEntity, Long>,
		JpaSpecificationExecutor<Gstr2ReconResultRespPsdEntity> {

	
	@Modifying
	@Query("UPDATE Gstr2ReconResultRespPsdEntity SET " + "endDtm =:endDtm "
			+ " WHERE  iD2A =:iD2A")
	public int updateTimeStampId2A(@Param("iD2A") Long iDPR,
			@Param("endDtm") LocalDateTime endDtm);

	@Modifying
	@Query("UPDATE Gstr2ReconResultRespPsdEntity SET " + "endDtm =:endDtm "
			+ " WHERE  iDPR =:iDPR")
	public int updateTimeStampIdPR(@Param("iDPR") Long iDPR,
			@Param("endDtm") LocalDateTime endDtm);
	
	
	@Query("select e.invoicekeyPR,fMResponse,rspTaxPeriod3B from "
			+ "Gstr2ReconResultRespPsdEntity e where e.invoicekeyPR in(:keys) "
			+ "and e.endDtm IS NULL and  e.rGSTINPR IS NOT NULL")
	public List<Object[]> getDataByDocKey(@Param("keys") List<String> keys);
	
	@Query("select e.invoicekeyPR from Gstr2ReconResultRespPsdEntity e where e.invoicekeyPR =:key "
			+ "and e.endDtm IS NULL and  e.rGSTINPR IS NOT NULL "
			+ "and (UPPER(e.fMResponse) in('LOCK','LOCK2') or e.rspTaxPeriod3B is not null)")
	public String islocked(@Param("key") String key);
	
}
