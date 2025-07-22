package com.ey.advisory.admin.data.repositories.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.RateMasterEntityClient;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("RateRepository")
public interface RateRepository extends JpaRepository<RateMasterEntityClient, Long>,
		JpaSpecificationExecutor<RateMasterEntityClient> {
	
	@Query("SELECT count(g.stateCode) FROM StateCodeInfoEntity g"
			+ " WHERE g.stateCode = :r")
	public int findRateCode(@Param("r") String r);
	
	@Query("SELECT g FROM RateMasterEntity g")
	public List<RateMasterEntityClient> findAll();
	
	@Query("SELECT count(e) FROM RateMasterEntity e WHERE e.igst=:igst")
	int findByIgst(@Param("igst") BigDecimal igst);
	
	@Query("SELECT count(e) FROM RateMasterEntity e WHERE e.cgst=:cgst")
	int findByCgst(@Param("cgst") BigDecimal cgst);
	
	@Query("SELECT count(e) FROM RateMasterEntity e WHERE e.sgst=:sgst")
	int findBySgst(@Param("sgst") BigDecimal sgst);

}
