package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.service.ims.ImsSaveJobQueueEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("ImsSaveJobQueueRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ImsSaveJobQueueRepository
		extends JpaRepository<ImsSaveJobQueueEntity, Long>,
		JpaSpecificationExecutor<ImsSaveJobQueueEntity> {
	
	@Query("select e.section from ImsSaveJobQueueEntity e WHERE "
			+ " e.isActive = true AND e.gstin=:gstin AND e.status In (:status) AND e.action=:action")
	public List<String> findData(@Param("gstin") String gstin,
			@Param("status") List<String> status, @Param("action") String action);
	
	@Query("select e from ImsSaveJobQueueEntity e WHERE "
			+ " e.status In (:status)")
	public List<ImsSaveJobQueueEntity> findAllActiveData(@Param("status") List<String> status);
	
	@Modifying
	@Query("Update ImsSaveJobQueueEntity SET status=:status, updatedOn = CURRENT_TIMESTAMP, "
			+ " isActive = false, saveResponse=:saveResponse WHERE gstin=:gstin AND section=:section "
			+ " AND status = 'InProgress' AND action =:action")
	int updateInActiveStatus(@Param("status") String status, @Param("gstin") String gstin,
			@Param("section") String section,@Param("saveResponse") String saveResponse,  @Param("action") String action);

	@Modifying
	@Query("Update ImsSaveJobQueueEntity SET status=:status, updatedOn = CURRENT_TIMESTAMP, "
			+ " saveResponse=:saveResponse WHERE gstin=:gstin AND section=:section "
			+ "  AND status = 'InProgress' AND action =:action ")
	int updateStatusBasedonGstin(@Param("status") String status, @Param("gstin") String gstin,
			@Param("section") String section,@Param("saveResponse") String saveResponse, @Param("action") String action);
	
	@Query("select e from ImsSaveJobQueueEntity e WHERE "
			+ " e.isActive = true AND e.gstin=:gstin ")
	public List<ImsSaveJobQueueEntity> findAllData(@Param("gstin") String gstin);
	
	@Modifying
	@Query("Update ImsSaveJobQueueEntity SET status=:status, jobPostDateTime = CURRENT_TIMESTAMP, "
			+ " updatedOn = CURRENT_TIMESTAMP WHERE gstin=:gstin AND section=:section AND isActive = true AND action=:action"
			+ " AND status = 'In Queue'  ")
	int updateInProgressStatus(@Param("status") String status, @Param("gstin") String gstin,
			@Param("section") String section, @Param("action") String action);
	
	
	@Modifying
	@Query("Update ImsSaveJobQueueEntity SET batchId=:batchId "
			+ " WHERE gstin=:gstin AND section=:section "
			+ " AND status = 'InProgress' AND action=:action")
	int updateBatchId(@Param("gstin") String gstin,
			@Param("section") String section,@Param("batchId") String batchId, @Param("action") String action);
	
	@Modifying
	@Query("Update ImsSaveJobQueueEntity SET status=:status, updatedOn = CURRENT_TIMESTAMP "
			+ " WHERE batchId=:batchId ")
	int updateStatus(@Param("status") String status, @Param("batchId") String batchId);
	
	@Query("select DISTINCT gstin from ImsSaveJobQueueEntity  WHERE "
			+ " status In (:status) AND gstin In (:gstin)")
	public List<String> findInProgressGstins(@Param("status") List<String> status,
			@Param("gstin") List<String> gstin);
	
	@Query("SELECT e.gstin, COUNT(e) FROM ImsSaveJobQueueEntity e WHERE "
		     + "e.isActive = true AND e.gstin IN (:gstins) AND e.status IN (:status) "
		     + "GROUP BY e.gstin")
		public List<Object[]> findCountsByGstinAndStatus(
		    @Param("gstins") List<String> gstins, 
		    @Param("status") List<String> status);
		
	@Query("select e.section from ImsSaveJobQueueEntity e WHERE "
			+ "  e.gstin=:gstin AND e.status In (:status) AND e.action=:action")
	public List<String> findInprogressData(@Param("gstin") String gstin,
			@Param("status") List<String> status,
			@Param("action") String action);
	
	@Modifying
	@Query("Update ImsSaveJobQueueEntity SET status=:status, updatedOn = CURRENT_TIMESTAMP , isActive = false "
			+ " WHERE gstin=:gstin AND status = 'In Queue' ")
	int updateStatusDowntime(@Param("status") String status, @Param("gstin") String gstin);


}
