package com.ey.advisory.app.data.repositories.client.gstr9;

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

import com.ey.advisory.app.data.entities.gstr9.Gstr9PeriodWiseEntity;

/**
 * @author Arun.KA
 *
 */

@Repository("Gstr9PeriodWiseRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr9PeriodWiseRepository
		extends CrudRepository<Gstr9PeriodWiseEntity, Long> {

	@Query("select gstin from Gstr9PeriodWiseEntity where "
			+ " isGstr1GetCompleted = true and fy =:fy group by gstin,fy "
			+ " having count(*) = 12")
	List<String> findGstr1EligibleForFY(@Param("fy") Integer fy);

	@Query("select gstin from Gstr9PeriodWiseEntity where "
			+ " isGstr1GetCompleted = true and derivedReturnPeriod >= :derivedAmdStPeriod and "
			+ " derivedReturnPeriod <= :derviedAmdLtPeriod group by gstin"
			+ " having count(*) = 8")
	List<String> findGstr1EligibleForAmd(
			@Param("derivedAmdStPeriod") Integer derivedAmdStPeriod,
			@Param("derviedAmdLtPeriod") Integer derviedAmdLtPeriod);

	@Query("select gstin from Gstr9PeriodWiseEntity where "
			+ " isGstr3BGetCompleted = true and fy =:fy group by gstin,fy "
			+ " having count(*) = 12")
	List<String> findGstr3BEligibleForFY(@Param("fy") Integer fy);

	@Modifying
	@Query("update Gstr9PeriodWiseEntity set isGstr1GetCompleted = :gstr1Completed, "
			+ " updatedOn = :updatedOn, Gstr1GetDt = :Gstr1GetDt  "
			+ " where gstin = :gstin and returnPeriod = :returnPeriod ")
	int updateGstr1GetStatus(@Param("gstin") String gstin,
			@Param("returnPeriod") String taxPeriod,
			@Param("gstr1Completed") boolean gstr1Completed,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("Gstr1GetDt") LocalDate Gstr1GetDt);

	@Modifying
	@Query("update Gstr9PeriodWiseEntity set isGstr3BGetCompleted = :gstr3bCompleted, "
			+ " updatedOn = :updatedOn, Gstr3BGetDt = :Gstr3BGetDt "
			+ " where gstin = :gstin and returnPeriod = :returnPeriod ")
	int updateGstr3BGetStatus(@Param("gstin") String gstin,
			@Param("returnPeriod") String taxPeriod,
			@Param("gstr3bCompleted") boolean gstr3bCompleted,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("Gstr3BGetDt") LocalDate Gstr3BGetDt);

	@Query("select gstin,returnPeriod from Gstr9PeriodWiseEntity where"
			+ " isGstr3BGetCompleted = false and"
			+ " derivedReturnPeriod >= :derivedStPeriod and "
			+ " derivedReturnPeriod <= :derivedEndPeriod ")
	List<Object[]> findEligibleRecordsGSTR3B(
			@Param("derivedStPeriod") Integer derivedStPeriod,
			@Param("derivedEndPeriod") Integer derivedEndPeriod);

	@Query("select gstin,returnPeriod from Gstr9PeriodWiseEntity where"
			+ " isGstr1GetCompleted = false  and"
			+ " derivedReturnPeriod >= :derivedStPeriod and "
			+ " derivedReturnPeriod <= :derivedEndPeriod ")
	List<Object[]> findEligibleRecordsGSTR1(
			@Param("derivedStPeriod") Integer derivedStPeriod,
			@Param("derivedEndPeriod") Integer derivedEndPeriod);

}
