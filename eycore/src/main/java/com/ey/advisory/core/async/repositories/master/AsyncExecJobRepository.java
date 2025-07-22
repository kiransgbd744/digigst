package com.ey.advisory.core.async.repositories.master;

import java.util.Date;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.async.JobsSearchParams;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;

@Repository("asyncExecJobRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface AsyncExecJobRepository
		extends JpaRepository<AsyncExecJob, Long>,
		JpaSpecificationExecutor<AsyncExecJob> {

	@Query("from AsyncExecJob job where job.status in :status "
			+ "and (job.scheduledTime <=:scheduledTime or job.scheduledTime is null)")
	public List<AsyncExecJob> findByStatusAndScheduledTime(
			@Param("status") String status,
			@Param("scheduledTime") Date scheduledTime, Pageable pageable);

	@Query("from AsyncExecJob job where job.status in :status "
			+ "AND (job.scheduledTime <=:scheduledTime or job.scheduledTime "
			+ "is null) AND job.jobCategory IN :taskTypes")
	public List<AsyncExecJob> findByStatusAndJobCategoryAndScheduledTime(
			@Param("status") String status,
			@Param("scheduledTime") Date scheduledTime,
			@Param("taskTypes") List<String> taskTypes, Pageable pageable);

	public AsyncExecJob findByJobId(Long jobId);

	@Query("from AsyncExecJob job where job.jobCategory = :category "
			+ "and job.status in :status " + "and job.groupCode = :groupCode "
			+ "and job.createdDate =:startDate "
			+ "and job.createdDate =:endDate")
	public List<AsyncExecJob> findJobsByParams(
			@Param("category") String category,
			@Param("groupCode") String groupCode,
			@Param("status") List<String> status,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("from AsyncExecJob job where job.jobCategory = :jobCategory "
			+ "and job.message = :paramJson and job.status = 'completed' ")
	public AsyncExecJob findJobCategery(@Param("paramJson") String paramJson,
			@Param("jobCategory") String jobCategory);

	public default Long countJobsBySearchParams(JobsSearchParams jobParams) {
		return count((root, criteriaQuery, criteriaBuilder) -> {

			AsyncExecJobPredicateBuilder builder = StaticContextHolder
					.getBean(AsyncExecJobPredicateBuilder.class);

			List<Predicate> predicates = builder
					.buildAsycnExecJobSearchPredicates(jobParams, root,
							criteriaBuilder);

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		});
	};

	public default Page<AsyncExecJob> findEWBJobsBySearchParams(
			JobsSearchParams jobParams, Pageable pageRequest) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			AsyncExecJobPredicateBuilder builder = StaticContextHolder
					.getBean(AsyncExecJobPredicateBuilder.class);

			List<Predicate> predicates = builder
					.buildAsycnExecJobSearchPredicates(jobParams, root,
							criteriaBuilder);

			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("createdDate")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		}, pageRequest);
	}

	@Query("SELECT job.jobId from AsyncExecJob job where job.message = :paramsJson ")
	public List<Long> findByJobId(@Param("paramsJson") String paramsJson);

	@Query("from AsyncExecJob job where job.jobId = :id ")
	public AsyncExecJob findByJobDetails(@Param("id") Long id);

	@Modifying
	@Query("update AsyncExecJob job set job.status = 'Failed',"
			+ " errReason = :errorReason where job.jobId = :jobId "
			+ "and job.status='Picked'")
	public int updateErrorMsgForPickedJobs(@Param("jobId") Long jobId,
			@Param("errorReason") String errorReason);

	@Modifying
	@Query("update AsyncExecJob job set job.jobBundle = :jobBundle"
			+ "  where job.jobId = :jobId ")
	public int updateJobBundle(@Param("jobId") Long jobId,
			@Param("jobBundle") String jobBundle);
	
	@Modifying
	@Query("update AsyncExecJob job set job.status = :status"
			+ "  where job.jobId = :jobId ")
	public int updateJobDetails(@Param("jobId") Long jobId,
			@Param("status") String status);

}
