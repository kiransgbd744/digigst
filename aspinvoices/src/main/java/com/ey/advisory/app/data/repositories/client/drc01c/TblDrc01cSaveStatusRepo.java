package com.ey.advisory.app.data.repositories.client.drc01c;

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

import com.ey.advisory.app.data.entities.drc01c.TblDrc01cSaveStatus;


@Repository("TblDrc01cSaveStatusRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TblDrc01cSaveStatusRepo
		extends JpaRepository<TblDrc01cSaveStatus, Long>,
		JpaSpecificationExecutor<TblDrc01cSaveStatus> {
	


	public List<TblDrc01cSaveStatus> findByGstinInAndTaxPeriodAndIsActiveTrue(List<String> gstins,
			String taxperiod);
	
	public TblDrc01cSaveStatus findByGstinAndTaxPeriodAndIsActiveTrue(String gstin,
			String taxperiod);
	
	@Query("SELECT e FROM TblDrc01cSaveStatus e WHERE gstin = :gstin AND taxPeriod= :taxPeriod AND createdOn= :createdOn")
	public TblDrc01cSaveStatus findByGstinAndTaxPeriodAndCreatedOn(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("createdOn") LocalDateTime createdOn);
	

	@Query("select count(e) from TblDrc01cSaveStatus e where"
			+ " gstin = :gstin and taxPeriod= :taxPeriod and status = :status")
	public Long findByGstinAndTaxPeriodAndStatus(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("status") String status);
	
	@Modifying
	@Query("update TblDrc01cSaveStatus set status =:status  where  id =:id ")
	void updateStatus(@Param("status") String status, @Param("id") Long id);
	
	@Modifying
	@Query("update TblDrc01cSaveStatus set status =:status, "
			+ "saveResponsePayload =:saveResponsePayload  where  id =:id ")
	void updateStatus(@Param("status") String status, @Param("id") Long id,
			@Param("saveResponsePayload") String saveResponsePayload);

	@Modifying
	@Query("Update TblDrc01cSaveStatus SET isActive = false  WHERE "
			+ "taxPeriod =:taxPeriod AND gstin =:gstin")
		void updateActiveFlag(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin);


	
}