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

import com.ey.advisory.app.data.entities.client.Table4d1ValidationStatusEntity;



@Repository("Table4d1ValidationStatusRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface Table4d1ValidationStatusRepository
		extends JpaRepository<Table4d1ValidationStatusEntity, Long>,
		JpaSpecificationExecutor<Table4d1ValidationStatusEntity> {
	
	@Query("Select A from Table4d1ValidationStatusEntity A where A.gstin = :gstin AND taxPeriod =:taxPeriod and isActive = true")
	List<Table4d1ValidationStatusEntity> findByGstnAndTaxperiod(
			@Param("gstin") String gstin,@Param("taxPeriod") String taxPeriod);
	
	@Modifying
	@Query("Update Table4d1ValidationStatusEntity SET isActive = false, updatedOn = :updatedOn, updatedBy = :updatedBy WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin  AND "
			+ "isActive = true")
	public int updateActiveFlag(@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			 @Param("updatedOn") LocalDateTime updatedOn, @Param("updatedBy") String updatedBy);
	
	
	public Table4d1ValidationStatusEntity findByGstinAndTaxPeriod(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod);
	
	

	@Query("SELECT e FROM Table4d1ValidationStatusEntity e WHERE gstin = :gstin AND taxPeriod= :taxPeriod AND isActive= true")
	public Table4d1ValidationStatusEntity findByGstinAndTaxPeriodAndIsActive(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod);

}
