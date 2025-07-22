package com.ey.advisory.app.data.repositories.client.gstr9;

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

import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeReadyStatusFYEntity;

/**
 * @author Hema G M
 *
 */
@Repository("Gstr9ComputeReadyStatusFYRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr9ComputeReadyStatusFYRepository
		extends JpaRepository<Gstr9ComputeReadyStatusFYEntity, Long>,
		JpaSpecificationExecutor<Gstr9ComputeReadyStatusFYEntity> {

	Gstr9ComputeReadyStatusFYEntity findByGstinAndFy(String gstin, int fy);

	@Query("select gstin from Gstr9ComputeReadyStatusFYEntity where fy = :fy")
	List<String> findGstinsByFy(@Param("fy") Integer fy);

	@Modifying
	@Query("update Gstr9ComputeReadyStatusFYEntity set isGstr1GetCompleted = :gstr1Completed, "
			+ " updatedOn = :updatedOn "
			+ " where gstin in (:gstins) and fy = :fy and isGstr1GetCompleted = false")
	int updateGstr1GetStatus(@Param("gstins") List<String> gstins,
			@Param("fy") Integer fy,
			@Param("gstr1Completed") boolean gstr1Completed,
			@Param("updatedOn") LocalDateTime updatedOn);

	@Modifying
	@Query("update Gstr9ComputeReadyStatusFYEntity set isGstr1AmdGetCompleted = :gstr1AmdCompleted, "
			+ " updatedOn = :updatedOn "
			+ " where gstin in (:gstins) and fy = :fy and isGstr1AmdGetCompleted = false")
	int updateGstr1AmdGetStatus(@Param("gstins") List<String> gstins,
			@Param("fy") Integer fy,
			@Param("gstr1AmdCompleted") boolean gstr1AmdCompleted,
			@Param("updatedOn") LocalDateTime updatedOn);

	@Modifying
	@Query("update Gstr9ComputeReadyStatusFYEntity set isGstr3bGetCompleted = :gstr3bCompleted, "
			+ " updatedOn = :updatedOn "
			+ " where gstin in (:gstins) and fy = :fy and isGstr3bGetCompleted = false")
	int updateGstr3BGetStatus(@Param("gstins") List<String> gstins,
			@Param("fy") Integer fy,
			@Param("gstr3bCompleted") boolean gstr3bCompleted,
			@Param("updatedOn") LocalDateTime updatedOn);

	@Query("select gstin from Gstr9ComputeReadyStatusFYEntity where "
			+ "(isGstr1GetCompleted = false or isGstr3bGetCompleted = false) "
			+ " and fy =:fy ")
	List<String> findEligibleRecords(@Param("fy") Integer fy);

	@Modifying
	@Query("update Gstr9ComputeReadyStatusFYEntity set isGstr1GetCompleted = true, "
			+ "isGstr3bGetCompleted = true, updatedOn = :updatedOn where "
			+ "gstin = :gstin and fy = :fy ")
	int updateStatus(@Param("gstin") String gstin, @Param("fy") Integer fy,
			@Param("updatedOn") LocalDateTime updatedOn);

}
