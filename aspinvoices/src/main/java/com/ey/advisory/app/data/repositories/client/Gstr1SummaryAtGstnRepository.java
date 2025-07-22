package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1SummaryEntity;
/**
 * 
 * @author Hemasundar.J
 *
 */
@Repository("Gstr1SummaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1SummaryAtGstnRepository extends 
CrudRepository<GetGstr1SummaryEntity, Long> {

	@Query("select e FROM Gstr1SummaryDocIssuedEntity e WHERE "
			+ "e.gstin = :gstin AND e.retPeriod = :retPeriod")
	List<GetGstr1SummaryEntity> getDocIssuedSummaryForRI(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod);
	
	@Query("select e FROM Gstr1SummaryNilEntity e WHERE "
			+ "e.gstin = :gstin AND e.retPeriod = :retPeriod")
	List<GetGstr1SummaryEntity> getNilSummaryForRI(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod);
	
	@Query("select e FROM Gstr1SummaryRateEntity e WHERE "
			+ "e.gstin = :gstin AND e.retPeriod = :retPeriod")
	List<GetGstr1SummaryEntity> getRateSummaryForRI(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod);
	
	@Modifying
	@Query("UPDATE Gstr1SummaryNilEntity b SET b.isDelete = true, modifiedOn = "
			+ ":modifiedOn, modifiedBy = 'SYSTEM' WHERE b.isDelete = false AND "
			+ "b.gstin =:sGstin AND b.retPeriod =:taxPeriod")
	void softlyDeleteNilSumry(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	@Modifying
	@Query("UPDATE Gstr1SummaryDocIssuedEntity b SET b.isDelete = true, "
			+ "modifiedOn = :modifiedOn, modifiedBy = 'SYSTEM' WHERE b.isDelete = "
			+ "false AND b.gstin =:sGstin AND b.retPeriod =:taxPeriod")
	void softlyDeleteDocIssuedSumry(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	@Modifying
	@Query("UPDATE Gstr1SummaryRateEntity b SET b.isDelete = true, modifiedOn = "
			+ ":modifiedOn, modifiedBy = 'SYSTEM' WHERE b.isDelete = false AND "
			+ "b.gstin =:sGstin AND b.retPeriod =:taxPeriod")
	void softlyDeleteRateSumry(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	
	@Modifying
	@Query("UPDATE Gstr1ASummaryNilEntity b SET b.isDelete = true, modifiedOn = "
			+ ":modifiedOn, modifiedBy = 'SYSTEM' WHERE b.isDelete = false AND "
			+ "b.gstin =:sGstin AND b.retPeriod =:taxPeriod")
	void softlyDeleteGstr1ANilSumry(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	@Modifying
	@Query("UPDATE Gstr1ASummaryDocIssuedEntity b SET b.isDelete = true, "
			+ "modifiedOn = :modifiedOn, modifiedBy = 'SYSTEM' WHERE b.isDelete = "
			+ "false AND b.gstin =:sGstin AND b.retPeriod =:taxPeriod")
	void softlyDeleteGstr1ADocIssuedSumry(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	
	@Modifying
	@Query("UPDATE Gstr1ASummaryRateEntity b SET b.isDelete = true, modifiedOn = "
			+ ":modifiedOn, modifiedBy = 'SYSTEM' WHERE b.isDelete = false AND "
			+ "b.gstin =:sGstin AND b.retPeriod =:taxPeriod")
	void softlyDeleteGstr1ARateSumry(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);
	

}
